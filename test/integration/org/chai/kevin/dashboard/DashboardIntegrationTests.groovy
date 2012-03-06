package org.chai.kevin.dashboard

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportProgram
import org.hisp.dhis.period.Period;

abstract class DashboardIntegrationTests extends IntegrationTests {

	static String ROOT = "Root"
	static String PROGRAM = "Program"
	static String TARGET1 = "Target 1"
	static String TARGET2 = "Target 2"
		
	def newDashboardProgram(def code, def program) {
		return new DashboardProgram(code: code, program: program, weight: 1).save(failOnError: true)
	}
	
	def newDashboardProgram(def code, def program, def weight) {
		return new DashboardProgram(code: code, program: program, weight: weight).save(failOnError: true)
	}

	def newDashboardTarget(def code, def calculation, def parent, def weight) {
		def dashboardTarget = new DashboardTarget(code: code, calculation: calculation, program: parent, weight: weight).save(failOnError: true)	
		return dashboardTarget
	}
	
	def setupDashboard() {
		def period = Period.list()[0]
		
		def root = newReportProgram(ROOT)
		def dashboardRoot = newDashboardProgram(ROOT, root, 0)
		
		def program = newReportProgram(PROGRAM, root)
		def dashboardProgram = newDashboardProgram(PROGRAM, program, 1)

		def dataElement1 = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"40",(HEALTH_CENTER_GROUP):"40"]]))
		def average1 = newAverage("\$"+dataElement1.id, CODE(2))
		def target1 = newDashboardTarget(TARGET1, average1, program, 1)
		
		def dataElement2 = newNormalizedDataElement(CODE(3), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"20"]]))
		def average2 = newAverage("\$"+dataElement2.id, CODE(4))
		def target2 = newDashboardTarget(TARGET2, average2, program, 1)
	}
	
}
