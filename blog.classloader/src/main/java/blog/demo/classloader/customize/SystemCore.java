package blog.demo.classloader.customize;

import java.net.URL;

/**
 *
 * @user wujianchao Oct 21, 2015
 */
public class SystemCore {
	
	private PersistanceModule persistance;
	
	public void setPersistanceModule(PersistanceModule persistance){
		this.persistance = persistance;
	}
	
	public String getPersistanceModuleImplementation(String config){
		if(config.equals("hdfs")){
			return "blog.demo.classloader.customize.HdfsPersistanceModule";
		}
		return null;
	}
	
	public URL[] getPersistanceModuleRepositoryPath(){
		return null;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		SystemCore core = new SystemCore();
		//system core do something init
		//init userClassloader with repository path
		
		URL[] urls = core.getPersistanceModuleRepositoryPath();
		UserClassLoader ucl = new UserClassLoader(urls);
		Class<?> clazz = ucl.loadClass(core.getPersistanceModuleImplementation("hdfs"));
		//note that here PersistanceModule.class is loaded by appCladdLoader, but HdfsPersistanceModule.class and related classes are by ucl.
		PersistanceModule persistance = (PersistanceModule) clazz.newInstance();
		
		core.setPersistanceModule(persistance);
		
		//system core do something 
		persistance.load();
		//system core do something 
		persistance.save(new Object());
		//system core do something 
	}

}
