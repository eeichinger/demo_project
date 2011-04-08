package org.oaky.web;

import org.oaky.service.ManagePermissionsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SecuredActionController {

	private final ManagePermissionsFacade facade;

	@Autowired
	public SecuredActionController(ManagePermissionsFacade facade) {

		this.facade = facade;
	}
	
	@RequestMapping("/forbiddenforguests.do")
	public String userOnlyMethod() {
		facade.doSomethingSensitive();
		return "home";
	}
}
