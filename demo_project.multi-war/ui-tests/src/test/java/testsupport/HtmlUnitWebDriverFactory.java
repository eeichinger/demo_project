package testsupport;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class HtmlUnitWebDriverFactory implements WebDriverFactory {

	private final String baseUrl;

	public HtmlUnitWebDriverFactory(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public WebDriver getWebDriver() {
		return new HtmlUnitDriver() {
			@Override
			public void get(String url) {
				super.get(baseUrl + url);
			}
		};
	}
}
