package org.oaky.jbpm;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/integration-test-context.xml")
public class SimpleWorkflowTest {

	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	JbpmConfiguration jbpmConfiguration = null;

	JbpmTemplate jbmp;
	TransactionTemplate transactionTemplate;
	
	@Before
	public void setUp() {
		jbmp = new JbpmTemplate(jbpmConfiguration);
		transactionTemplate = new TransactionTemplate(transactionManager);
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
		final long processId = transactionTemplate.execute(new TransactionCallback<Long>() {
			public Long doInTransaction(TransactionStatus status) {
				return processInstanceIsCreatedWhenUserSubmitsWebappForm();
			}
		});

		// Then, later, upon the arrival of an asynchronous message the
		// execution must continue.
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				theProcessInstanceContinuesWhenAnAsyncMessageIsReceived(processId);
			}
		});

		assertTrue(TestActionHandler.isExecuted);
	}

	@Test
	public void testFailingActionHandler() {
//		final long processId = transactionTemplate.execute(new TransactionCallback<Long>() {
//			public Long doInTransaction(TransactionStatus status) {
//				return processInstanceIsCreatedWhenUserSubmitsWebappForm();
//			}
//		});

		final long processId = processInstanceIsCreatedWhenUserSubmitsWebappForm();

		// Then, later, upon the arrival of an asynchronous message the
		// execution must continue - this time the ActionHandler throws an exception.
		TestActionHandler.throwException = true;
		try {
//			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
//				protected void doInTransactionWithoutResult(TransactionStatus status) {
//					theProcessInstanceContinuesWhenAnAsyncMessageIsReceived(processId);
//				}
//			});
			theProcessInstanceContinuesWhenAnAsyncMessageIsReceived(processId);
			fail("expected exception");
		} catch(RuntimeException rex) {
			assertTrue("Expected test exception but was " + rex, rex.getMessage().contains("a test exception"));
		} finally {
			TestActionHandler.throwException = false;
		}

		// TODO: figure out what happens to a process in case an ActionHandler throws an exception!!!!
		assertRootTokenNodeEquals(processId, "middle");
	}

	public void deployProcessDefinition() {
		// This test shows a process definition and one execution
		// of the process definition.  The process definition has
		// 3 nodes: an unnamed start-state, a state 's' and an
		// end-state named 'end'.
		JbpmContext ctx = jbpmConfiguration.createJbpmContext();
		try {
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

			ctx.deployProcessDefinition(processDefinition);
		} finally {
			ctx.close();
		}
	}

	public long processInstanceIsCreatedWhenUserSubmitsWebappForm() {
		// The code in this method could be inside a struts-action
		// or a JSF managed bean.

		//Lookup the pojo persistence context-builder that is configured above
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			ProcessInstance processInstance = jbpmContext.newProcessInstanceForUpdate("hello world");

			Token token = processInstance.getRootToken();
			assertEquals("start", token.getNode().getName());
			// Let's start the process execution
			token.signal();
			// Now the process is in the state 's'.
			assertEquals("middle", token.getNode().getName());

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
			ProcessInstance processInstance = jbpmContext.loadProcessInstanceForUpdate(processId);

			// Now we can continue the execution.  Note that the processInstance
			// delegates signals to the main path of execution (=the root token).
			processInstance.signal();

			// After this signal, we know the process execution should have
			// arrived in the end-state.
			assertTrue(processInstance.hasEnded());

		} catch(RuntimeException ex) {
			// an exception occured, mark ambient transaction invalid
			jbpmContext.setRollbackOnly();
			throw ex;
		} finally {
			// Tear down the pojo persistence context.
			jbpmContext.close();
		}
	}
	
	public void assertRootTokenNodeEquals(long processId, String nodeName) {
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try {
			ProcessInstance processInstance = jbpmContext.getProcessInstanceForUpdate(processId);
			Token token = processInstance.getRootToken();
			final Node tokenNode = token.getNode();
			assertEquals(nodeName, tokenNode.getName());
		} finally {
			jbpmContext.close();
		}
	}

}
