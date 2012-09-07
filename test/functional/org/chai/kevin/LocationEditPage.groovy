package org.chai.kevin

import geb.Page;

class LocationEditPage extends Page {
	
	static url = "location/create?targetURI=%2Flocation%2Flist"
	
	static at = {
		title ==~ /Edit/
	}

	static content = {
		code { $('input', name: 'code') }
		level { $('select', name: 'level.id') }
		save { $("button", type: "submit") }
	}
	
}
