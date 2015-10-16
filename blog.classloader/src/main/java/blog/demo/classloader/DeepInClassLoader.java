package blog.demo.classloader;

import java.net.URLClassLoader;

/**
 *
 * @user wujianchao Oct 15, 2015
 */
public class DeepInClassLoader {

	public static void main(String[] args) throws ClassNotFoundException {
//		Class.forName("");
//		URLClassLoader
		System.out.println(System.getProperty("sun.boot.class.path"));
		System.out.println(System.getProperty("java.class.path"));  
	}

}
