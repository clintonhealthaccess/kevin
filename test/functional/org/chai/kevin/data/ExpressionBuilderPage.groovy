package org.chai.kevin.data

import geb.Page;

class ExpressionBuilderPage extends Page {
	
	static url = "expression/test"
	
	static at = {
		title ==~ /Expression Builder/
	}

	static content = {
		expression { $('textarea', name: 'expression') }
		type { $('textarea', name: 'type') }
		locationType { $('select', name: 'typeCodes') }
		period { $('select', name: 'periodId') }
		save { $("button", type: "submit") }
	}
	
}
