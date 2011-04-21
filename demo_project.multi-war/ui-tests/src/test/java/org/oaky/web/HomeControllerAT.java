package org.oaky.web;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import testsupport.WebDriverFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/acceptance-test-context.xml")
public class HomeControllerAT {

	@Autowired
	private WebDriverFactory driverFactory;

	private WebDriver driver;
	
	@Before
	public void setup() {
		driver = driverFactory.getWebDriver();
	}

	@After
	public void tearDown() {
		driver.quit();
	}
	
	@Test
	public void unauthenticated_homepage_request_must_display_login() {
		driver.get("home.do");
		final String pageSource = driver.getPageSource();
        String expectedMessage="Log In";
        Assert.assertTrue("Page doesnt contain expected message: '" + expectedMessage + "', was:\n" + pageSource, pageSource.contains(expectedMessage));
	}

	@Test
	public void authenticated_homepage_request_must_display_homemessage() {
		driver.get("home.do");

		WebElement username = driver.findElement(By.name("j_username"));
		WebElement password = driver.findElement(By.name("j_password"));
		username.sendKeys("guest");
		password.sendKeys("guest");
		driver.findElement(By.name("submit")).click();
		final String pageSource = driver.getPageSource();
        String expectedMessage="up+running";
		Assert.assertTrue("Page doesnt contain expected message: '" + expectedMessage + "', was:\n" + pageSource, pageSource.contains(expectedMessage));
	}
}
