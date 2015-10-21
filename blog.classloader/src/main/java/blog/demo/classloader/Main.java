package blog.demo.classloader;

/**
 *
 * @user wujianchao Oct 16, 2015
 */
public class Main {
	
	static{
		System.out.println(Main.class + " Loaded by " + Main.class.getClassLoader());
	}
	
	private UserDefinedClassA A;
	private UserDefinedClassB B;
	
	public Main(){
		A = new UserDefinedClassA();
		B = new UserDefinedClassB();
	}

	public static void main(String[] args) {
		String str = new String("I am a simple string");
		System.out.println("String is loaded by " + str.getClass().getClassLoader());
		Main mainClass = new Main();
	}

}
