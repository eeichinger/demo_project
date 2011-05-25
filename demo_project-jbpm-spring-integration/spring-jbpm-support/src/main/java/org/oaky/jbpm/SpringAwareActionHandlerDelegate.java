package org.oaky.jbpm;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

public class SpringAwareActionHandlerDelegate implements ActionHandler {

	private String beanName;
	private SpringProcessContextLoader contextLoader;
	
	public SpringAwareActionHandlerDelegate() {
		this(new DefaultSpringProcessContextLoader());
	}

	public SpringAwareActionHandlerDelegate(SpringProcessContextLoader contextLoader) {
		Assert.notNull(contextLoader, "ContextLoader is required");
		this.contextLoader = contextLoader;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void execute(ExecutionContext executionContext) throws Exception {
		Assert.hasText(beanName, "beanName must be specified on <action> handler definition");
		ActionHandler actionHandler = resolveTargetBean(executionContext);
		actionHandler.execute(executionContext);
		return;
	}

	protected SpringProcessContextLoader getContextLoader() {
		return contextLoader;
	}
	
	protected ActionHandler resolveTargetBean(ExecutionContext executionContext) {
		ApplicationContext applicationContext = getContextLoader().loadApplicationContext(executionContext);
		return applicationContext.getBean(beanName, ActionHandler.class);
	}
}
