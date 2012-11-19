package org.chai.kevin.planning

import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;
import org.hibernate.cfg.annotations.reflection.XMLContext.Default;

import grails.validation.ValidationException;


class PlanningDomainSpec extends PlanningIntegrationTests {

	def "period cannot be null in planning"() {
		setup:
		def period = newPeriod()
		
		when:
		new Planning(period: period, active: true).save(failOnError: true)
		
		then:
		Planning.count() == 1
		
		when:
		new Planning(active: true).save(failOnError: true)
		
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
	
	def "header prefix must be a value prefix or empty"() {
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
		PlanningType.count() == 1
		
		when:
		new PlanningType(planning: planning, fixedHeader: '[_].key', formElement: formElement).save(failOnError: true)
		
		then:
		PlanningType.count() == 2
	}
	
	def "save planning type cascades form element"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")])))
		
		when:
		def planningType = new PlanningType(planning: planning, fixedHeader: '', formElement: new FormElement(dataElement: dataElement))
		planningType.formElement.setHeaders(['':['en':'test']])
		planningType.save(failOnError: true)
				
		then:
		PlanningType.count() == 1
		FormElement.count() == 1
		FormElement.list()[0].getHeaders('en')[''] == 'test'
	}
	
	def "null constraints in planning cost"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		def planningType = newPlanningType(newFormElement(newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")])))), "[_].key", planning)
		def dataElement = newNormalizedDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_NUMBER()), [:])
		
		when:
		new PlanningCost(planningType: planningType, dataElement: dataElement, type: PlanningCostType.INCOMING, hideIfZero: false).save(failOnError: true)
		
		then:
		PlanningCost.count() == 1
		
		when:
		new PlanningCost(planningType: planningType, type: PlanningCostType.INCOMING, hideIfZero: false).save(failOnError: true)

		then:
		thrown ValidationException

		when:
		new PlanningCost(planningType: planningType, dataElement: dataElement, hideIfZero: false).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	
	def "skip rule - null constraints"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		
		when:
		new PlanningSkipRule(code: 'skip', planning: planning, expression: "true").save(failOnError: true)
		
		then:
		PlanningSkipRule.count() == 1
		
		when:
		new PlanningSkipRule(code: 'skip', expression: "true").save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningSkipRule(code: 'skip', planning: planning).save(failOnError: true)
		
		then:
		thrown ValidationException
		
	}
	
	def "planning output - null constraints"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_NUMBER()))
		
		when:
		new PlanningOutput(dataElement: dataElement, fixedHeader: "[_]", planning: planning, displayTotal: true).save(failOnError:true)
		
		then:
		PlanningOutput.count() == 1
		
		when:
		new PlanningOutput(dataElement: dataElement, planning: planning, displayTotal: true).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningOutput(fixedHeader: "[_]", planning: planning, displayTotal: true).save(failOnError:true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningOutput(dataElement: dataElement, fixedHeader: "[_]", displayTotal: true).save(failOnError:true)
		
		then:
		thrown ValidationException
	}
	
	def "planning output - fixed header must not be empty"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_NUMBER()))
		
		when:
		new PlanningOutput(dataElement: dataElement, fixedHeader: "", planning: planning).save(failOnError:true)
		
		then:
		thrown ValidationException
	}
		
	
	def "planning output - data element must be a list"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		
		when:
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		new PlanningOutput(dataElement: dataElement, fixedHeader: "[_]", planning: planning).save(failOnError:true)
		
		then:
		thrown ValidationException
	}
	
	def "planning output column - null constraints"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_NUMBER()))
		def planningOutput = newPlanningOutput(planning, dataElement, '[_]')
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_NUMBER()), [:])
		
		when:
		new PlanningOutputColumn(planningOutput: planningOutput, prefix: '[_]').save(failOnError:true)
		
		then:
		PlanningOutputColumn.count() == 1
		
		when:
		new PlanningOutputColumn(planningOutput: planningOutput).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningOutputColumn(prefix: '[_]').save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	
}
