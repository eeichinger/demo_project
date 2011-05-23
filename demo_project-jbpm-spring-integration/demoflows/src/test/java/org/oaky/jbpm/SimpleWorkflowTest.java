package org.oaky.jbpm;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.persistence.db.DbPersistenceServiceFactory;
import org.jbpm.svc.Services;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/integration-test-context.xml")
public class SimpleWorkflowTest {

	JbpmConfiguration jbpmConfiguration = null;

	public SimpleWorkflowTest() {
		jbpmConfiguration = JbpmConfiguration.parseXmlString(
				"<jbpm-configuration>" +
						// A jbpm-context mechanism separates the jbpm core
						// engine from the services that jbpm uses from
						// the environment.
						"<jbpm-context>" +
						"  <service name='persistence' factory='org.jbpm.persistence.db.DbPersistenceServiceFactory' />" +
						"</jbpm-context>" +

						// Also all the resource files that are used by jbpm are
						// referenced from the jbpm.cfg.xml
						"<string name='resource.hibernate.cfg.xml' value='hibernate.cfg.test.xml' />" +
				"</jbpm-configuration>"
		);
	}

	@Before
	public void setUp() {
		DbPersistenceServiceFactory sf = (DbPersistenceServiceFactory) jbpmConfiguration.createJbpmContext().getServiceFactory(Services.SERVICENAME_PERSISTENCE);
		sf.createSchema();
	}

	@After
	public void tearDown() {
		jbpmConfiguration.dropSchema();
	}

	@Test
	public void testSimplePersistence() {
		// Between the 3 method calls below, all data is passed via the
		// database.  Here, in this unit test, these 3 methods are executed
		// right after each other because we want to test a complete process
		// scenario.  But in reality, these methods represent different
		// requests to a server.

		// Since we start with a clean, empty in-memory database, we have to
		// deploy the process first.  In reality, this is done once by the
		// process developer.
		deployProcessDefinition();

		// Suppose we want to start a process instance (=process execution)
		// when a user submits a form in a web application...
		processInstanceIsCreatedWhenUserSubmitsWebappForm();

		// Then, later, upon the arrival of an asynchronous message the
		// execution must continue.
		theProcessInstanceContinuesWhenAnAsyncMessageIsReceived();
	}

	public void deployProcessDefinition() {
		// This test shows a process definition and one execution
		// of the process definition.  The process definition has
		// 3 nodes: an unnamed start-state, a state 's' and an
		// end-state named 'end'.
		ProcessDefinition processDefinition =
				ProcessDefinition.parseXmlString(
						"<process-definition name='hello world'>" +
								"  <start-state name='start'>" +
								"    <transition to='s' />" +
								"  </start-state>" +
								"  <state name='s'>" +
								"    <transition to='end' />" +
								"  </state>" +
								"  <end-state name='end' />" +
								"</process-definition>"
				);

		//Lookup the pojo persistence context-builder that is configured above
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			// Deploy the process definition in the database
			jbpmContext.deployProcessDefinition(processDefinition);

		} finally {
			// Tear down the pojo persistence context.
			// This includes flush the SQL for inserting the process definition
			// to the database.
			jbpmContext.close();
		}
	}

	public void processInstanceIsCreatedWhenUserSubmitsWebappForm() {
		// The code in this method could be inside a struts-action
		// or a JSF managed bean.

		//Lookup the pojo persistence context-builder that is configured above
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {

			GraphSession graphSession = jbpmContext.getGraphSession();

			ProcessDefinition processDefinition =
					graphSession.findLatestProcessDefinition("hello world");

			//With the processDefinition that we retrieved from the database, we
			//can create an execution of the process definition just like in the
			//hello world example (which was without persistence).
			ProcessInstance processInstance =
					new ProcessInstance(processDefinition);

			Token token = processInstance.getRootToken();
			assertEquals("start", token.getNode().getName());
			// Let's start the process execution
			token.signal();
			// Now the process is in the state 's'.
			assertEquals("s", token.getNode().getName());

			// Now the processInstance is saved in the database.  So the
			// current state of the execution of the process is stored in the
			// database.
			jbpmContext.save(processInstance);
			// The method below will get the process instance back out
			// of the database and resume execution by providing another
			// external signal.

		} finally {
			// Tear down the pojo persistence context.
			jbpmContext.close();
		}
	}

	public void theProcessInstanceContinuesWhenAnAsyncMessageIsReceived() {
		//The code in this method could be the content of a message driven bean.

		// Lookup the pojo persistence context-builder that is configured above
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {

			GraphSession graphSession = jbpmContext.getGraphSession();
			// First, we need to get the process instance back out of the
			// database.  There are several options to know what process
			// instance we are dealing  with here.  The easiest in this simple
			// test case is just to look for the full list of process instances.
			// That should give us only one result.  So let's look up the
			// process definition.

			ProcessDefinition processDefinition =
					graphSession.findLatestProcessDefinition("hello world");

			//Now search for all process instances of this process definition.
			List processInstances =
					graphSession.findProcessInstances(processDefinition.getId());

			// Because we know that in the context of this unit test, there is
			// only one execution.  In real life, the processInstanceId can be
			// extracted from the content of the message that arrived or from
			// the user making a choice.
			ProcessInstance processInstance =
					(ProcessInstance) processInstances.get(0);

			// Now we can continue the execution.  Note that the processInstance
			// delegates signals to the main path of execution (=the root token).
			processInstance.signal();

			// After this signal, we know the process execution should have
			// arrived in the end-state.
			assertTrue(processInstance.hasEnded());

			// Now we can update the state of the execution in the database
			jbpmContext.save(processInstance);

		} finally {
			// Tear down the pojo persistence context.
			jbpmContext.close();
		}
	}
}

//public class SimpleWorkflowTest {
//
//	@Test
//	public void simpleWorkflow_should_pass() {
//
//		JbpmConfiguration cfg = JbpmConfiguration.parseXmlString();
//		// The next process is a variant of the hello world process.
//		// We have added an action on the transition from state 's'
//		// to the end-state.  The purpose of this test is to show
//		// how easy it is to integrate Java code in a jBPM process.
//		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
//				"<process-definition>" +
//						"  <start-state>" +
//						"    <transition to='s' />" +
//						"  </start-state>" +
//						"  <state name='s'>" +
//						"    <transition to='end'>" +
//						"      <action class='"+MyActionHandler.class.getName()+"' />" +
//						"    </transition>" +
//						"  </state>" +
//						"  <end-state name='end' />" +
//						"</process-definition>"
//		);
//
//		// Let's start a new execution for the process definition.
//		ProcessInstance processInstance =
//				new ProcessInstance(processDefinition);
//
//		// The next signal will cause the execution to leave the start
//		// state and enter the state 's'
//		processInstance.signal();
//
//		// Here we show that MyActionHandler was not yet executed.
//		assertFalse(MyActionHandler.isExecuted);
//		// ... and that the main path of execution is positioned in
//		// the state 's'
//		assertSame(processDefinition.getNode("s"),
//				processInstance.getRootToken().getNode());
//
//		// The next signal will trigger the execution of the root
//		// token.  The token will take the transition with the
//		// action and the action will be executed during the
//		// call to the signal method.
//		processInstance.signal();
//
//		// Here we can see that MyActionHandler was executed during
//		// the call to the signal method.
//		assertTrue(MyActionHandler.isExecuted);
//	}
//
//	public static class MyActionHandler implements ActionHandler {
//
//		// Before each test (in the setUp), the isExecuted member
//		// will be set to false.
//		public static boolean isExecuted = false;
//
//		// The action will set the isExecuted to true so the
//		// unit test will be able to show when the action
//		// is being executed.
//		public void execute(ExecutionContext executionContext) {
//			isExecuted = true;
//		}
//	}
//}
