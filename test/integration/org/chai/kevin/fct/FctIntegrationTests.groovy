package org.chai.kevin.fct

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.util.Utils;

abstract class FctIntegrationTests extends IntegrationTests {
	
	def newFctTarget(def code, def sum, def format, def types, def program) {
		def target = new FctTarget(names: [:], 
			code: code, 
			format: format, 
			sum: sum, 
			program: program, 
			typeCodeString: Utils.unsplit(types)).save(failOnError: true)
			
//		program.targets << target
		program.save(failOnError: true)
		return target
	}
		
	def newFctTarget(def code, def sum, def types, def program) {
		return newFctTarget(code, sum, null, types, program)
	}
}
