package org.chai.kevin.dsr

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.dsr.DsrTargetCategory
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.util.Utils;

abstract class DsrIntegrationTests extends IntegrationTests {
	
	static def newDsrTarget(def code, def names, def order, def data, def average, def format, def category) {
		def target = new DsrTarget(code: code, order: order, format: format, data: data, average: average, category: category).save(failOnError: true)
		setLocaleValueInMap(target, names, "Names")
		if (category != null) {
			category.addToTargets(target)
			category.save(failOnError: true)
		}
		return target
	}
	
	static def newDsrTarget(def code, def data, def category) {
		return newDsrTarget(code, null, null, data, false, null, category)
	}
	
	static def newDsrTarget(def code, def order, def data, def category) {
		return newDsrTarget(code, null, order, data, false, null, category)
	}
	
	static def newDsrTarget(def code, Map names, def order, def data, def category) {
		return newDsrTarget(code, names, order, data, false, null, category)
	}
	
	static def newDsrTarget(def code, def order, def data, def average, def category) {
		return newDsrTarget(code, null, order, data, average, null, category)
	}
	
	static def newDsrTargetCategory(def code, def program, def order) {
		return new DsrTargetCategory(code: code, order: order, program: program).save(failOnError: true)
	}
}