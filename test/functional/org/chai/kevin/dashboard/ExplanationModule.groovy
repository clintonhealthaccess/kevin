package org.chai.kevin.dashboard

import org.chai.kevin.EntityFormModule;

import geb.Module;
import geb.error.RequiredPageContentNotPresent;

class ExplanationModule extends Module {

	static content = {
		explanationCell { $("div", id: "dashboard-explanation")}
	}
	
	def hasValues() {
		return explanationCell.find("span.value", text: contains("%"))
	}
	
}
