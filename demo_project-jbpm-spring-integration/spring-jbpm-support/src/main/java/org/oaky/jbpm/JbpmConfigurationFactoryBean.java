package org.oaky.jbpm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.configuration.ObjectFactory;
import org.jbpm.configuration.ObjectFactoryParser;
import org.jbpm.util.ClassLoaderUtil;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Factory bean that produces the (singleton) jBPM configuration bean.
 *
 * Features : - returns the singleton JbpmConfiguration object for this application
 *            - allows the injection of the Spring-configured session factory, which will then be used by
 *              jBPM to access the database
 *            - allows to start a prefdefined nr of job executor threads and shuts them down properly
 *              when the application context goes doen.
 *
 * @author Joram Barrez
 */
public class JbpmConfigurationFactoryBean implements FactoryBean, InitializingBean, ApplicationListener, BeanFactoryAware {

	/** Logger for this class. */
	private static final Log LOG = LogFactory.getLog(JbpmConfigurationFactoryBean.class);

	/** The singleton object that this factory produces */
	private JbpmConfiguration jbpmConfiguration;

	/** resource holding the configuration file */
	private Resource configLocation = new ClassPathResource("jpbm.cfg.xml");

	private BeanFactory beanFactory;

	/** The jBPM object factory */
	private ObjectFactory objectFactory;

	/** Indicates whether the job executor must be started */
	private boolean startJobExecutor;

	/**
	 * Default constructor.
	 */
	public JbpmConfigurationFactoryBean() {

	}

	public Object getObject() throws Exception {
		return jbpmConfiguration;
	}

	public Class getObjectType() {
		return JbpmConfiguration.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public void afterPropertiesSet() throws Exception {

		LOG.info("All properties set. Initializing the jBPM configuration");

		// Create jbpm Config object
		if (configLocation != null) {
			objectFactory = ObjectFactoryParser.parseInputStream(configLocation.getInputStream());
		} else {
			objectFactory = ObjectFactoryParser.parseInputStream(ClassLoaderUtil.getJbpmConfigurationStream("jpbm.cfg.xml"));
		}
		ObjectFactory actualObjectFactory = new SpringObjectFactory(this.beanFactory, this.objectFactory);

		jbpmConfiguration = new JbpmConfiguration(actualObjectFactory);

		// Start job executor if needed
		if (startJobExecutor) {
			LOG.info("Starting job executor ...");
			jbpmConfiguration.startJobExecutor();
			LOG.info("Job executor started.");
		}
	}

	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof ContextClosedEvent) {
			if (startJobExecutor) {
				jbpmConfiguration.getJobExecutor().stop();
			}
		}
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setObjectFactory(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}

	public void setConfigLocation(Resource configLocation) {
		this.configLocation = configLocation;
	}

	public void setStartJobExecutor(boolean startJobExecutor) {
		this.startJobExecutor = startJobExecutor;
	}

//	protected JbpmConfiguration createDefaultConfiguration() {
//		return JbpmConfiguration.parseXmlString(
//				"<jbpm-configuration>" +
//						// A jbpm-context mechanism separates the jbpm core
//						// engine from the services that jbpm uses from
//						// the environment.
//						"<jbpm-context>" +
//						"	<service name='authentication' factory='org.jbpm.security.authentication.DefaultAuthenticationServiceFactory' />" +
//						"	<service name='logging' factory='org.jbpm.logging.db.DbLoggingServiceFactory' />" +
//						"	<service name='message' factory='org.jbpm.msg.db.DbMessageServiceFactory' />" +
////						"	<service name='persistence' factory='org.jbpm.persistence.db.DbPersistenceServiceFactory' />" +
//						"   <service name='persistence' factory='"+ SpringDbPersistenceServiceFactory.class.getName()+"' />" +
//						"	<service name='scheduler' factory='org.jbpm.scheduler.db.DbSchedulerServiceFactory' />" +
//						"	<service name='tx' factory='org.jbpm.tx.TxServiceFactory' />" +
//						"</jbpm-context>" +
//				"</jbpm-configuration>"
//		);
//	}
}
