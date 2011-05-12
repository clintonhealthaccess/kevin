package org.chai.kevin.dashboard

import org.chai.kevin.KevinPage;
import org.chai.kevin.ReportPage;

import geb.Page;
import geb.error.RequiredPageContentNotPresent;

class DashboardPage extends ReportPage {
	
    static at = { title == "Dashboard" }
	static url = "/kevin/dashboard/view"
	
	static content = {
		dashboard { $("div", id:"values").find("table") }
		dashboardHeader { dashboard.find(".header") }
		dashboardCells { dashboard.find("td.value") }
		explanation(required: false) {module ExplanationModule }

		addTarget { $("a", id:"add-dashboard-target-link") }
		addObjective { $("a", id:"add-dashboard-objective-link") }

		createTarget(required: false) { module CreateDashboardTargetModule }
		createObjective(required: false) { module CreateDashboardObjectiveModule }
				
		refresh { $("a", text: contains("refresh")) }
	}
	
	def clickOnFirstCell() {
		dashboardCells.first().jquery.click()
		waitFor {
			explanation.present
		}
		waitFor {
			Thread.sleep 1000
			true
		}
	}
	
	def hasValues() {
		return dashboardCells.find("span a", text: contains("%"))
	}
	
	def clickRefresh() {
		refresh.click()
	}
		
	def getObjective(def text) {
		return dashboardHeader.find("a", text: contains(text))
	}
	
	def getTarget(def text) {
		return dashboardHeader.find("span", text: contains(text))
	}
	
}