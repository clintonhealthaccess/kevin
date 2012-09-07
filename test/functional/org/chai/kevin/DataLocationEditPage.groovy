package org.chai.kevin

import geb.Page;

class DataLocationEditPage extends Page {
	
	static url = "dataLocation/create?targetURI=%2FdataLocation%2Flist"
	
	static at = {
		title ==~ /Edit/
	}

	static content = {
		code { $('input', name: 'code') }
		type { $('select', name: 'type.id') }
		locationContainer(required: false) { $('div', 'class': 'chzn-container') }
		locationInput(required: false) { $('.chzn-drop input') }
		location { $('select', name: 'location.id') }
		save { $("button", type: "submit") }
	}
	
}
