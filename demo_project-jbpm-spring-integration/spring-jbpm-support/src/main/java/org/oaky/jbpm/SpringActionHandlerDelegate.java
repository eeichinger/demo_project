package org.oaky.jbpm;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class SpringActionHandlerDelegate implements ActionHandler {

	private String beanName;

	public SpringActionHandlerDelegate() {
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void execute(ExecutionContext executionContext) throws Exception {
		ActionHandler targetHandler = (ActionHandler) executionContext.getJbpmContext().getObjectFactory().createObject(beanName);
		targetHandler.execute(executionContext);
	}
}
