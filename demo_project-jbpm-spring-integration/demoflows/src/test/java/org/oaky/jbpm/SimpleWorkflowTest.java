package org.oaky.jbpm;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/integration-test-context.xml")
public class SimpleWorkflowTest {

	JbpmConfiguration jbpmConfiguration = null;
	JbpmTemplate jbmp;
	
	public SimpleWorkflowTest() {
		jbpmConfiguration = JbpmConfiguration.parseXmlString(
				"<jbpm-configuration>" +
						// A jbpm-context mechanism separates the jbpm core
						// engine from the services that jbpm uses from
						// the environment.
						"<jbpm-context>" +
//						"  <service name='persistence' factory='org.jbpm.persistence.db.DbPersistenceServiceFactory' />" +
//						"  <service name='tx' factory='"+ TxServiceFactory.class.getName()+"' />" +
						"  <service name='persistence' factory='"+ SpringDbPersistenceServiceFactory.class.getName()+"' />" +
						"</jbpm-context>" +

						// Also all the resource files that are used by jbpm are
						// referenced from the jbpm.cfg.xml
						"<string name='resource.hibernate.cfg.xml' value='hibernate.cfg.hsqldb.xml' />" +
				"</jbpm-configuration>"
		);
		jbmp = new JbpmTemplate(jbpmConfiguration);
	}

	@Before
	public void setUp() {
		// Since we start with a clean, empty in-memory database, we have to
		// deploy the process first.  In reality, this is done once by the
		// process developer.
		deployProcessDefinition();
		TestActionHandler.isExecuted = false;
		TestActionHandler.throwException = false;
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testSimplePersistence() {
		// Between the 3 method calls below, all data is passed via the
		// database.  Here, in this unit test, these 3 methods are executed
		// right after each other because we want to test a complete process
		// scenario.  But in reality, these methods represent different
		// requests to a server.

		// Suppose we want to start a process instance (=process execution)
		// when a user submits a form in a web application...
		long processId = processInstanceIsCreatedWhenUserSubmitsWebappForm();

		// Then, later, upon the arrival of an asynchronous message the
		// execution must continue.
		theProcessInstanceContinuesWhenAnAsyncMessageIsReceived(processId);

		assertTrue(TestActionHandler.isExecuted);
	}

	@Test
	public void testFailingActionHandler() {
		long processId = processInstanceIsCreatedWhenUserSubmitsWebappForm();

		// Then, later, upon the arrival of an asynchronous message the
		// execution must continue - this time the ActionHandler throws an exception.
		TestActionHandler.throwException = true;
		try {
			theProcessInstanceContinuesWhenAnAsyncMessageIsReceived(processId);
			fail("expected exception");
		} catch(RuntimeException rex) {
			assertTrue(rex.getMessage().contains("a test exception"));
		} finally {
			TestActionHandler.throwException = false;
		}

		// TODO: figure out what happens to a process in case an ActionHandler throws an exception!!!!
//		assertRootTokenNodeEquals(processId, "middle");
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
								"    <transition to='middle' />" +
								"  </start-state>" +
								"  <state name='middle'>" +
								"    <transition to='end'>" +
								"      <action class='"+SpringActionHandlerDelegate.class.getName()+"' configType='bean'>" +
								"        <beanName>middleActionHandler</beanName>" +
								"      </action>" +
								"    </transition>" +
								"  </state>" +
								"  <end-state name='end' />" +
								"</process-definition>"
				);

		jbmp.deployProcessDefinition(processDefinition);
	}

	public long processInstanceIsCreatedWhenUserSubmitsWebappForm() {
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
			assertEquals("middle", token.getNode().getName());

			// Now the processInstance is saved in the database.  So the
			// current state of the execution of the process is stored in the
			// database.
//			jbpmContext.save(processInstance);
			// The method below will get the process instance back out
			// of the database and resume execution by providing another
			// external signal.

			return processInstance.getId();
		} finally {
			// Tear down the pojo persistence context.
			jbpmContext.close();
		}
	}

	public void theProcessInstanceContinuesWhenAnAsyncMessageIsReceived(long processId) {
		//The code in this method could be the content of a message driven bean.

		// Lookup the pojo persistence context-builder that is configured above
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			ProcessInstance processInstance = jbpmContext.loadProcessInstance(processId);

			// Now we can continue the execution.  Note that the processInstance
			// delegates signals to the main path of execution (=the root token).
			processInstance.signal();

			// After this signal, we know the process execution should have
			// arrived in the end-state.
			assertTrue(processInstance.hasEnded());

			// Now we can update the state of the execution in the database
//			jbpmContext.save(processInstance);

		} finally {
			// Tear down the pojo persistence context.
			jbpmContext.close();
		}
	}
	
	public void assertRootTokenNodeEquals(long processId, String nodeName) {
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processId);
//			assertFalse("is suspended",processInstance.isSuspended());
//			assertFalse("is terminated",processInstance.isTerminatedImplicitly());
			Token token = processInstance.getRootToken();
			final Node tokenNode = token.getNode();
			assertEquals(nodeName, tokenNode.getName());
		} finally {
			jbpmContext.close();
		}
	}

}
