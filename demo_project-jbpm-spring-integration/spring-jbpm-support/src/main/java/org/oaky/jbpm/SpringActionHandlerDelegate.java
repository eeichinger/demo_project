package org.oaky.jbpm;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;

public class SpringActionHandlerDelegate implements ActionHandler, BeanFactoryAware, DisposableBean {

	private static BeanFactory globalBeanFactory;

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		Assert.isNull(globalBeanFactory, "globalBeanFactory already set - did you declare SpringActionHandlerDelegate multiple times in your application context?");
		globalBeanFactory = beanFactory;
	}

	private String beanName;

	public SpringActionHandlerDelegate() {
	}

	public void execute(ExecutionContext executionContext) throws Exception {
		Assert.notNull(globalBeanFactory, "globalBeanFactory not set - did you forget to declare a SpringActionHandlerDelegate bean in your application context?");
		globalBeanFactory.getBean(beanName, ActionHandler.class).execute(executionContext);
	}

	public void destroy() throws Exception {
		globalBeanFactory = null;
	}
}
