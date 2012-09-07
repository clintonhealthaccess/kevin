package org.chai.kevin

import geb.Page;

class DataLocationTypeEditPage extends Page {
	
	static url = "dataLocationType/create?targetURI=%2FdataLocationType%2Flist"
	
	static at = {
		title ==~ /Edit/
	}

	static content = {
		code { $('input', name: 'code') }
		save { $("button", type: "submit") }
	}
	
}
