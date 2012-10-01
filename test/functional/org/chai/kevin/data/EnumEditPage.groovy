package org.chai.kevin.data

import geb.Page;

class EnumEditPage extends Page {
	
	static url = "enum/create?targetURI=%2Fenum%2Flist"
	
	static at = {
		title ==~ /Edit/
	}

	static content = {
		code { $('input', name: 'code') }
		save { $("button", type: "submit") }
	}
	
}
