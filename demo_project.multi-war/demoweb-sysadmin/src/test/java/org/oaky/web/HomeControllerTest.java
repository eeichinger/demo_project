package org.oaky.web;

import org.junit.Test;
import org.oaky.web.HomeController;

import static org.junit.Assert.assertEquals;

public class HomeControllerTest {
	@Test
	public void showHome_must_return_defaultView() {
		HomeController controller = new HomeController();
		String viewName = controller.showHome();
		assertEquals(HomeController.DEFAULT_VIEW, viewName);
	}
}
