//package testsupport;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.Properties;
//
//import org.springframework.beans.factory.BeanNameAware;
//
//public class DebuggableOracleXADataSource extends oracle.jdbc.xa.client.OracleXADataSource implements BeanNameAware {
//
//	private static final long serialVersionUID = 4786918747939961417L;
//
//	private String beanName;
//
//	public void setBeanName(String beanName) {
//		this.beanName = beanName;
//	}
//
//	public DebuggableOracleXADataSource() throws SQLException {
//		super();
//		// TODO Auto-generated constructor stub
//	}
//
//	@Override
//	protected Connection getPhysicalConnection(Properties props) throws SQLException {
//		try {
//			return super.getPhysicalConnection(props);
//		} catch (SQLException e) {
//			throw new SQLException("error connecting datasource '" + beanName + "'", e);
//		}
//	}
//}
