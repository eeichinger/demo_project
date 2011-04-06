package featuresteps;

import cuke4duke.annotation.I18n.EN.Given;
import cuke4duke.annotation.I18n.EN.Then;
import cuke4duke.annotation.I18n.EN.When;
import org.oaky.service.ForbiddenForGuestsFacade;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import testsupport.SecurityHelper;

@Component
public class PreventFraudSteps {

	@Autowired
	public ForbiddenForGuestsFacade forbiddenForGuestsFacade;

	private Exception resultException;
	
	@Given("^a (.*) user$")
	public void aUserInRole(String role) {
		if("anonymous".equalsIgnoreCase(role)) {
			return;
		}
		if ("authorized".equalsIgnoreCase(role)) {
			role = "user";
		}
		SecurityHelper.authenticateSecurityContext(null, null, ("ROLE_" + role).toUpperCase());
	}

	@When("^a facade function is used$")
	public void aFacadeFunctionIsUsed() {
		try {
			forbiddenForGuestsFacade.doSomethingSensitive();
		} catch (Exception e) {
			resultException = e;
		}
	}

	@Then("^the system must throw a security exception$")
	public void theSystemMustThrowASecurityException() {
		Assert.assertNotNull(resultException);
	}

	@Then("^the system will proceed normal$")
	public void theSystemMustNotThrowASecurityException() {
		Assert.assertNull(resultException);
	}
}
