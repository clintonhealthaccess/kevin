package org.chai.kevin.reports

import geb.Page;

class FctTargetEditPage extends Page {
	
	static url = "fctTarget/create?targetURI=%2FfctTarget%2Flist"
	
	static at = {
		title ==~ /Edit/
	}

	static content = {
		code { $('input', name: 'code') }
		program { $('select', name: 'program.id') }
		save { $("button", type: "submit") }
	}
	
}
