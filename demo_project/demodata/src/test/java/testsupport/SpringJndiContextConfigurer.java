package testsupport;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.PriorityOrdered;
import org.springframework.mock.jndi.SimpleNamingContext;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;
import java.lang.ref.WeakReference;
import java.util.Hashtable;

/**
 * @author Erich Eichinger
 * @date 2010-07-29
 */
public class SpringJndiContextConfigurer implements BeanFactoryPostProcessor, PriorityOrdered, DisposableBean {

    private int order = PriorityOrdered.HIGHEST_PRECEDENCE;

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        DelegatingInitialContextFactoryBuilder
                .getInstalledInitialContextFactoryBuilder()
                .setInitialContextFactoryBuilder(new SpringInitialContextFactoryBuilder(beanFactory));
    }

    public void destroy() throws Exception {
        DelegatingInitialContextFactoryBuilder
                .getInstalledInitialContextFactoryBuilder()
                .setInitialContextFactoryBuilder(null);
    }

    public class SpringInitialContextFactoryBuilder implements InitialContextFactoryBuilder {
        private WeakReference<BeanFactory> appCtxRef = null;

        public SpringInitialContextFactoryBuilder(BeanFactory appCtxRef) {
            this.appCtxRef = new WeakReference<BeanFactory>(appCtxRef);
        }

        public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment) throws NamingException {
            return new InitialContextFactory() {
                public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
                    return new SimpleNamingContext() {
                        public Object lookup(String lookupName) throws NameNotFoundException {
                            if (appCtxRef.get().containsBean(lookupName)) {
                                return appCtxRef.get().getBean(lookupName);
                            }
                            if (lookupName.startsWith("java:comp/env/")) {
                                lookupName = lookupName.substring("java:comp/env/".length());
                            }
                            if (lookupName.startsWith("java:comp/")) {
                                lookupName = lookupName.substring("java:comp/".length());
                            }
                            return appCtxRef.get().getBean(lookupName);
                        }
                    };
                }
            };
        }
    }

    public static class DelegatingInitialContextFactoryBuilder implements InitialContextFactoryBuilder {

        private InitialContextFactoryBuilder contextFactoryBuilder;

        public InitialContextFactoryBuilder getInitialContextFactoryBuilder() {
            return contextFactoryBuilder;
        }

        public void setInitialContextFactoryBuilder(InitialContextFactoryBuilder contextFactoryBuilder) {
            this.contextFactoryBuilder = contextFactoryBuilder;
        }

        public DelegatingInitialContextFactoryBuilder() {
        }

        public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment) throws NamingException {
            return contextFactoryBuilder.createInitialContextFactory(environment);
        }

        private static DelegatingInitialContextFactoryBuilder installedInitialContextFactoryBuilder;

        public static DelegatingInitialContextFactoryBuilder getInstalledInitialContextFactoryBuilder() {
            install();
            return installedInitialContextFactoryBuilder;
        }

        public synchronized static void install() {
            try {
                if (installedInitialContextFactoryBuilder == null) {
                    DelegatingInitialContextFactoryBuilder newBuilder = new DelegatingInitialContextFactoryBuilder();
                    NamingManager.setInitialContextFactoryBuilder(newBuilder);
                    installedInitialContextFactoryBuilder = newBuilder;
                }
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
