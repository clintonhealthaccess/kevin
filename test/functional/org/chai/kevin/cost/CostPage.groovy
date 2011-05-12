package org.chai.kevin.cost

import org.chai.kevin.KevinPage;
import org.chai.kevin.ReportPage;

class CostPage extends ReportPage {

	static at = { title == "Costing" }
	static url = "/kevin/cost/view"
	
	static content = {
		addTarget { $("a", id:"add-cost-target-link") }
		addObjective { $("a", id:"add-cost-objective-link") }
		
		costTable { $('div', id:'values') }
		
		createTarget(required: false) { module CreateCostTargetModule }
		createObejctive(required: false) { module CreateCostObjectiveModule }
		
		periodFilter { $('div.filter')[0] }
		organisationFilter { $('div.filter')[1] }
		objectiveFilter { $('div.filter')[2] }
		
	}
	
	def getTarget(def text) {
		costTable.find('tbody th.label', text: contains(text))
	}
	
	def pickObjective(def text) {
		objectiveFilter.find('a').first().click();
		waitFor {
			objectiveFilter.find('div.dropdown-list').displayed
		}
		objectiveFilter.find('li a', text: contains(text)).click()
	}
	
	def pickOrganisation(def text) {
		organisationFilter.find('a').first().click();
		waitFor {
			organisationFilter.find('div.dropdown-list').displayed
		}
		organisationFilter.find('li a', text: contains(text)).click()
	}
	
}
