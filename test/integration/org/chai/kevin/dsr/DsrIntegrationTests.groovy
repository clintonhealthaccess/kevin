package org.chai.kevin.dsr

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.util.Utils;

abstract class DsrIntegrationTests extends IntegrationTests {
	
	def newDsrTarget(def code, def dataElement, def format, def types, def program) {
		return newDsrTarget(code, dataElement, format, types, program, null)
	}	
	
	def newDsrTarget(def code, def dataElement, def types, def program, DsrTargetCategory category) {
		return newDsrTarget(code, dataElement, null, types, program, category)
	}
	
	static def newDsrTargetCategory(def code) {
		return new DsrTargetCategory(code: code).save(failOnError: true)
	}
}
