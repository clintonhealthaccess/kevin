package org.chai.kevin

import geb.Module;

class CreateConstantModule extends EntityFormModule {

	static content = {
		entityFormContainer { $("div", id:"add-constant") }
		nameField { entityFormContainer.find("input", name: "names.en") }
		codeField { entityFormContainer.find("input", name: "code") }
		valueField { entityFormContainer.find("input", name: "value") }
	}

}
