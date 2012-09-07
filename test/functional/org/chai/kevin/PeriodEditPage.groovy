package org.chai.kevin

import geb.Page;

class PeriodEditPage extends Page {
	
	static url = "period/create?targetURI=%2Fperiod%2Flist"
	
	static at = {
		title ==~ /Edit/
	}

	static content = {
		code { $('input', name: 'code') }
		endDateDay { $('select', name: 'endDate_day') }
		save { $("button", type: "submit") }
	}
	
}
