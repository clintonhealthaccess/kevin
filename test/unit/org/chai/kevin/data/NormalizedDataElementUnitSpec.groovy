package org.chai.kevin.data;

import org.chai.kevin.Period;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.util.JSONUtils;

import grails.plugin.spock.UnitSpec;

public class NormalizedDataElementUnitSpec extends UnitSpec {

	def "get expression"() {
		when:
		def period1 = new Period(id: 1)
		def period2 = new Period(id: 2)
		
		def normalizedDataElement = new NormalizedDataElement(expressionMap: [('1'):[('DH'):"1"]])
		
		then:
		normalizedDataElement.getExpression(period1, 'DH') == "1"
		normalizedDataElement.getExpression(period2, 'DH') == null
		normalizedDataElement.getExpression(period1, 'HC') == null
	}

}

