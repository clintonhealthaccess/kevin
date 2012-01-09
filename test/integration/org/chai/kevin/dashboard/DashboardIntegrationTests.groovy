package org.chai.kevin.dashboard

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportObjective
import org.hisp.dhis.period.Period;

abstract class DashboardIntegrationTests extends IntegrationTests {

	static String ROOT = "Root"
	static String OBJECTIVE = "Objective"
	static String TARGET1 = "Target 1"
	static String TARGET2 = "Target 2"
		
	def newDashboardObjective(def code, def objective) {
		return new DashboardObjective(code: code, objective: objective).save(failOnError: true)
	}
	
	def newDashboardObjective(def code, def objective, def weight) {
		return new DashboardObjective(code: code, objective: objective, weight: weight).save(failOnError: true)
	}

	def newDashboardTarget(def code, def calculation, def parent, def weight) {
		def dashboardTarget = new DashboardTarget(code: code, calculation: calculation, objective: parent).save(failOnError: true)	
		dashboardTarget.save(failOnError: true)
//		parent.save(failOnError: true)
		return dashboardTarget
	}
	
	def setupDashboard() {
		def period = Period.list()[0]
		
		def root = newReportObjective(ROOT)
		def dashboardRoot = newDashboardObjective(ROOT, root, 0)
		
		def objective = newReportObjective(OBJECTIVE, root)
		def dashboardObjective = newDashboardObjective(OBJECTIVE, objective, 1)

		def dataElement1 = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"40",(HEALTH_CENTER_GROUP):"40"]]))
		def average1 = newAverage("\$"+dataElement1.id, CODE(2))
		def target1 = newDashboardTarget(TARGET1, average1, objective, 1)
		
		def dataElement2 = newNormalizedDataElement(CODE(3), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"20"]]))
		def average2 = newAverage("\$"+dataElement2.id, CODE(4))
		def target2 = newDashboardTarget(TARGET2, average2, objective, 1)
	}
	
}
