package org.chai.kevin.reports

import geb.Page;

class FctTargetOptionEditPage extends Page {
	
	static url = "fctTargetOption/create?targetURI=%2FfctTargetOption%2Flist"
	
	static at = {
		title ==~ /Edit/
	}

	static content = {
		code { $('input', name: 'code') }
		target { $('select', name: 'target.id') }
		sumContainer(required: false) { $('div', 'class': 'chzn-container') } 
		sumInput(required: false) { $('.chzn-drop input') }
		sum { $('select', name: 'data.id') }
		save { $("button", type: "submit") }
	}
	
}
