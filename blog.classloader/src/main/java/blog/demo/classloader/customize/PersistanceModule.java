package blog.demo.classloader.customize;

/**
 *
 * @user wujianchao Oct 21, 2015
 */
public interface PersistanceModule {
	 void save(Object data);
	 Object load();
}
