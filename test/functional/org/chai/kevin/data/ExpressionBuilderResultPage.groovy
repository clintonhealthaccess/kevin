package org.chai.kevin.data

import geb.Page;

class ExpressionBuilderResultPage extends Page {
	
	static url = "expression/doTest"
	
	static at = {
		title ==~ /Data Value List/
	}

	static content = {
		tableBody { $('table', 'class': 'listing').find('tbody') }
	}
	
}
