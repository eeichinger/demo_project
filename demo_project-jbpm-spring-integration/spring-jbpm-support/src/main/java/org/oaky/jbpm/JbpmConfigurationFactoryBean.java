package org.oaky.jbpm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.configuration.ObjectFactory;
import org.jbpm.configuration.ObjectFactoryImpl;
import org.jbpm.configuration.ObjectFactoryParser;
import org.jbpm.configuration.ObjectInfo;
import org.jbpm.configuration.RefInfo;
import org.jbpm.util.XmlUtil;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @author Erich Eichinger
 */
public class JbpmConfigurationFactoryBean implements FactoryBean, InitializingBean, ApplicationListener, BeanFactoryAware {

	/** Logger for this class. */
	private static final Log LOG = LogFactory.getLog(JbpmConfigurationFactoryBean.class);

	/** The singleton object that this factory produces */
	private JbpmConfiguration jbpmConfiguration;

	/** resource holding the configuration file */
	private Resource configLocation = new ClassPathResource("jbpm.cfg.xml");

	private BeanFactory beanFactory;

	/** The jBPM object factory */
	private ObjectFactory objectFactory;

	/** The session factory to use */
	private SessionFactory sessionFactory;
	
	/** Indicates whether the job executor must be started */
	private boolean startJobExecutor;

	/** whether to set this configuration's object factory as the global default */
	private boolean makeObjectFactoryGlobalDefault;

	/** use spring to resolve jBpm ObjectFactory name resolution */
	private boolean useSpringObjectFactory = true;
	
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
		Assert.notNull(sessionFactory, "sessionFactory is required");
		Assert.notNull(configLocation, "configLocation is required");

		LOG.info("All properties set. Initializing the jBPM configuration");

		// Create jbpm Config object
		if (useSpringObjectFactory) {
			objectFactory = parseObjectFactory(configLocation.getInputStream());
			jbpmConfiguration = new JbpmConfiguration(objectFactory);
			// workaround for jbpm 3.2.10 - register config instance with objectFactory
			registerInstanceWithObjectFactory();
		} else {
//			objectFactory = ObjectFactoryParser.parseInputStream(configLocation.getInputStream());
			jbpmConfiguration = JbpmConfiguration.parseInputStream(configLocation.getInputStream());
		}

		// apply session factory - this will set the sessionfactory
		// on the DbPersistenceServiceFactory if available
		JbpmContext ctx = jbpmConfiguration.createJbpmContext();
		ctx.setSessionFactory(this.sessionFactory);
		if (makeObjectFactoryGlobalDefault) {
			JbpmConfiguration.Configs.setDefaultObjectFactory(ctx.getObjectFactory());
		}
		ctx.close();
		
		// Start job executor if needed
		if (startJobExecutor) {
			LOG.info("Starting job executor ...");
			jbpmConfiguration.startJobExecutor();
			LOG.info("Job executor started.");
		}
	}

	private void registerInstanceWithObjectFactory() {
		((SpringObjectFactory)objectFactory).addObjectInfo(
				new ObjectInfo() {
					public boolean hasName() {
						return true;
					}

					public String getName() {
						return "jbpm.configuration";
					}

					public boolean isSingleton() {
						return true;
					}

					public Object createObject(ObjectFactoryImpl objectFactory) {
						return jbpmConfiguration;
					}
				}
		);
	}

	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof ContextClosedEvent) {
			JbpmConfiguration.Configs.setDefaultObjectFactory(null);
			if (startJobExecutor) {
				jbpmConfiguration.getJobExecutor().stop();
			}
		}
	}

	public void setMakeObjectFactoryGlobalDefault(boolean makeObjectFactoryGlobalDefault) {
		this.makeObjectFactoryGlobalDefault = makeObjectFactoryGlobalDefault;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setObjectFactory(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}

	public void setConfigLocation(Resource configLocation) {
		Assert.notNull(configLocation);
		this.configLocation = configLocation;
	}

	public void setStartJobExecutor(boolean startJobExecutor) {
		this.startJobExecutor = startJobExecutor;
	}

	protected ObjectFactory parseObjectFactory(InputStream inputStream)
	{
	  ObjectFactoryParser objectFactoryParser = new ObjectFactoryParser();
	  ObjectFactoryImpl objectFactoryImpl = createObjectFactory(beanFactory);
	  objectFactoryParser.parseElementsFromResource("org/jbpm/default.jbpm.cfg.xml", objectFactoryImpl);

	  if (inputStream != null)
	  {
	    objectFactoryParser.parseElementsStream(inputStream, objectFactoryImpl);
	  }

	  return objectFactoryImpl;
	}

	protected ObjectFactoryImpl createObjectFactory(BeanFactory beanFactory) {
		return new SpringObjectFactory(beanFactory);
	}
	
//	protected ObjectFactory parseInputStream(InputStream xmlInputStream) {
//	  Element rootElement = XmlUtil.parseXmlInputStream(xmlInputStream).getDocumentElement();
//	  return createObjectFactory(rootElement);
//	}
//
//	protected ObjectFactory createObjectFactory(Element rootElement) {
//	  FixedObjectFactoryParser objectFactoryParser = new FixedObjectFactoryParser();
//	  List objectInfos = new ArrayList();
//	  List topLevelElements = XmlUtil.elements(rootElement);
//	  for (int i = 0; i<topLevelElements.size(); i++) {
//	    Element topLevelElement = (Element) topLevelElements.get(i);
//	    ObjectInfo objectInfo = objectFactoryParser.parse(topLevelElement);
//	    objectInfos.add(objectInfo);
//	  }
//	  return createObjectFactory(objectFactoryParser.getNamedObjectInfos(), objectInfos);
//	}
//
//	private ObjectFactory createObjectFactory(Map namedObjectInfos, List objectInfos) {
//		return new SpringObjectFactory(namedObjectInfos, objectInfos, this.beanFactory);
//	}
//
//	private static class FixedObjectFactoryParser extends ObjectFactoryParser {
//
//		private final Map namedObjectInfos = new HashMap();
//
//		public Map getNamedObjectInfos() {
//			return namedObjectInfos;
//		}
//
//		@Override
//		public void addNamedObjectInfo(String name, ObjectInfo objectInfo) {
//			this.namedObjectInfos.put(name, objectInfo);
//			super.addNamedObjectInfo(name, objectInfo);
//		}
//	}
}
