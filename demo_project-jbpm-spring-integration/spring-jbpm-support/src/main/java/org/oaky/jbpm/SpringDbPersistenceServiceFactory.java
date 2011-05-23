package org.oaky.jbpm;

import org.hibernate.SessionFactory;
import org.jbpm.svc.Service;
import org.jbpm.svc.ServiceFactory;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

public class SpringDbPersistenceServiceFactory implements ServiceFactory {

	private static SessionFactory globalSessionFactory;
	private static TransactionTemplate globalTransactionTemplate;

	/*
	 use this constructor for defining the spring bean
	 */
	public SpringDbPersistenceServiceFactory(SessionFactory sessionFactory, TransactionTemplate transactionManager) {
		Assert.isNull(globalSessionFactory);
		Assert.isNull(globalTransactionTemplate);
		globalSessionFactory = sessionFactory;
		globalTransactionTemplate = transactionManager;
		this.transactionTemplate = null;
		this.sessionFactory = null;
	}

	private final SessionFactory sessionFactory;
	private final TransactionTemplate transactionTemplate;

	public SpringDbPersistenceServiceFactory() {
//		setCurrentSessionEnabled(true);
//		setTransactionEnabled(false);

		Assert.notNull(globalSessionFactory, "globalSessionFactory not set - did you forget to declare a SpringDbPersistenceServiceFactory bean in your ApplicationContext?");
		Assert.notNull(globalTransactionTemplate, "globalTransactionManager not set - did you forget to declare a SpringDbPersistenceServiceFactory bean in your ApplicationContext?");

		sessionFactory = globalSessionFactory;
		transactionTemplate = globalTransactionTemplate;
	}

	public Service openService() {
		return new SpringDbPersistenceService(sessionFactory, transactionTemplate);
	}

	public void close() {
		// noop
	}
}
