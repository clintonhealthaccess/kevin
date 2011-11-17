package org.chai.kevin.survey

import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.DataElement;
import grails.plugin.spock.UnitSpec;

class SurveyEnteredValueUnitSpec extends UnitSpec {

	def NULL_SKIPPED_VALUE = "{\"value\":null, \"skipped\":\"1\"}"
	
	def "test set invalid prefix"() {
//		when:
//		def enteredValue = new SurveyEnteredValue(value: new Value("{\"value\":[{\"value\": 	}]}"));
		
	}
	
	def "test skipped prefix completes question"() {
		when:
		def value = new Value(NULL_SKIPPED_VALUE)
		def type = Type.TYPE_NUMBER()
		def rawDataElement = new RawDataElement(type: type)
		def surveyElement = new SurveyElement(rawDataElement: rawDataElement)
		def enteredValue = new SurveyEnteredValue(value: value, surveyElement: surveyElement)
		
		then:
		enteredValue.isComplete() == true
	}
	
	def "test get skipped prefixes"() {
		when:
		def value = new Value(NULL_SKIPPED_VALUE)
		def type = Type.TYPE_NUMBER()
		def rawDataElement = new RawDataElement(type: type)
		def surveyElement = new SurveyElement(rawDataElement: rawDataElement)
		def enteredValue = new SurveyEnteredValue(value: value, surveyElement: surveyElement)
		
		then:
		enteredValue.getSkippedPrefixes().equals(new HashSet([""]))
	}
	
}
