package org.chai.kevin

import geb.Module;

class CreateConstantModule extends EntityFormModule {

	static content = {
		entityFormContainer { $("div", id:"add-constant") }
		nameField { entityFormContainer.find("input", name: "name") }
		shortNameField { entityFormContainer.find("input", name: "shortName") }
		valueField { entityFormContainer.find("input", name: "value") }
	}

}
