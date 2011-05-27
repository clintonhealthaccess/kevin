package org.chai.kevin.cost

import org.chai.kevin.CreateExpressionModule;
import org.chai.kevin.EntityFormWithExpressionsModule;
import org.chai.kevin.EntityFormModule;

import geb.Module;
import geb.error.RequiredPageContentNotPresent;

class CreateCostObjectiveModule extends EntityFormWithExpressionsModule {

	static content = {
		entityFormContainer { $("div", id:"add-cost-objective") }
		codeField { entityFormContainer.find("input", code: "code") }
		nameField { entityFormContainer.find("input", name: "names.en") }
		orderField { entityFormContainer.find("input", name: "order") }
		
	}

}
