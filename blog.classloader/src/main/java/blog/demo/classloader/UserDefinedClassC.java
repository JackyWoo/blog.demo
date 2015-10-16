package blog.demo.classloader;

/**
 *
 * @user wujianchao Oct 16, 2015
 */
public class UserDefinedClassC {

	static{
		System.out.println(UserDefinedClassC.class + " Loaded by " + UserDefinedClassC.class.getClassLoader());
	}
	
}
