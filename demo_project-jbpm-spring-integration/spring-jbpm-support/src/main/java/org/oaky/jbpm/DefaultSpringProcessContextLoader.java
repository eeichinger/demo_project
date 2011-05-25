package org.oaky.jbpm;

import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class DefaultSpringProcessContextLoader implements SpringProcessContextLoader {
	public ApplicationContext loadApplicationContext(ExecutionContext executionContext) {
		Assert.notNull(executionContext, "executionContext is required");
		Resource configLocation = resolveApplicationContextConfigurationLocation(executionContext);
//		executionContext.getJbpmContext().getServices().getServiceFactories().
		return createApplicationContext(configLocation);
	}

	private ApplicationContext createApplicationContext(Resource configLocation) {
		return new GenericXmlApplicationContext(configLocation);
	}

	protected Resource resolveApplicationContextConfigurationLocation(ExecutionContext executionContext) {
		String location = executionContext.getProcessDefinition().getName() + "-context.xml";
		return new ClassPathResource(location);
	}
}
