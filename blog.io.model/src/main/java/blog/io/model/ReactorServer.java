package blog.io.model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * IO model reactor
 * 
 * @author wujianchao
 */
public class ReactorServer {

	public static Logger LOGGER = LoggerFactory.getLogger(PreactorServer.class);

	private int listenPort;

	private ServerSocketChannel serverChannel;

	private Selector selector;

	private volatile boolean isRun = false;

	public ReactorServer(int listenPort) {
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
					if (!event.isValid()) {
						return;
					}
					if (event.isAcceptable()) {
						SocketChannel channel = accept(event);
						dispatch(channel);
					}

				}

			} catch (IOException e) {
				LOGGER.error("mainloop", e);
			}
		}

	}

	private void dispatch(SocketChannel channel) throws IOException {
		ByteBuffer request = read(channel);
		ByteBuffer response = process(request);
		write(channel, response);
	}

	private void write(SocketChannel channel, ByteBuffer response) throws IOException {
		if(response.hasRemaining())
			channel.write(response);
	}

	private ByteBuffer read(SocketChannel channel) throws IOException {
		ByteBuffer request = ByteBuffer.allocate(1024);
		channel.read(request);
		return request;
	}

	private ByteBuffer process(ByteBuffer buffer) throws IOException {
		//business logic goes here
		return null;
	}

	private SocketChannel accept(SelectionKey event) throws IOException {
		SocketChannel channel = ((ServerSocketChannel) event.channel()).accept();
		channel.configureBlocking(false);
		LOGGER.info("new connection " + channel.getRemoteAddress());
		return channel;
	}

	public synchronized void close() throws IOException {
		if (serverChannel != null)
			serverChannel.close();
		if (selector != null)
			selector.close();
		isRun = false;
	}

	public static void main(String[] args) throws IOException {
		ReactorServer server = new ReactorServer(8088);
		server.start();
		server.eventLoop();
		server.close();
	}

}
