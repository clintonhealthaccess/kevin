package org.chai.kevin.reports

import geb.Page;

class DsrCategoryEditPage extends Page {
	
	static url = "dsrTargetCategory/create?targetURI=%2FdsrTargetCategory%2Flist"
	
	static at = {
		title ==~ /Edit/
	}

	static content = {
		code { $('input', name: 'code') }
		save { $("button", type: "submit") }
	}
	
}
