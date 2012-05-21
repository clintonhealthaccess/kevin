package org.chai.kevin.planning

import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;

import grails.validation.ValidationException;


class PlanningDomainSpec extends PlanningIntegrationTests {

	def "period cannot be null in planning"() {
		setup:
		def period = newPeriod()
		
		when:
		new Planning(period: period).save(failOnError: true)
		
		then:
		Planning.count() == 1
		
		when:
		new Planning().save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "null constraints in planning type"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")])))
		def formElement = newFormElement(dataElement)
		
		when:
		new PlanningType(planning: planning, fixedHeader: '[_].key', formElement: formElement).save(failOnError: true)
		
		then:
		PlanningType.count() == 1
		
		when:
		new PlanningType(planning: planning, fixedHeader: '[_].key').save(failOnError: true)

		then:
		thrown ValidationException
		
		when:
		new PlanningType(formElement: formElement, fixedHeader: '[_].key').save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "data element has to be LIST-MAP in planning type"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		
		when:
		new PlanningType(planning: planning, fixedHeader: '[_].key', formElement: 
			newFormElement(newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningType(planning: planning, fixedHeader: '[_].key', formElement: 
			newFormElement(newRawDataElement(CODE(2), Type.TYPE_NUMBER()))
		).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningType(planning: planning, fixedHeader: '[_].key', formElement:
			newFormElement(newRawDataElement(CODE(3), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")]))))
		).save(failOnError: true)
		
		then:
		PlanningType.count() == 1
		
		
	}
	
	def "header prefix must be a value prefix"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")])))
		def formElement = newFormElement(dataElement)
		
		when:
		new PlanningType(planning: planning, fixedHeader: '[_]', formElement: formElement).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningType(planning: planning, fixedHeader: '', formElement: formElement).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningType(planning: planning, fixedHeader: '[_].key', formElement: formElement).save(failOnError: true)
		
		then:
		PlanningType.count() == 1
	}
	
	def "null constraints in planning cost"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		def planningType = newPlanningType(newFormElement(newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")])))), "[_].key", planning)
		def dataElement = newNormalizedDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_NUMBER()), e([:]))
		
		when:
		new PlanningCost(planningType: planningType, dataElement: dataElement, type: PlanningCostType.INCOMING).save(failOnError: true)
		
		then:
		PlanningCost.count() == 1
		
		when:
		new PlanningCost(planningType: planningType, type: PlanningCostType.INCOMING).save(failOnError: true)

		then:
		thrown ValidationException

		when:
		new PlanningCost(planningType: planningType, dataElement: dataElement).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	
	def "skip rule - null constraints"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		
		when:
		new PlanningSkipRule(planning: planning, expression: "true").save(failOnError: true)
		
		then:
		PlanningSkipRule.count() == 1
		
		when:
		new PlanningSkipRule(expression: "true").save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningSkipRule(planning: planning).save(failOnError: true)
		
		then:
		thrown ValidationException
		
	}
	
}
