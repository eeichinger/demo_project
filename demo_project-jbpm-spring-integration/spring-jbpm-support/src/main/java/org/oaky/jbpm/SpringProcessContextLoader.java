package org.oaky.jbpm;

import org.jbpm.graph.exe.ExecutionContext;
import org.springframework.context.ApplicationContext;

public interface SpringProcessContextLoader {
	ApplicationContext loadApplicationContext(ExecutionContext executionContext);
}
