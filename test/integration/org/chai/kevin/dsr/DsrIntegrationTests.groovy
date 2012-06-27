package org.chai.kevin.dsr

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.dsr.DsrTargetCategory
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.util.Utils;

abstract class DsrIntegrationTests extends IntegrationTests {
	
	static def newDsrTarget(def code, def order, def data, def format, def program, def category) {
		def target = new DsrTarget(names: [:],
			code: code,
			order: order,
			format: format,
			data: data,
			program: program,
			category: category
		).save(failOnError: true)
		if (category != null) {
			category.targets << target
			category.save(failOnError: true)
		}
		program.save(failOnError: true)
		return target
	}
	
	static def newDsrTarget(def code, def data, def program, def category) {
		return newDsrTarget(code, null, data, null, program, category)
	}
	
	static def newDsrTarget(def code, def order, def data, def program, def category) {
		return newDsrTarget(code, order, data, null, program, category)
	}
	
	static def newDsrTargetCategory(def code, def order) {
		return new DsrTargetCategory(code: code, order: order).save(failOnError: true)
	}
}