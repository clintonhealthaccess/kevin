package org.chai.kevin.fct

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.util.Utils;

abstract class FctIntegrationTests extends IntegrationTests {
	
	static def newFctTarget(def code, def types, def program) {
		def target = new FctTarget(names: [:],
			code: code,
			program: program,
			typeCodeString: Utils.unsplit(types)).save(failOnError: true)
			
		program.targets << target
		program.save(failOnError: true)
		return target
	}

	
	def newFctTargetOption(def code, def target, def sum, def order) {
		def targetOption = new FctTargetOption(
		names: [:],
		code: code,
		target: target,		
		sum: sum,
		order: order).save(failOnError: true)
		
		target.targetOptions << targetOption
		target.save(failOnError: true)
		return targetOption
	}	
}
