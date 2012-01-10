package org.chai.kevin.reports

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportObjective
import org.chai.kevin.util.Utils;

abstract class ReportIntegrationTests extends IntegrationTests {
	
//	def newReportTarget(def code, def sum, def format, def groups, def objective) {
//		def target = new ReportTarget(names: [:], 
//			code: code, 
//			format: format, 
//			sum: sum, 
//			objective: objective, 
//			groupUuidString: Utils.unsplit(groups)).save(failOnError: true)
//			
//		objective.targets << target
//		objective.save(failOnError: true)
//		return target
//	}
//		
//	def newReportTarget(def code, def sum, def groups, def objective) {
//		return newReportTarget(code, sum, null, groups, objective)
//	}
}
