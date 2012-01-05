package org.chai.kevin.survey

import org.chai.kevin.data.Type
import org.chai.kevin.location.DataEntity;
import org.chai.kevin.value.Value
import org.hisp.dhis.organisationunit.OrganisationUnit

class ValidationServiceSpec extends SurveyIntegrationTests {

	def validationService;
	
	def "skip elemnts"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()

		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def dataElement1 = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(['key1':Type.TYPE_NUMBER(),'key2':Type.TYPE_NUMBER()])))
		def element1 = newSurveyElement(question1, dataElement1)
		
		def rule = newSkipRule(survey, "\$"+element1.id+"[_].key1 == 1", [(element1): "[_].key1,[_].key2"], [])
		
		when:
		newSurveyEnteredValue(element1, period, OrganisationUnit.findByName(KIVUYE), new Value("{\"value\": [{\"value\":[{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]},{\"value\":[{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}]}"))
		def skipped = validationService.getSkippedPrefix(element1, rule, getOrganisation(KIVUYE))
		
		then:
		skipped.equals(s(["[0].key1","[0].key2","[1].key1","[1].key2"]))
		
	}
	
	def "false validation based on other elements"() {
		setup:
		setupLocationTree()
		def period = newPeriod()

		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(section, 2, [(HEALTH_CENTER_GROUP)])
		
		def dataElement1 = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def dataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def element1 = newSurveyElement(question1, dataElement1)
		def element2 = newSurveyElement(question2, dataElement2)
		
		def validationRule = null
		
		when:
		validationRule = newSurveyValidationRule(element1, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "\$"+element1.id+" > \$"+element2.id)
		
		newSurveyEnteredValue(element1, period, DataEntity.findByCode(KIVUYE), v("1"))
		newSurveyEnteredValue(element2, period, DataEntity.findByCode(KIVUYE), v("1"))
		def prefixes = validationService.getInvalidPrefix(validationRule, DataEntity.findByCode(KIVUYE))
		
		then:
		prefixes.equals(new HashSet([""]))
	}	
	
	def "no validation errors"() {
		setup:
		setupLocationTree()
		def period = newPeriod()

		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(section, 2, [(HEALTH_CENTER_GROUP)])
		
		def dataElement1 = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def dataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def element1 = newSurveyElement(question1, dataElement1)
		def element2 = newSurveyElement(question2, dataElement2)
		
		def validationRule = null
		
		when:
		validationRule = newSurveyValidationRule(element1, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "\$"+element1.id+" > \$"+element2.id)
		
		newSurveyEnteredValue(element1, period, DataEntity.findByCode(KIVUYE), v("2"))
		newSurveyEnteredValue(element2, period, DataEntity.findByCode(KIVUYE), v("1"))
		def prefixes = validationService.getInvalidPrefix(validationRule, DataEntity.findByCode(KIVUYE))
		
		then:
		prefixes.isEmpty()
	}
	
	def "validation with null elements"() {
		
		setup:
		setupLocationTree()
		def period = newPeriod()

		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(section, 2, [(HEALTH_CENTER_GROUP)])
		
		def dataElement1 = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def dataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def element1 = newSurveyElement(question1, dataElement1)
		def element2 = newSurveyElement(question2, dataElement2)
		
		def validationRule = null
		
		when:
		validationRule = newSurveyValidationRule(element1, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "if (\$"+element1.id+" == 0) \$"+element2.id+" == null else false")
		
		newSurveyEnteredValue(element1, period, DataEntity.findByCode(KIVUYE), v("0"))
		newSurveyEnteredValue(element2, period, DataEntity.findByCode(KIVUYE), Value.NULL)
		def prefixes = validationService.getInvalidPrefix(validationRule, DataEntity.findByCode(KIVUYE))
		
		then:
		prefixes.isEmpty()
	}

	def "validation on certain unit groups only"() {
		setup:
		setupLocationTree()
		def period = newPeriod()

		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)])
		
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)])
		def question2 = newSimpleQuestion(section, 2, [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)])
		
		def dataElement1 = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element1 = newSurveyElement(question1, dataElement1)
		
		def validationRule = newSurveyValidationRule(element1, "", [(DISTRICT_HOSPITAL_GROUP)], "\$"+element1.id+" > 0")
		def prefixes = null
		
		when:
		newSurveyEnteredValue(element1, period, DataEntity.findByCode(KIVUYE), v("0"))
		prefixes = validationService.getInvalidPrefix(validationRule, DataEntity.findByCode(KIVUYE))
		
		then:
		prefixes.isEmpty()
		
		when:
		newSurveyEnteredValue(element1, period, DataEntity.findByCode(BUTARO), v("0"))
		prefixes = validationService.getInvalidPrefix(validationRule, DataEntity.findByCode(BUTARO))
		
		then:
		prefixes.equals(new HashSet([""]))
	}
		
}
