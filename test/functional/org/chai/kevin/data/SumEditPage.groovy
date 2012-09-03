package org.chai.kevin.data

import geb.Page;

class SumEditPage extends Page {
	
	static url = "sum/create?targetURI=%2Fcalculation%2Flist"
	
	static at = {
		title ==~ /Edit/
	}

	static content = {
		code { $('input', name: 'code') }
		expression { $('textarea', name: 'expression') }
		save { $("button", type: "submit") }
	}
	
}
