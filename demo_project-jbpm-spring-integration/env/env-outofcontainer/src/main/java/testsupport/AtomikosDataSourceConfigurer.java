package testsupport;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import com.atomikos.icatch.system.Configuration;
import com.atomikos.jdbc.AtomikosDataSourceBean;

/**
 * @author Erich Eichinger
 * @date 2010-07-23
 */
public class AtomikosDataSourceConfigurer implements BeanPostProcessor, PriorityOrdered, DisposableBean {
    private final static Logger log = LoggerFactory.getLogger(AtomikosDataSourceConfigurer.class);

    private static class XAAtomikosDataSourceBean extends AtomikosDataSourceBean implements XADataSource {
        public <T> T unwrap(Class<T> iface) throws SQLException {
            throw new RuntimeException("TO BE IMPLEMENTED");
        }

        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            throw new RuntimeException("TO BE IMPLEMENTED");
        }

        public XAConnection getXAConnection() throws SQLException {
            throw new RuntimeException("TO BE IMPLEMENTED");
        }

        public XAConnection getXAConnection(String user, String password) throws SQLException {
            throw new RuntimeException("TO BE IMPLEMENTED");
        }
    }

    private int order = Ordered.LOWEST_PRECEDENCE;
    private boolean allowNonXADataSources = false;
    private AtomikosDataSourceBean prototypeAtomikosConfig = new AtomikosDataSourceBean();
    private final static List<DisposableBean> dataSourceBeans = new ArrayList<DisposableBean>();

    public boolean isAllowNonXADataSources() {
        return allowNonXADataSources;
    }

    public void setAllowNonXADataSources(boolean allowNonXADataSources) {
        this.allowNonXADataSources = allowNonXADataSources;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getMaxPoolSize() {
        return prototypeAtomikosConfig.getMaxPoolSize();
    }

    public void setMaxPoolSize(int maxPoolSize) {
        prototypeAtomikosConfig.setMaxPoolSize(maxPoolSize);
    }

    public int getMinPoolSize() {
        return prototypeAtomikosConfig.getMinPoolSize();
    }

    public void setMinPoolSize(int minPoolSize) {
        prototypeAtomikosConfig.setMinPoolSize(minPoolSize);
    }

    public AtomikosDataSourceConfigurer() {
        log.debug("instantiated");
        if (dataSourceBeans.size() > 0) {
            throw new RuntimeException("orphan datasources left from previous run");
        }

        ArrayList list = new ArrayList();
        Enumeration enumeration = Configuration.getResources();
        while (enumeration.hasMoreElements()) {
            list.add(enumeration.nextElement());
        }

        if (list.size() > 0) {
            throw new RuntimeException("orphan resources left from previous run");
        }
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            return doPostProcess(bean, beanName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object doPostProcess(Object bean, final String beanName) throws Exception {
        if (!(bean instanceof AtomikosDataSourceBean)) {
            if (bean instanceof XADataSource) {
                log.debug("wrapping datasource bean '" + beanName + "'");
                final XAAtomikosDataSourceBean adsb = createAtomikosDataSource(beanName, bean);
                adsb.init();
                registerDisposableBean(beanName, adsb);
                bean = adsb;
            } else if (bean instanceof DataSource) {
                if (allowNonXADataSources) {
                    log.warn("datasource bean '" + beanName + "' is not an XADataSource");
//                    DataSource ds = (DataSource) bean;
//                    AtomikosNonXADataSourceBean adsb = new AtomikosNonXADataSourceBean();
//                    adsb.setUniqueResourceName(beanName);
//                    adsb.setDriverClassName(ds.);
                    // TBD/FIXME
                } else {
                    throw new BeanNotOfRequiredTypeException(beanName, XADataSource.class, bean.getClass());
                }
            }
        }
        return bean;
    }

    private XAAtomikosDataSourceBean createAtomikosDataSource(String beanName, Object bean) {
        final XAAtomikosDataSourceBean adsb = new XAAtomikosDataSourceBean();
        adsb.setUniqueResourceName("XADS_" + System.currentTimeMillis());
        adsb.setXaDataSource((XADataSource) bean);
        adsb.setMaxPoolSize(this.prototypeAtomikosConfig.getMaxPoolSize());
        Properties props = new Properties();
        props.put("dataSourceName", beanName);
        adsb.setXaProperties(props);
        return adsb;
    }

    private void registerDisposableBean(final String beanName, final AtomikosDataSourceBean adsb) {
        dataSourceBeans.add(new DisposableBean() {
            public void destroy() throws Exception {
                log.debug("closing datasource bean '" + beanName + "'");
                adsb.close();
            }
        });
    }

    public void destroy() throws Exception {
        for (DisposableBean bean : dataSourceBeans) {
            bean.destroy();
        }
        dataSourceBeans.clear();
        log.debug("disposed");
    }
}
