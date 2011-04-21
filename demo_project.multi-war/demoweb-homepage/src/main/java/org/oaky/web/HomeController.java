package org.oaky.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	public final static String DEFAULT_VIEW = "home";
	
	@RequestMapping(value="/")
	public String showHome() {
		return DEFAULT_VIEW;
	}
}
