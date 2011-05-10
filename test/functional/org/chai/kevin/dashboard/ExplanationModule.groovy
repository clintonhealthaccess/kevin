package org.chai.kevin.dashboard

import org.chai.kevin.EntityFormModule;

import geb.Module;
import geb.error.RequiredPageContentNotPresent;

class ExplanationModule extends Module {

	static content = {
		explanationCell { $("div", id: "explanation")}
	}
	
	def hasValues() {
		return 
			dashboard.find("div.value", text: contains("%")) ||
			dashboard.find("div.value", text: contains("N/A"))
	}
	
}
