package blog.demo.classloader.customize;

/**
 *
 * @user wujianchao Oct 21, 2015
 */
public class HdfsPersistanceModule implements PersistanceModule {

	@Override
	public void save(Object data) {
		//save data in hdfs
	}

	@Override
	public Object load() {
		//load from hdfs
		return null;
	}

}
