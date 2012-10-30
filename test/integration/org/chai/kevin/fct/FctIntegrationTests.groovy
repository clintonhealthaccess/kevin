package org.chai.kevin.fct

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.location.DataLocationType;
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.util.Utils;

abstract class FctIntegrationTests extends IntegrationTests {

	static def newFctTarget(def code, def program) {
		def target = new FctTarget(code: code, program: program).save(failOnError: true)
		program.save(failOnError: true)
		return target
	}
	
	static def newFctTarget(def code, def order, def program) {
		def target = new FctTarget(code: code, order: order, program: program).save(failOnError: true)
		program.save(failOnError: true)
		return target
	}

	static def newFctTargetOption(def code, def target, def sum) {
		def targetOption = new FctTargetOption(code: code, target: target, data: sum).save(failOnError: true)
		target.addToTargetOptions(targetOption)
		target.save(failOnError: true)
		return targetOption
	}
		
	static def newFctTargetOption(def code, def order, def target, def sum) {
		def targetOption = new FctTargetOption(order: order, code: code, target: target, data: sum).save(failOnError: true)		
		target.addToTargetOptions(targetOption)
		target.save(failOnError: true)
		return targetOption
	}	
}
