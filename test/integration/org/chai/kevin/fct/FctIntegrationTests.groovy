package org.chai.kevin.fct

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.location.DataLocationType;
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.util.Utils;

abstract class FctIntegrationTests extends IntegrationTests {

//	static def newFctTarget(def names, def code, def program) {
//		
//	}
	
	static def newFctTarget(def code, def program) {
		return newFctTarget(code, null, program)
	}
	
	static def newFctTarget(def code, def order, def program) {
		return new FctTarget(code: code, order: order, program: program).save(failOnError: true)
	}

	static def newFctTargetOption(def code, def order, def target, def sum) {
		newFctTargetOption(code, null, order, target, sum)
	}
	
	static def newFctTargetOption(def code, def target, def sum) {
		newFctTargetOption(code, null, null, target, sum)
	}
	
	
	static def newFctTargetOption(def code, Map names, def target, def sum) {
		newFctTargetOption(code, names, null, target, sum)
	}
		
	static def newFctTargetOption(def code, def names, def order, def target, def sum) {
		def targetOption = new FctTargetOption(order: order, code: code, target: target, data: sum).save(failOnError: true)
		setLocaleValueInMap(targetOption, names, "Names")
		target.addToTargetOptions(targetOption)
		target.save(failOnError: true)
		return targetOption
	}	
}
