package blog.demo.classloader;

import java.sql.Driver;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 *
 * @user wujianchao Oct 15, 2015
 */
public class MysqlDriver {
	public static void main(String[] args) throws SQLException {
//		BasicDataSource dataSource = new BasicDataSource();
//	    dataSource.setUsername("user");
//	    dataSource.setPassword("pwd");
//	    dataSource.setUrl("");
//
//	    dataSource.setValidationQuery("SELECT 1");
//	    dataSource.setTestOnBorrow(true);
//	    dataSource.setDriverClassLoader(MysqlDriver.class.getClassLoader());
//	    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
////	    dataSource.getConnection();
	    
	    Driver driver = new com.mysql.jdbc.Driver();
	    driver.connect("null", null);
	    
	    
	}
}
