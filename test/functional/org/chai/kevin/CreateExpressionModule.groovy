package org.chai.kevin

import geb.Module;

class CreateExpressionModule extends EntityFormModule {

	static content = {
		entityFormContainer { $("div", id:"add-expression") }
		nameField { entityFormContainer.find("input", name: "name") }
		expressionField { entityFormContainer.find("textarea", name: "expression") }
		shortNameField { entityFormContainer.find("input", name: "shortName") }
		searchButton { $("form", name:"search-data-form").find("button", type: "submit") }
		dataElements { $("ul", id:"data") }
	}

	def searchDataElement() {
		searchButton.jquery.click()
	}
	
	def hasDataElements() {
		return dataElements.find("li").size() > 0;
	}
	
}
