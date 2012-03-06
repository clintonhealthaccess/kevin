package org.chai.kevin.dsr

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.util.Utils;

abstract class DsrIntegrationTests extends IntegrationTests {
	
	def newDsrTarget(def code, def dataElement, def format, def types, def program) {
		def target = new DsrTarget(names: [:], 
			code: code, 
			format: format, 
			dataElement: dataElement, 
			program: program, 
			typeCodeString: Utils.unsplit(types)).save(failOnError: true)
			
//		program.targets << target
		program.save(failOnError: true)
		return target
	}
		
	def newDsrTarget(def code, def dataElement, def types, def program) {
		return newDsrTarget(code, dataElement, null, types, program)
	}
}
