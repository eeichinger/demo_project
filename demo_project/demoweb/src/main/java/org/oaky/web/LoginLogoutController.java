package org.oaky.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginLogoutController {

	public static final String LOGINFORM = "login";
	
	@RequestMapping("/login.do")
	public String showLoginForm() {
		return LOGINFORM;
	}
}
