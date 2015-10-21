package blog.demo.classloader.customize;

import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 * @user wujianchao Oct 21, 2015
 */
public class UserClassLoader extends URLClassLoader {

	public UserClassLoader(URL[] urls) {
		super(urls);
	}

}
