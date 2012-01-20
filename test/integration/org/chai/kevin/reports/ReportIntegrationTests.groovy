package org.chai.kevin.reports

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportObjective
import org.chai.kevin.util.Utils;

abstract class ReportIntegrationTests extends IntegrationTests {
	
//	def newReportTarget(def code, def sum, def format, def types, def objective) {
//		def target = new ReportTarget(names: [:], 
//			code: code, 
//			format: format, 
//			sum: sum, 
//			objective: objective, 
//			typeCodeString: Utils.unsplit(types)).save(failOnError: true)
//			
//		objective.targets << target
//		objective.save(failOnError: true)
//		return target
//	}
//		
//	def newReportTarget(def code, def sum, def types, def objective) {
//		return newReportTarget(code, sum, null, types, objective)
//	}
}
