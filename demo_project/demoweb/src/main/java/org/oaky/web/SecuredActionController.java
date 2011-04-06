package org.oaky.web;

import org.oaky.service.ForbiddenForGuestsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SecuredActionController {

	private final ForbiddenForGuestsFacade facade;

	@Autowired
	public SecuredActionController(ForbiddenForGuestsFacade facade) {

		this.facade = facade;
	}
	
	@RequestMapping("/forbiddenforguests.do")
	public void userOnlyMethod() {
		facade.doSomethingSensitive();
	}
}
