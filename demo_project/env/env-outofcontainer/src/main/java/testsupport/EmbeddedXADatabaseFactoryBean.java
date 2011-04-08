package testsupport;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.jdbc.datasource.embedded.DataSourceFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.sql.Driver;

public class EmbeddedXADatabaseFactoryBean extends EmbeddedDatabaseFactory
		implements FactoryBean<XADataSource>, InitializingBean, DisposableBean {

	public void setDataSource(final XADataSource dataSource) {
		super.setDataSourceFactory(new DataSourceFactory() {
			public ConnectionProperties getConnectionProperties() {
				return new ConnectionProperties() {
					public void setDriverClass(Class<? extends Driver> driverClass) {}
					public void setUrl(String url) {}
					public void setUsername(String username) {}
					public void setPassword(String password) {}
				};
			}

			public DataSource getDataSource() {
				return (DataSource) dataSource;
			}
		});
	}
	
	public void afterPropertiesSet() {
		initDatabase();
	}

	public XADataSource getObject() {
		return (XADataSource)getDataSource();
	}

	public Class<? extends DataSource> getObjectType() {
		return DataSource.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public void destroy() {
		shutdownDatabase();
	}
}
