package org.chai.kevin.data

import geb.Page;

class SourceEditPage extends Page {
	
	static url = "source/create?targetURI=%2Fsource%2Flist"
	
	static at = {
		title ==~ /Edit/
	}

	static content = {
		code { $('input', name: 'code') }
		save { $("button", type: "submit") }
	}
	
}
