package org.chai.kevin.dashboard

import org.chai.kevin.CreateExpressionModule;
import org.chai.kevin.EntityFormWithExpressionsModule;
import org.chai.kevin.EntityFormModule;

import geb.Module;
import geb.error.RequiredPageContentNotPresent;

class CreateDashboardTargetModule extends EntityFormWithExpressionsModule {

	static content = {
		entityFormContainer { $("div", id:"add-dashboard-target") }
		weightField { entityFormContainer.find("input", name: "weight") }
		codeField { entityFormContainer.find("input", name: "entry.code") }
		nameField { entityFormContainer.find("input", name: "entry.names.en") }
		orderField { entityFormContainer.find("input", name: "order") }
	}
	
}
