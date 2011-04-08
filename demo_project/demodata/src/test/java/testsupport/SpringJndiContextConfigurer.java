package testsupport;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.PriorityOrdered;
import org.springframework.mock.jndi.SimpleNamingContext;
import org.springframework.util.Assert;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
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
                .unsetInitialContextFactoryBuilder();
    }

    public class SpringInitialContextFactoryBuilder implements InitialContextFactoryBuilder {
        private WeakReference<BeanFactory> appCtxRef = null;

        public SpringInitialContextFactoryBuilder(BeanFactory appCtxRef) {
            this.appCtxRef = new WeakReference<BeanFactory>(appCtxRef);
        }

        public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment) throws NamingException {
	        if (environment != null && environment.containsKey(Context.INITIAL_CONTEXT_FACTORY)) {
		        String className = (String) environment.get(Context.INITIAL_CONTEXT_FACTORY);
		        try {
			        Class clazz = Class.forName(className);
			        return (InitialContextFactory) clazz.newInstance();
		        } catch (Exception e) {
			        throw new RuntimeException(e);
		        }
	        }
	        
            return new InitialContextFactory() {
                public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
                    return new ApplicationContextNamingContext(environment, appCtxRef);
                }
            };
        }
    }

	public static class ApplicationContextNamingContext implements Context {
		private final Hashtable env;
		private final WeakReference<BeanFactory> appCtxRef;
		
		public ApplicationContextNamingContext(Hashtable env, WeakReference<BeanFactory> appCtxRef) {
			this.env = env;
			this.appCtxRef = appCtxRef;
		}

		public Object lookup(String name) throws NamingException {
			if (appCtxRef.get().containsBean(name)) {
			    return appCtxRef.get().getBean(name);
			}
			if (name.startsWith("java:comp/env/")) {
			    name = name.substring("java:comp/env/".length());
			}
			if (name.startsWith("java:comp/")) {
			    name = name.substring("java:comp/".length());
			}
			return appCtxRef.get().getBean(name);
		}

		public Object lookup(Name name) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public void bind(Name name, Object obj) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public void bind(String name, Object obj) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public void rebind(Name name, Object obj) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public void rebind(String name, Object obj) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public void unbind(Name name) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public void unbind(String name) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public void rename(Name oldName, Name newName) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public void rename(String oldName, String newName) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public void destroySubcontext(Name name) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public void destroySubcontext(String name) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public Context createSubcontext(Name name) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public Context createSubcontext(String name) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public Object lookupLink(Name name) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public Object lookupLink(String name) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public NameParser getNameParser(Name name) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public NameParser getNameParser(String name) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public Name composeName(Name name, Name prefix) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public String composeName(String name, String prefix) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public Object addToEnvironment(String propName, Object propVal) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public Object removeFromEnvironment(String propName) throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}

		public Hashtable<?, ?> getEnvironment() throws NamingException {
			return env;
		}

		public void close() throws NamingException {
		}

		public String getNameInNamespace() throws NamingException {
			throw new OperationNotSupportedException("SimpleNamingContext does not support [javax.naming.Name]");
		}
	}
	
    public static class DelegatingInitialContextFactoryBuilder implements InitialContextFactoryBuilder {

        private InitialContextFactoryBuilder contextFactoryBuilder;

        public InitialContextFactoryBuilder getInitialContextFactoryBuilder() {
            return contextFactoryBuilder;
        }

        public void setInitialContextFactoryBuilder(InitialContextFactoryBuilder contextFactoryBuilder) {
	        Assert.isNull(this.contextFactoryBuilder, "contextFactoryBuilder is already set");
            this.contextFactoryBuilder = contextFactoryBuilder;
        }

        public void unsetInitialContextFactoryBuilder() {
            this.contextFactoryBuilder = null;
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
