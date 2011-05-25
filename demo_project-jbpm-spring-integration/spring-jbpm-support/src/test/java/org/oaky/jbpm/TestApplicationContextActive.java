package org.oaky.jbpm;

import org.springframework.beans.factory.DisposableBean;

public class TestApplicationContextActive implements DisposableBean{

	public static int activeContextCounter;

	public TestApplicationContextActive() {
		activeContextCounter++;
	}

	public void destroy() throws Exception {
		activeContextCounter--;
	}
}
