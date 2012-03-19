package org.chai.kevin.dashboard

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.reports.ReportIntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.value.Value
import org.hisp.dhis.period.Period;

abstract class DashboardIntegrationTests extends IntegrationTests {

	static String ROOT = "Root"
	
	static String PROGRAM1 = "Program1"
	static String TARGET1 = "Target 1"
	static String TARGET2 = "Target 2"
	
	static String PROGRAM2 = "Program2"
	static String TARGET3 = "Target 3"
	
	public static final String VALUE_STRING = "value";
	
	static def newDashboardProgram(def code, def program) {
		return new DashboardProgram(code: code, program: program, weight: 1).save(failOnError: true)
	}
	
	static def newDashboardProgram(def code, def program, def weight) {
		return new DashboardProgram(code: code, program: program, weight: weight).save(failOnError: true)
	}

	static def newDashboardTarget(def code, def calculation, def parent, def weight) {
		def dashboardTarget = new DashboardTarget(code: code, calculation: calculation, program: parent, weight: weight).save(failOnError: true)	
		return dashboardTarget
	}
	
	static def newDashboardPercentage(def value){
		def dashboardPercentage = new DashboardPercentage(new Value("{\""+VALUE_STRING+"\":"+value+"}"), null, null)
		return dashboardPercentage
	}
	
	static def setupDashboard() {
		def period = Period.list()[0]
		
		def root = newReportProgram(ROOT)
		def dashboardRoot = newDashboardProgram(ROOT, root, 0)
		
		def program1 = newReportProgram(PROGRAM1, root)
		def dashboardProgram1 = newDashboardProgram(PROGRAM1, program1, 1)

		def program2 = newReportProgram(PROGRAM2, root)
		def dashboardProgram2 = newDashboardProgram(PROGRAM2, program2, 1)
		
		def dataElement1 = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"40",(HEALTH_CENTER_GROUP):"40"]]))
		def average1 = newAverage("\$"+dataElement1.id, CODE(2))
		def target1 = newDashboardTarget(TARGET1, average1, program1, 1)
		
		def dataElement2 = newNormalizedDataElement(CODE(3), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"20"]]))
		def average2 = newAverage("\$"+dataElement2.id, CODE(4))
		def target2 = newDashboardTarget(TARGET2, average2, program1, 1)
		
		def dataElement3 = newNormalizedDataElement(CODE(5), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def average3 = newAverage("\$"+dataElement3.id, CODE(6))
		def target3 = newDashboardTarget(TARGET2, average3, program2, 1)
	}	
	
	def getPercentage(def percentage) {
		if(percentage != null && percentage.isValid())
			return percentage.getRoundedValue();
		else
			return null;
	}
}