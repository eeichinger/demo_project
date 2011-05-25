package org.oaky.jbpm;

import org.hibernate.secure.JACCPermissions;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.junit.*;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SpringAwareActionHandlerDelegateTest {

	JbpmConfiguration jbpmConfiguration;
	
	@Before
	public void setUp() throws Exception {
		jbpmConfiguration = JbpmConfiguration.getInstance();
		JbpmContext ctx = jbpmConfiguration.createJbpmContext();
		String processDefinitionLocation = "SpringAwareActionHandlerDelegateTest-workflows.jpdl"; // '/' + this.getClass().getName().replace('.','/') + "-workflows.jpdl";
//		String processDefinitionLocation = '/' + this.getClass().getName().replace('.','/') + "-workflows.jpdl";
		InputStream inputStream = new ClassPathResource(processDefinitionLocation, this.getClass()).getInputStream();
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlInputStream(inputStream);
		ctx.deployProcessDefinition(processDefinition);
		TestActionHandler.isExecuted = false;
		ctx.close();
	}

	@After
	public void tearDown() throws Exception {
		if (jbpmConfiguration != null) {
			jbpmConfiguration.close();
		}
	}

	@Test
	public void testExecute_SunnyDay() throws Exception {
		JbpmContext ctx = jbpmConfiguration.createJbpmContext();
		String processDefinitionName = this.getClass().getName().replace('.','/') + "-workflows-sunnyday";
		ProcessInstance processInstance = ctx.newProcessInstance(processDefinitionName);
		processInstance.signal();
		ctx.close();
		assertTrue("TestActionHandler didnt execute", TestActionHandler.isExecuted);
	}

	@Test
	public void closing_jbpmConfiguration_should_close_active_springcontexts() {
		assertEquals(0, TestApplicationContextActive.activeContextCounter);
	}
}
