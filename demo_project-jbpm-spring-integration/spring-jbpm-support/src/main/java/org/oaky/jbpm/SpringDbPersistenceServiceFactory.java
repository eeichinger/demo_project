package org.oaky.jbpm;

import org.hibernate.SessionFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.svc.Service;
import org.jbpm.svc.ServiceFactory;
import org.jbpm.tx.TxService;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

public class SpringDbPersistenceServiceFactory implements ServiceFactory {

	public SpringDbPersistenceServiceFactory(JbpmConfiguration jbpmConfiguration, SessionFactory sessionFactory, TransactionTemplate transactionTemplate) {
		Assert.notNull(jbpmConfiguration, "jbpmConfiguration is mandatory");
		Assert.notNull(sessionFactory, "globalSessionFactory is mandatory");
		Assert.notNull(transactionTemplate, "globalTransactionManager is mandatory");

		this.jbpmConfiguration = jbpmConfiguration;
		this.sessionFactory = sessionFactory;
		this.transactionTemplate = transactionTemplate;
	}

	private final JbpmConfiguration jbpmConfiguration;
	private final SessionFactory sessionFactory;
	private final TransactionTemplate transactionTemplate;

	public Service openService() {
		final TxService txService = jbpmConfiguration.getCurrentJbpmContext().getServices().getTxService();
		Assert.notNull(txService, "tx service is mandatory when using persistence service");
		return new SpringDbPersistenceService(txService, sessionFactory, transactionTemplate);
	}

	public void close() {
		// noop
	}
}
