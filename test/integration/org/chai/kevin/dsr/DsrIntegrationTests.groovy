package org.chai.kevin.dsr

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportObjective
import org.chai.kevin.util.Utils;

abstract class DsrIntegrationTests extends IntegrationTests {
	
	def newDsrTarget(def code, def dataElement, def format, def types, def objective) {
		def target = new DsrTarget(names: [:], 
			code: code, 
			format: format, 
			dataElement: dataElement, 
			objective: objective, 
			typeCodeString: Utils.unsplit(types)).save(failOnError: true)
			
//		objective.targets << target
		objective.save(failOnError: true)
		return target
	}
		
	def newDsrTarget(def code, def dataElement, def types, def objective) {
		return newDsrTarget(code, dataElement, null, types, objective)
	}
}
