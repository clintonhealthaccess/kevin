package org.chai.kevin.reports

import geb.Page;

class ReportProgramEditPage extends Page {
	
	static url = "reportProgram/create?targetURI=%2FreportProgram%2Flist"
	
	static at = {
		title ==~ /Edit/
	}

	static content = {
		code { $('input', name: 'code') }
		save { $("button", type: "submit") }
	}
	
}
