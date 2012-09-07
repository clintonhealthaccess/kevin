package org.chai.kevin

import geb.Page;

class LocationLevelEditPage extends Page {
	
	static url = "locationLevel/create?targetURI=%2FlocationLevel%2Flist"
	
	static at = {
		title ==~ /Edit/
	}

	static content = {
		code { $('input', name: 'code') }
		order { $('input', name: 'order') }
		save { $("button", type: "submit") }
	}
	
}
