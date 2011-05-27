package org.chai.kevin

import geb.Module;

class CreateExpressionModule extends EntityFormModule {

	static content = {
		entityFormContainer { $("div", id:"add-expression") }
		nameField { entityFormContainer.find("input", name: "names.en") }
		expressionField { entityFormContainer.find("textarea", name: "expression") }
		codeField { entityFormContainer.find("input", name: "code") }
		searchButton { entityFormContainer.find("form", name:"search-data-form").find("button", type: "submit") }
		dataElements { entityFormContainer.find("ul", id:"data") }
	}

	def searchDataElement() {
		searchButton.jquery.click()
		waitFor {
			hasDataElements()
		}
		waitFor {
			Thread.sleep(1000)
			true
		}
	}
	
	def hasDataElements() {
		return dataElements.find("li").size() > 0;
	}
	
}
