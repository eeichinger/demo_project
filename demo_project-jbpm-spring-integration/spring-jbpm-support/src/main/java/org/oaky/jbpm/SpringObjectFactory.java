package org.oaky.jbpm;

import org.jbpm.configuration.ObjectFactoryImpl;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;

public class SpringObjectFactory extends ObjectFactoryImpl {

	private BeanFactory beanFactory;

	public SpringObjectFactory(BeanFactory beanFactory) {
		super();
		Assert.notNull(beanFactory, "beanFactory is required");
		this.beanFactory = beanFactory;
	}

	public Object createObject(String name) {
		if (beanFactory.containsBean(name)) {
			return beanFactory.getBean(name);
		}
		return super.createObject(name);
	}

	public boolean hasObject(String name) {
		return beanFactory.containsBean(name) || super.hasObject(name);
	}
}
