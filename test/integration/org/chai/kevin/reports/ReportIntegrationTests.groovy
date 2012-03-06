package org.chai.kevin.reports

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.util.Utils;

abstract class ReportIntegrationTests extends IntegrationTests {
	
//	def newReportTarget(def code, def sum, def format, def types, def program) {
//		def target = new ReportTarget(names: [:], 
//			code: code, 
//			format: format, 
//			sum: sum, 
//			program: program, 
//			typeCodeString: Utils.unsplit(types)).save(failOnError: true)
//			
//		program.targets << target
//		program.save(failOnError: true)
//		return target
//	}
//		
//	def newReportTarget(def code, def sum, def types, def program) {
//		return newReportTarget(code, sum, null, types, program)
//	}
}
