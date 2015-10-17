package blog.demo.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

/**
 *
 * @author wujianchao<wuchienchao@qq.com>
 */
public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		
		Socket socket = new Socket("localhost", 8088);
		OutputStream out = socket.getOutputStream();
		
		out.write("世界，你好！".getBytes());
		out.flush();
		
//		Thread.sleep(1000);
//		
//		out.write("世界，你好么！！".getBytes());
//		out.flush();
		
		InputStream in = socket.getInputStream();
		byte[] buffer = new byte["世界，你好".getBytes().length];
		in.read(buffer);
		
		System.out.println(new String(buffer));
		
		Thread.sleep(2000);
		socket.close();
		System.out.println(new Date() + "socket closed.");
	}

}
