package org.chai.kevin;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import grails.plugin.geb.GebSpec;


abstract class GebTests extends GebSpec {

	WebDriver createDriver() {
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.setJavascriptEnabled(true);
		return driver;
    }
	
//	WebDriver createDriver() {
//		return new ChromeDriver();
//	}
	
}
