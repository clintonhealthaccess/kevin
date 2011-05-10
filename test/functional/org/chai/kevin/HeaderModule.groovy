package org.chai.kevin;

import geb.Module;

public class HeaderModule extends Module {

	static content = {
		header { $('div', id: 'header') }
		navigation { header.find('div', id: 'navigation') }
	}
	
	def hasLink(def text) {
		return navigation.find("a", text: contains(text))
	}

	def click(def text) {
		navigation.find("a", text: contains(text)).click()
	}
	
}
