package org.oaky.jbpm;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jbpm.db.ContextSession;
import org.jbpm.db.GraphSession;
import org.jbpm.db.JobSession;
import org.jbpm.db.LoggingSession;
import org.jbpm.db.TaskMgmtSession;
import org.jbpm.persistence.PersistenceService;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

public class SpringDbPersistenceService implements PersistenceService {

	private final SessionFactory sessionFactory;
	private final TransactionTemplate transactionTemplate;
	private TransactionStatus currentTransaction;
	
	public SpringDbPersistenceService(SessionFactory sessionFactory, TransactionTemplate transactionTemplate) {
		this.sessionFactory = sessionFactory;
		this.transactionTemplate = transactionTemplate;

		currentTransaction = transactionTemplate.getTransactionManager().getTransaction(transactionTemplate);
	}

	protected Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public void assignId(Object object) {
		getCurrentSession().save(object);
	}

	private GraphSession graphSession;
	private LoggingSession loggingSession;
	private JobSession jobSession;
	private ContextSession contextSession;
	private TaskMgmtSession taskMgmtSession;
	
	public GraphSession getGraphSession() {
		if (graphSession == null && getCurrentSession() != null) {
			graphSession = new GraphSession(getCurrentSession());
		}
		return graphSession;
	}

	public LoggingSession getLoggingSession() {
		if (loggingSession == null && getCurrentSession() != null) {
			loggingSession = new LoggingSession(getCurrentSession());
		}
		return loggingSession;
	}

	public JobSession getJobSession() {
		if (jobSession == null && getCurrentSession() != null) {
			jobSession = new JobSession(getCurrentSession());
		}
		return jobSession;
	}

	public ContextSession getContextSession() {
		if (contextSession == null && getCurrentSession() != null) {
			contextSession = new ContextSession(getCurrentSession());
		}
		return contextSession;
	}

	public TaskMgmtSession getTaskMgmtSession() {
		if (taskMgmtSession == null && getCurrentSession() != null) {
			taskMgmtSession = new TaskMgmtSession(getCurrentSession());
		}
		return taskMgmtSession;
	}

	public boolean isRollbackOnly() {
//		return currentTransaction.isRollbackOnly();
		throw new RuntimeException("TO BE IMPLEMENTED");
	}

	public void setRollbackOnly(boolean isRollbackOnly) {
		throw new RuntimeException("TO BE IMPLEMENTED");
	}

	public void setRollbackOnly() {
//		currentTransaction.setRollbackOnly();
		throw new RuntimeException("TO BE IMPLEMENTED");
	}

	public void setGraphSession(GraphSession graphSession) {
		this.graphSession = graphSession;
	}

	public void setLoggingSession(LoggingSession loggingSession) {
		this.loggingSession = loggingSession;
	}

	public void setJobSession(JobSession jobSession) {
		this.jobSession = jobSession;
	}

	public void setTaskMgmtSession(TaskMgmtSession taskMgmtSession) {
		this.taskMgmtSession = taskMgmtSession;
	}

	public void close() {
		transactionTemplate.getTransactionManager().commit(currentTransaction);
	}
}