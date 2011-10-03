package org.chai.kevin.dashboard

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;

abstract class DashboardIntegrationTests extends IntegrationTests {

	static String ROOT = "Root"
	static String OBJECTIVE = "Objective"
	static String TARGET1 = "Target 1"
	static String TARGET2 = "Target 2"
	
	def newDashboardObjective(def code) {
		return new DashboardObjective(root: true, names:[:], code: code).save(failOnError: true)
	}
	
	def newDashboardObjective(def code, def parent, def weight) {
		def dashboardObjective = new DashboardObjective(root: false, names:[:], code: code).save(failOnError: true)
		def dashboardObjectiveEntry = new DashboardObjectiveEntry(parent: parent, entry: dashboardObjective, weight: weight).save(failOnError: true)
		dashboardObjective.parent = dashboardObjectiveEntry
		parent.objectiveEntries << dashboardObjectiveEntry
		dashboardObjective.save(failOnError: true)
		parent.save(failOnError: true)
		return dashboardObjective
	}

	def newDashboardTarget(def code, def calculation, def parent, def weight) {
		def dashboardTarget = new DashboardTarget(names: [:], code: code, calculation: calculation).save(failOnError: true)
		def dashboardObjectiveEntry = new DashboardObjectiveEntry(parent: parent, entry: dashboardTarget, weight: weight).save(failOnError: true)
		dashboardTarget.parent = dashboardObjectiveEntry
		parent.objectiveEntries << dashboardObjectiveEntry
		dashboardTarget.save(failOnError: true)
		parent.save(failOnError: true)
		return dashboardTarget
	}
	
	def setupDashboard() {
		def root = newDashboardObjective(ROOT)
		def objective = newDashboardObjective(OBJECTIVE, root, 1)

		def average1 = newAverage([
				(DISTRICT_HOSPITAL_GROUP):newExpression(CODE(2), Type.TYPE_NUMBER(), "40"),
				(HEALTH_CENTER_GROUP):newExpression(CODE(3), Type.TYPE_NUMBER(), "40")
			], CODE(1), Type.TYPE_NUMBER())
		def target1 = newDashboardTarget(TARGET1, average1, objective, 1)
		
		def average2 = newAverage([
			(DISTRICT_HOSPITAL_GROUP):newExpression(CODE(4), Type.TYPE_NUMBER(), "20")
		], CODE(5), Type.TYPE_NUMBER())
		def target2 = newDashboardTarget(TARGET2, average1, objective, 1)
	}
	
}
