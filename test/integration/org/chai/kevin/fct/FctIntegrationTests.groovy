package org.chai.kevin.fct

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportObjective
import org.chai.kevin.util.Utils;

abstract class FctIntegrationTests extends IntegrationTests {
	
	def newFctTarget(def code, def sum, def format, def types, def objective) {
		def target = new FctTarget(names: [:], 
			code: code, 
			format: format, 
			sum: sum, 
			objective: objective, 
			typeCodeString: Utils.unsplit(types)).save(failOnError: true)
			
//		objective.targets << target
		objective.save(failOnError: true)
		return target
	}
		
	def newFctTarget(def code, def sum, def types, def objective) {
		return newFctTarget(code, sum, null, types, objective)
	}
}
