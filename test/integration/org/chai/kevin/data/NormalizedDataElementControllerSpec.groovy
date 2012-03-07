package org.chai.kevin.data;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.NormalizedDataElementController;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.Status;

class NormalizedDataElementControllerSpec extends IntegrationTests {

	def locationService
	def normalizedDataElementController

	def "deleting normalized data element deletes values"() {
		setup:
		def type = newDataEntityType(DISTRICT_HOSPITAL_GROUP)
		def location = newDataLocationEntity(BUTARO, type)
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		newNormalizedDataElementValue(normalizedDataElement, location, period, Status.VALID, v("1"))
		normalizedDataElementController.params.id = normalizedDataElement.id
		normalizedDataElementController.delete()
		
		then:
		NormalizedDataElement.count() == 0
		NormalizedDataElementValue.count() == 0
	}
	
	def "saving normalized data element sets timestamp"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
		normalizedDataElementController = new NormalizedDataElementController()
		def time1 = normalizedDataElement.timestamp
		
		when:
		normalizedDataElementController.params.id = normalizedDataElement.id
		normalizedDataElementController.save()
		
		then:
		NormalizedDataElement.count() == 1
		!NormalizedDataElement.list()[0].timestamp.equals(time1)
		
	}
	
	def "saving normalized data element deletes values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
		newNormalizedDataElementValue(normalizedDataElement, DataLocationEntity.findByCode(BUTARO), period, Status.VALID, v("1"))
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		normalizedDataElementController.params.id = normalizedDataElement.id
		normalizedDataElementController.save()
		
		then:
		NormalizedDataElement.count() == 1
		NormalizedDataElementValue.count() == 0
	}
	
	def "cannot delete normalized data element if there are associated calculations"() {
		setup:
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
		def calculation = newAverage("\$"+normalizedDataElement.id, CODE(2))
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		normalizedDataElementController.params.id = normalizedDataElement.id
		normalizedDataElementController.delete()
		
		then:
		NormalizedDataElement.count() == 1
		Average.count() == 1
	}
	
	def "search normalized data element"() {
		setup:
		def normalizedDataElement = newNormalizedDataElement(j(["en":"data element"]), CODE(1), Type.TYPE_NUMBER(), e([:]))
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		normalizedDataElementController.params.q = "element"
		normalizedDataElementController.search()
		
		then:
		normalizedDataElementController.modelAndView.model.entities.size() == 1
		normalizedDataElementController.modelAndView.model.entities[0].equals(normalizedDataElement)
		normalizedDataElementController.modelAndView.model.entityCount == 1
	}
	
	def "create normalized element with expressions"() {
		setup:
		def period1 = newPeriod()
		def period2 = newPeriod()
		def type1 = newDataEntityType("type1")
		normalizedDataElementController = new NormalizedDataElementController()

		when:
		normalizedDataElementController.params.type = "{\"type\":\"number\"}"
		normalizedDataElementController.params.code = "code"
		normalizedDataElementController.params['expressionMap['+period1.id+']['+type1.code+']'] = '123'
		normalizedDataElementController.params['expressionMap['+period2.id+']['+type1.code+']'] = '456'
		normalizedDataElementController.saveWithoutTokenCheck()
		
		then:
		NormalizedDataElement.count() == 1
		NormalizedDataElement.list()[0].expressionMap.equals( [(period1.id+''):[(type1.code):'123'], (period2.id+''):[(type1.code):'456']] )
	}
	
	def "get explainer"() {
		setup:
		setupLocationTree()
		def period1 = newPeriod()
		def period2 = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
		newNormalizedDataElementValue(normalizedDataElement, DataLocationEntity.findByCode(BUTARO), period1, Status.ERROR, v("1"))
		newNormalizedDataElementValue(normalizedDataElement, DataLocationEntity.findByCode(BUTARO), period2, Status.VALID, v("1"))
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		normalizedDataElementController.params.id = normalizedDataElement.id
		normalizedDataElementController.getExplainer()
		
		then:
		normalizedDataElementController.modelAndView.model.values == 2
		normalizedDataElementController.modelAndView.model.valuesWithError[period1] == 1
		normalizedDataElementController.modelAndView.model.valuesWithError[period2] == 0
	}
}
