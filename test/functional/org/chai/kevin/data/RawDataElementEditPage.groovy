package org.chai.kevin.data

import geb.Page;

class RawDataElementEditPage extends Page {
	
	static url = "rawDataElement/create?targetURI=%2FrawDataElement%2Flist"
	
	static at = {
		title ==~ /Edit/
	}

	static content = {
		code { $('input', name: 'code') }
		type { $('textarea', name: 'type.jsonValue') }
		source { $('select', name: 'source.id') }
		save { $("button", type: "submit") }
	}
	
}
