package org.chai.kevin.reports

import geb.Page;

class DashboardProgramEditPage extends Page {
	
	static url = "dashboardProgram/create?targetURI=%2FdashboardProgram%2Flist"
	
	static at = {
		title ==~ /Edit/
	}

	static content = {
		code { $('input', name: 'code') }
		program { $('select', name: 'program.id') }
		weight { $('input', name: 'weight') }
		save { $("button", type: "submit") }
	}
	
}
