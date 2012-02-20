package org.chai.kevin.value

import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.DataElement;
import grails.plugin.spock.UnitSpec;

class ValidatableUnitSpec extends UnitSpec {

	def NULL_SKIPPED_VALUE = "{\"value\":null, \"skipped\":\"1\"}"
	
	def "test skipped prefix completes question"() {
		when:
		def value = new Value(NULL_SKIPPED_VALUE)
		def type = Type.TYPE_NUMBER()
		def validatable = new ValidatableValue(value, type)
		
		then:
		validatable.isComplete() == true
	}
	
	def "test get skipped prefixes"() {
		when:
		def value = new Value(NULL_SKIPPED_VALUE)
		def type = Type.TYPE_NUMBER()
		def validatable = new ValidatableValue(value, type)
		
		then:
		validatable.getSkippedPrefixes().equals(new HashSet([""]))
	}
	
}
