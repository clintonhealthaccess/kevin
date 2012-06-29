package org.chai.kevin.dashboard

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.Period;
import org.chai.kevin.reports.ReportIntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.value.Value

abstract class DashboardIntegrationTests extends IntegrationTests {
	
	public static final String VALUE_STRING = "value";
	
	static def setupDashboardTree(){
		def period = Period.list([cache: true])[0]
		def root = ReportProgram.findByCode(ROOT)
		def program1 = ReportProgram.findByCode(PROGRAM1)
		def program2 = ReportProgram.findByCode(PROGRAM2)
		def program3 = ReportProgram.findByCode(PROGRAM3)
		
		def dashboardRoot = newDashboardProgram(ROOT, root, 0)		
		def dashboardProgram1 = newDashboardProgram(PROGRAM1, program1, 1)
		def dashboardProgram2 = newDashboardProgram(PROGRAM2, program2, 1)
		def dashboardProgram3 = newDashboardProgram(PROGRAM3, program3, 1)
		
		def dataElement1 = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"40",(HEALTH_CENTER_GROUP):"40"]]))
		def ratio1 = newSum("\$"+dataElement1.id, CODE(2))
		def target1 = newDashboardTarget(TARGET1, ratio1, program1, 1)
		
		def dataElement2 = newNormalizedDataElement(CODE(3), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"20"]]))
		def ratio2 = newSum("\$"+dataElement2.id, CODE(4))
		def target2 = newDashboardTarget(TARGET2, ratio2, program1, 1)
		
		def dataElement3 = newNormalizedDataElement(CODE(5), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def ratio3 = newSum("\$"+dataElement3.id, CODE(6))
		def target3 = newDashboardTarget(TARGET3, ratio3, program2, 1)
		
		def dataElement4 = newNormalizedDataElement(CODE(7), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"0"]]))
		def ratio4 = newSum("\$"+dataElement4.id, CODE(8))
		def target4 = newDashboardTarget(TARGET4, ratio4, program3, 1)
		
	}
	
	static def newDashboardProgram(def code, def program) {
		return new DashboardProgram(code: code, program: program, weight: 1).save(failOnError: true)
	}

	static def newDashboardProgram(def code, def program, def weight) {
		return new DashboardProgram(code: code, program: program, weight: weight).save(failOnError: true)
	}
	
	static def newDashboardProgram(def code, def program, def weight, def order) {
		return new DashboardProgram(code: code, program: program, weight: weight, order: order).save(failOnError: true)
	}
	
	static def newDashboardTarget(def code, def calculation, def program, def weight) {
		def dashboardTarget = new DashboardTarget(code: code, calculation: calculation, program: program, weight: weight).save(failOnError: true)
		return dashboardTarget
	}
	
	static def newDashboardTarget(def code, def calculation, def program, def weight, def order) {
		def dashboardTarget = new DashboardTarget(code: code, calculation: calculation, program: program, weight: weight, order: order).save(failOnError: true)
		return dashboardTarget
	}
	
}