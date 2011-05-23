//package org.oaky.jbpm;
//
//import org.jbpm.JbpmConfiguration;
//import org.jbpm.JbpmContext;
//import org.jbpm.graph.def.ProcessDefinition;
//import org.springframework.util.Assert;
//
//public class JbpmTemplate {
//
//	private final JbpmConfiguration cfg;
//
//	public JbpmTemplate(JbpmConfiguration cfg) {
//		Assert.notNull(cfg, "template requires valid jbpmconfiguration instance");
//		this.cfg = cfg;
//	}
//
//	public void deployProcessDefinition(final ProcessDefinition pd) {
//		doInContext(new Cmd() {
//			public void execute(JbpmContext ctx) {
//				ctx.deployProcessDefinition(pd);
//			}
//		});
//	}
//
//	public void doInContext(Cmd cmd) {
//		JbpmContext jbpmContext = cfg.createJbpmContext();
//		try {
//			cmd.execute(jbpmContext);
//		} finally {
//			// Tear down the pojo persistence context.
//			// This includes flush the SQL for inserting the process definition
//			// to the database.
//			jbpmContext.close();
//		}
//	}
//
//	public interface Cmd {
//		void execute(JbpmContext ctx);
//	}
//}
