package org.oaky.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

	public final static String DEFAULT_VIEW = "home";
	
	@RequestMapping(value="/home.do")
	public String showHome() {
		return DEFAULT_VIEW;
	}
}
