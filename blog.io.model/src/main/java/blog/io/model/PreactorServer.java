package blog.io.model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * IO model preactor
 * 
 * @author wujianchao
 */
public class PreactorServer {

	public static Logger LOGGER = LoggerFactory.getLogger(PreactorServer.class);

	private int listenPort;

	private ServerSocketChannel serverChannel;

	private Selector selector;

	private volatile boolean isRun = false;

	/**
	 * asynchronous write data if all processing is done in the eventLoop
	 * thread, there is no deed to sync "toWriteData"
	 */
	private ConcurrentHashMap<SocketChannel, List<ByteBuffer>> toWriteData = new ConcurrentHashMap<SocketChannel, List<ByteBuffer>>();

	public PreactorServer(int listenPort) {
		this.listenPort = listenPort;
	}

	public synchronized void start() throws IOException {
		if (!isRun) {
			selector = Selector.open();
			serverChannel = ServerSocketChannel.open();
			// use selector must configure channel in blocking mode
			serverChannel.configureBlocking(false);

			serverChannel.socket().bind(new InetSocketAddress(listenPort));
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			isRun = true;
			LOGGER.info("Listening " + listenPort);
		}
	}

	public void eventLoop() {
		while (isRun) {
			try {
				selector.select();
				Set<SelectionKey> events = selector.selectedKeys();

				Iterator<SelectionKey> eventItr = events.iterator();

				SelectionKey event;

				while (eventItr.hasNext()) {
					event = eventItr.next();
					eventItr.remove();
					dispatch(event);
				}

			} catch (IOException e) {
				LOGGER.error("mainloop", e);
			}
		}

	}

	private void dispatch(SelectionKey event) throws IOException {
		if (!event.isValid()) {
			return;
		}
		if (event.isAcceptable()) {
			accept(event);
		}
		if (event.isValid() && event.isReadable()) {
			// read data ==> process ==> change
			// selectionkey.interestOps=OP_WRITE
			read(event);
		}
		if (event.isValid() && event.isWritable()) {// client may
													// close
													// connection or
													// connection
													// may disturbed
													// so
													// event.isValid
													// must check
			// write ==> change selectionkey.interestOps=OP_READ
			write(event);
		}
		
	}

	private void write(SelectionKey event) throws IOException {
		SocketChannel channel = (SocketChannel) event.channel();
		LOGGER.info("Server is ready to write to " + channel.getRemoteAddress());
		List<ByteBuffer> dataList = toWriteData.get(event.channel());
		while (!dataList.isEmpty()) {
			ByteBuffer buffer = dataList.get(0);
			long startTime = System.nanoTime();
			// just copy buffer to socket buffer
			// not throw exception event though remote client is closed.
			// block when socket buffer is full or not blocking.
			int size = channel.write(buffer);
			long endTime = System.nanoTime();
			LOGGER.info(size + " bytes write to " + channel.getRemoteAddress() + ".Time " + (endTime - startTime));
			// write not completed
			if (buffer.remaining() > 0) {
				LOGGER.info("write muti-times " + channel);
				break;
			}

			dataList.remove(0);
		}
		if (dataList.isEmpty()) {// all write is done
			// select mechanism. when register OP_WRITE selector.select() will
			// not block.
			// so OP_WRITE must be registered by response method, when you want
			// to write something to remote client.
			// CUP consumption. When I also register OP_WRITE 4 core CPU 24%
			// more, when there is no request.
			// this operation is necessary for save CUP and read new request
			// data.
			channel.register(event.selector(), SelectionKey.OP_READ);
			// channel.register(event.selector(), SelectionKey.OP_READ |
			// SelectionKey.OP_WRITE);
		}
	}

	private void read(SelectionKey event) throws IOException {
		SocketChannel channel = (SocketChannel) event.channel();
		LOGGER.info("Server is ready to read from " + channel.getRemoteAddress());
		long startTime = System.nanoTime();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int size = 0;
		try {
			// TODO read not complete.
			size = channel.read(buffer);
		} catch (IOException e) {
			// remote client force closed the connection or network is
			// disturbed.
			// must cancel the selectionKey or selector may catch the event
			// again.
			// when invoke channel.close() keys associated with the channel are
			// canceled.
			channel.close();
			LOGGER.info("client force closed the connection or network is disturbed");
			return;
		}
		if (size == -1) {
			// eof. 
			// when remote client sent null
			channel.close();
			LOGGER.info("client closed");
			return;
		}
		long endTime = System.nanoTime();
		LOGGER.info(size + " bytes read from " + channel.getRemoteAddress() + ".Time " + (endTime - startTime));

		process(channel, buffer);

	}

	// TODO process
	// business logic goes here. common steps are decoding, processing, decoding
	// and response.
	// to expand the throughout, the logic better goes in other threads.
	// you can extend the software to a http server.
	private void process(SocketChannel channel, ByteBuffer buffer) throws IOException {
		LOGGER.info("decode " + channel.getRemoteAddress());
		// TODO decode
		LOGGER.info("process " + channel.getRemoteAddress());
		response(channel, buffer);
	}

	// we want to write something to remote client.
	public void response(SocketChannel channel, ByteBuffer buffer) throws IOException {
		List<ByteBuffer> tmp = toWriteData.get(channel);
		if (tmp == null) {
			tmp = new ArrayList<ByteBuffer>();
		}
		// switch buffer to read mode.
		buffer.flip();
		tmp.add(buffer);
		toWriteData.put(channel, tmp);
		// OP_READ & OP_WRITE at the same time.
		channel.register(selector, channel.keyFor(selector).interestOps() | SelectionKey.OP_WRITE);
		System.out.println(channel.keyFor(selector).interestOps());
		LOGGER.info("response " + channel.getRemoteAddress());
	}

	private void accept(SelectionKey event) throws IOException {
		SocketChannel channel = ((ServerSocketChannel) event.channel()).accept();
		// use selector must configure channel in blocking mode
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_READ);
		LOGGER.info("new connection " + channel.getRemoteAddress());
	}

	public synchronized void close() throws IOException {
		if (serverChannel != null)
			serverChannel.close();
		if (selector != null)
			selector.close();
		isRun = false;
	}

	public static void main(String[] args) throws IOException {
		PreactorServer server = new PreactorServer(8088);
		server.start();
		server.eventLoop();
		server.close();
	}

}
