package org.oaky.jbpm;

import org.jbpm.configuration.ObjectFactoryImpl;
import org.jbpm.configuration.ObjectInfo;
import org.springframework.beans.factory.BeanFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpringObjectFactory extends ObjectFactoryImpl {

	private BeanFactory beanFactory;

	public SpringObjectFactory(Map namedObjectInfos, List objectInfos, BeanFactory beanFactory) {
		super(new SpringNamedObjectInfos(namedObjectInfos, beanFactory), objectInfos);
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

	private static class SpringNamedObjectInfos extends HashMap {

		private final BeanFactory beanFactory;
		
		private SpringNamedObjectInfos(Map m, BeanFactory beanFactory) {
			super(m);
			this.beanFactory = beanFactory;
		}

		@Override
		public boolean containsKey(Object key) {
			return beanFactory.containsBean((String) key) || super.containsKey(key);
		}

		@Override
		public Object get(final Object key) {
			if (beanFactory.containsBean((String) key)) {
				return new ObjectInfo() {
					public boolean hasName() {
						return true;
					}

					public String getName() {
						return (String) key;
					}

					public boolean isSingleton() {
						return beanFactory.isSingleton(getName());
					}

					public Object createObject(ObjectFactoryImpl objectFactory) {
						return beanFactory.getBean(getName());
					}
				};
			}
			return super.get(key);
		}
	}
}
