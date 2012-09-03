package org.chai.kevin.reports

import geb.Page;

class DsrTargetEditPage extends Page {
	
	static url = "dsrTarget/create?targetURI=%2FdsrTarget%2Flist"
	
	static at = {
		title ==~ /Edit/
	}

	static content = {
		code { $('input', name: 'code') }
		program { $('select', name: 'program.id') }
		category { $('select', name: 'category.id') }
		dataContainer(required: false) { $('div', 'class': 'chzn-container') }
		dataInput(required: false) { $('.chzn-drop input') }
		data { $('select', name: 'data.id') }
		save { $("button", type: "submit") }
	}
	
}
