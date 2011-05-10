package org.chai.kevin.cost

import org.chai.kevin.KevinPage;
import org.chai.kevin.ReportPage;

class CostPage extends ReportPage {

	static at = { title == "Costing" }
	static url = "/kevin/cost/view"
	
	static content = {
		addTarget { $("a", id:"add-cost-target-link") }
		addObjective { $("a", id:"add-cost-objective-link") }
		
		createTarget(required: false) { module CreateCostTargetModule }
		createObejctive(required: false) { module CreateCostObjectiveModule }
	}
	
}
