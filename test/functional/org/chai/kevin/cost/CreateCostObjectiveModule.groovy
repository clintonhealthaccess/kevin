package org.chai.kevin.cost

import org.chai.kevin.CreateExpressionModule;
import org.chai.kevin.EntityFormWithExpressionsModule;
import org.chai.kevin.EntityFormModule;

import geb.Module;
import geb.error.RequiredPageContentNotPresent;

class CreateCostObjectiveModule extends EntityFormWithExpressionsModule {

	static content = {
		entityFormContainer { $("div", id:"add-cost-objective") }
		nameField { $("input", name: "name") }
		orderField { $("input", name: "order") }
		
	}

}
