package org.chai.kevin.cost

import org.chai.kevin.CreateExpressionModule;
import org.chai.kevin.EntityFormWithExpressionsModule;
import org.chai.kevin.EntityFormModule;

import geb.Module;
import geb.error.RequiredPageContentNotPresent;

class CreateCostTargetModule extends EntityFormWithExpressionsModule {

	static content = {
		entityFormContainer { $("div", id:"add-cost-target") }
		nameField { entityFormContainer.find("input", name: "name") }
		orderField { entityFormContainer.find("input", name: "order") }
	}

}
