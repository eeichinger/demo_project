package org.oaky.jbpm;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class TestActionHandler implements ActionHandler {

	// Before each test (in the setUp), the isExecuted member
	// will be set to false.
	public static boolean isExecuted = false;

	public static boolean throwException = false;

	// The action will set the isExecuted to true so the
	// unit test will be able to show when the action
	// is being executed.
	public void execute(ExecutionContext executionContext) {
		isExecuted = true;
		if (throwException) {
			throw new RuntimeException("a test exception from ActionHandler");
		}
	}
}
