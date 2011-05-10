package org.chai.kevin.dashboard

import org.chai.kevin.EntityFormModule;

import geb.Module;

class CreateDashboardObjectiveModule extends EntityFormModule {

	static content = {
		entityFormContainer { $("div", id:"add-dashboard-objective") }
		weightField { entityFormContainer.find("input", name: "weight") }
		nameField { entityFormContainer.find("input", name: "entry.name") }
		orderField { entityFormContainer.find("input", name: "order") }
	}
}
