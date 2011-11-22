package org.chai.kevin.dsr

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.Utils;

abstract class DsrIntegrationTests extends IntegrationTests {


	def newDsrObjective(def code) {
		return new DsrObjective(code: code, names: [:]).save(failOnError: true);
	}
	
	def newDsrTarget(def code, def dataElement, def format, def groups, def objective) {
		def target = new DsrTarget(names: [:], 
			code: code, 
			format: format, 
			dataElement: dataElement, 
			objective: objective, 
			groupUuidString: Utils.unsplit(groups)).save(failOnError: true)
			
		objective.targets << target
		objective.save(failOnError: true)
		return target
	}
		
	def newDsrTarget(def code, def expression, def groups, def objective) {
		return newDsrTarget(code, expression, null, groups, objective)
	}
}
