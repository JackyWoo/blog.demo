package blog.demo.classloader;

/**
 *
 * @user wujianchao Oct 16, 2015
 */
public class UserDefinedClassA {

	static{
		System.out.println(UserDefinedClassA.class + " Loaded by " + UserDefinedClassA.class.getClassLoader());
	}
	
}
