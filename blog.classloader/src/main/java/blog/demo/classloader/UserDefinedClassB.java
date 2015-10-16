package blog.demo.classloader;

/**
 *
 * @user wujianchao Oct 16, 2015
 */
public class UserDefinedClassB {
	
	private UserDefinedClassC C;
	
	static{
		System.out.println(UserDefinedClassB.class + " Loaded by " + UserDefinedClassB.class.getClassLoader());
	}
	
	public UserDefinedClassB(){
		C = new UserDefinedClassC();
	}
}
