package org.oaky.jbpm;

import org.jbpm.svc.Service;
import org.jbpm.svc.ServiceFactory;

public class SpringTxServiceFactory implements ServiceFactory {
	
	public Service openService() {
		throw new RuntimeException("TO BE IMPLEMENTED");
	}

	public void close() {
		throw new RuntimeException("TO BE IMPLEMENTED");
	}
}
