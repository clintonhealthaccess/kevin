package org.chai.kevin.reports

import geb.Page;

class DashboardTargetEditPage extends Page {
	
	static url = "dashboardTarget/create?targetURI=%2FdashboardTarget%2Flist"
	
	static at = {
		title ==~ /Edit/
	}

	static content = {
		code { $('input', name: 'code') }
		weight { $('input', name: 'weight') }
		program { $('select', name: 'program.id') }
		calculationContainer(required: false) { $('div', 'class': 'chzn-container') }
		calculationInput(required: false) { $('.chzn-drop input') }
		calculation { $('select', name: 'data.id') }
		save { $("button", type: "submit") }
	}
	
}
