package testsupport;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.jndi.SimpleNamingContext;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;

public class SpringJndiContextConfigurerTest {

	public static class TestInitialContextFactory implements InitialContextFactory {
		public Context getInitialContext(Hashtable environment) throws NamingException {
			Hashtable boundObjects = new Hashtable();
			boundObjects.put("TESTROOT/self", "THIS");
			return new SimpleNamingContext("TESTROOT/", boundObjects, environment);
		}
	}

	@Test
	public void useRightContext() throws Exception {

		GenericApplicationContext appCtx = new GenericApplicationContext();
		SpringJndiContextConfigurer cfg = new SpringJndiContextConfigurer();
		cfg.postProcessBeanFactory(appCtx.getBeanFactory());

		Hashtable env = new Hashtable();
		env.put("java.naming.factory.initial", TestInitialContextFactory.class.getName());
		env.put("java.naming.provider.url", "ldap://localhost:389/");
		InitialContext ctx = new InitialContext(env);
		Assert.assertEquals("THIS", ctx.lookup("self"));
	}
}
