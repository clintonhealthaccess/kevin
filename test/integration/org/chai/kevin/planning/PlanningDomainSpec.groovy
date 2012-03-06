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
		def planning = newPlanning(period)
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")])))
		
		when:
		new PlanningType(planning: planning, discriminator: '[_].key', fixedHeader: '[_].key',dataElement: dataElement).save(failOnError: true)
		
		then:
		PlanningType.count() == 1
		
		when:
		new PlanningType(planning: planning, discriminator: '[_].key', fixedHeader: '[_].key').save(failOnError: true)

		then:
		thrown ValidationException
		
		when:
		new PlanningType(planning: planning, dataElement: dataElement, fixedHeader: '[_].key').save(failOnError: true)

		then:
		thrown ValidationException

		when:
		new PlanningType(discriminator: '[_].key', dataElement: dataElement, fixedHeader: '[_].key').save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "data element has to be LIST-MAP in planning type"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period)
		
		when:
		new PlanningType(planning: planning, discriminator: '[_].key', fixedHeader: '[_].key', dataElement: 
			newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningType(planning: planning, discriminator: '[_].key', fixedHeader: '[_].key', dataElement:
			newRawDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_NUMBER()))
		).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningType(planning: planning, discriminator: '[_].key', fixedHeader: '[_].key', dataElement:
			newRawDataElement(CODE(3), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")])))
		).save(failOnError: true)
		
		then:
		PlanningType.count() == 1
		
		
	}
	
	def "discriminator must be a prefix of type"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period)
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")])))
		
		when:
		new PlanningType(planning: planning, discriminator: '[_]', dataElement: dataElement, fixedHeader: '[_].key').save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningType(planning: planning, discriminator: '[_].key2', dataElement: dataElement, fixedHeader: '[_].key').save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningType(planning: planning, discriminator: '[_].key', dataElement: dataElement, fixedHeader: '[_].key').save(failOnError: true)
		
		then:
		PlanningType.count() == 1
	}
	
	def "header prefix must be a value prefix"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period)
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")])))
		
		when:
		new PlanningType(planning: planning, discriminator: '[_].key', fixedHeader: '[_]', dataElement: dataElement).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningType(planning: planning, discriminator: '[_].key', fixedHeader: '', dataElement: dataElement).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningType(planning: planning, discriminator: '[_].key', fixedHeader: '[_].key', dataElement: dataElement).save(failOnError: true)
		
		then:
		PlanningType.count() == 1
	}
	
	def "discriminator must reference an ENUM"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period)
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key1":Type.TYPE_ENUM("code"), "key2":Type.TYPE_NUMBER()])))
		
		when:
		new PlanningType(planning: planning, discriminator: '[_].key2', dataElement: dataElement, fixedHeader: '[_].key1').save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningType(planning: planning, discriminator: '[_].key1', dataElement: dataElement, fixedHeader: '[_].key1').save(failOnError: true)
		
		then:
		PlanningType.count() == 1
	}
	
	def "null constraints in planning cost"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period)
		def planningType = newPlanningType(newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")]))), "[_].key", "[_].key", planning)
		def dataElement = newNormalizedDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_NUMBER()), e([:]))
		
		when:
		new PlanningCost(planningType: planningType, discriminatorValueString: 'value', dataElement: dataElement, type: PlanningCostType.INCOMING, section: '[_].key').save(failOnError: true)
		
		then:
		PlanningCost.count() == 1
		
		when:
		new PlanningCost(planningType: planningType, dataElement: dataElement, type: PlanningCostType.INCOMING, section: '[_].key').save(failOnError: true)

		then:
		thrown ValidationException
		
		when:
		new PlanningCost(planningType: planningType, discriminatorValueString: 'value', type: PlanningCostType.INCOMING, section: '[_].key').save(failOnError: true)

		then:
		thrown ValidationException

		when:
		new PlanningCost(planningType: planningType, discriminatorValueString: 'value', dataElement: dataElement, section: '[_].key').save(failOnError: true)
		
		then:
		thrown ValidationException

		when:
		new PlanningCost(planningType: planningType, discriminatorValueString: 'value', dataElement: dataElement, type: PlanningCostType.INCOMING).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "section must be a valid section"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period)
		def planningType = newPlanningType(newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code"), "key1":Type.TYPE_MAP(["key12":Type.TYPE_NUMBER()])]))), "[_].key", "[_].key", planning)
		def dataElement = newNormalizedDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_NUMBER()), e([:]))
		
		when:
		new PlanningCost(planningType: planningType, discriminatorValueString: 'value', dataElement: dataElement, type: PlanningCostType.INCOMING, section: '[_]').save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningCost(planningType: planningType, discriminatorValueString: 'value', dataElement: dataElement, type: PlanningCostType.INCOMING, section: '').save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningCost(planningType: planningType, discriminatorValueString: 'value', dataElement: dataElement, type: PlanningCostType.INCOMING, section: '[_].key1.key12').save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new PlanningCost(planningType: planningType, discriminatorValueString: 'value', dataElement: dataElement, type: PlanningCostType.INCOMING, section: '[_].key').save(failOnError: true)
		
		then:
		PlanningCost.count() == 1
		
		when:
		new PlanningCost(planningType: planningType, discriminatorValueString: 'value', dataElement: dataElement, type: PlanningCostType.INCOMING, section: '[_].key1').save(failOnError: true)
		
		then:
		PlanningCost.count() == 2
	}
	
}
