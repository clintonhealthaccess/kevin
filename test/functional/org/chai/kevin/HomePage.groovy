package org.chai.kevin

import geb.Page;

class HomePage extends Page {
	
	static url = "home/landingPage"
	
	static at = {
		title ==~ /DHSST - Welcome/
	}

	static content = {
		
	}
	
}
