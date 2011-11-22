package org.chai.kevin;

import org.chai.kevin.data.ExpressionMap;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.util.JSONUtils;
import org.hisp.dhis.period.Period;

import grails.plugin.spock.UnitSpec;

public class NormalizedDataElementUnitSpec extends UnitSpec {

	def "get expression"() {
		when:
		def period1 = new Period(id: 1)
		def period2 = new Period(id: 2)
		
		def normalizedDataElement = new NormalizedDataElement(expressionMap: e([('1'):[('DH'):"1"]]))
		
		then:
		normalizedDataElement.getExpression(period1, 'DH') == "1"
		normalizedDataElement.getExpression(period2, 'DH') == null
		normalizedDataElement.getExpression(period1, 'HC') == null
	}

	static e(def map) {
		return new ExpressionMap(jsonText: JSONUtils.getJSONFromMap(map))
	}
	
}

