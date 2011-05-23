package org.oaky.jbpm;

import org.jbpm.configuration.ObjectFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class SpringObjectFactory implements ObjectFactory,BeanFactoryAware {

	private BeanFactory beanFactory;
	private final ObjectFactory inner;

	public SpringObjectFactory(ObjectFactory inner) {
		this.inner = inner;
	}

	public SpringObjectFactory(BeanFactory beanFactory, ObjectFactory inner) {
		this.beanFactory = beanFactory;
		this.inner = inner;
	}

	public Object createObject(String name) {
		if (beanFactory.containsBean(name)) {
			return beanFactory.getBean(name);
		}
		return inner.createObject(name);
	}

	public boolean hasObject(String name) {
		return beanFactory.containsBean(name) || inner.hasObject(name);
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
}
