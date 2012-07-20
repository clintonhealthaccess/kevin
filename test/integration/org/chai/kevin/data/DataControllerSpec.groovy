package org.chai.kevin.data;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.Status;
import org.chai.kevin.value.Value;

class DataControllerSpec extends IntegrationTests {
	
	def dataController
	
	def "get data elements"() {
		setup:
		newRawDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER())
		
		when:
		dataController = new DataController()
		dataController.params.searchText = 'ele'
		dataController.params['class'] = 'RawDataElement'
		def model = dataController.getData()

		then:
		dataController.response.contentAsString.contains("success")
		dataController.response.contentAsString.contains("Element 1")
	}

	def "get data element description"() {
		setup:
		def dataElement = newRawDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER())

		when:
		dataController = new DataController()
		dataController.params.id = dataElement.id+""
		def model = dataController.getDescription()

		then:
		dataController.response.contentAsString.contains("success")
		dataController.response.contentAsString.contains("number")
	}

	def "get data element values - 404 when no data"() {
		setup:
		dataController = new DataController()
		
		when:
		dataController.dataElementValueList()
		
		then:
		dataController.modelAndView == null
	}
	
	def "get data element values - default to first period when no period specified"() {
		setup:
		setupLocationTree()
		def period1 = newPeriod()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		dataController = new DataController()
		
		when:
		def dataElementValue = newRawDataElementValue(dataElement, period1, DataLocation.findByCode(BUTARO), Value.NULL_INSTANCE())
		dataController.params.data = dataElement.id
		dataController.dataElementValueList()
		
		then:
		dataController.modelAndView.model.selectedPeriod.equals(period1)
		dataController.modelAndView.model.entities.equals([dataElementValue])
		dataController.modelAndView.model.entityCount == 1
	}
	
	def "get data element values - paging works"() {
		setup:
		setupLocationTree()
		def period1 = newPeriod()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		dataController = new DataController()
		
		when:
		def dataElementValue1 = newRawDataElementValue(dataElement, period1, DataLocation.findByCode(BUTARO), Value.NULL_INSTANCE())
		def dataElementValue2 = newRawDataElementValue(dataElement, period1, DataLocation.findByCode(KIVUYE), Value.NULL_INSTANCE())
		dataController.params.period = period1.id
		dataController.params.data = dataElement.id
		dataController.params.max = 1
		dataController.dataElementValueList()
		
		then:
		dataController.modelAndView.model.entities.equals([dataElementValue1])
		dataController.modelAndView.model.entityCount == 2
	}
	
	def "get data element values - raw data element"() {
		setup:
		setupLocationTree()
		def period1 = newPeriod()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		dataController = new DataController()
		
		when:
		dataController.params.data = dataElement.id
		dataController.dataElementValueList()
		
		then:
		dataController.modelAndView.model.periods.equals([period1])
		dataController.modelAndView.model.entities.equals([])
		
		when:
		def dataElementValue = newRawDataElementValue(dataElement, period1, DataLocation.findByCode(BUTARO), Value.NULL_INSTANCE())
		dataController.dataElementValueList()
		
		then:
		dataController.modelAndView.model.periods.equals([period1])
		dataController.modelAndView.model.selectedPeriod.equals(period1)
		dataController.modelAndView.model.entities.equals([dataElementValue])
	}
	
	def "get data element values - normalized data element"() {
		setup:
		setupLocationTree()
		def period1 = newPeriod()
		def dataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
		dataController = new DataController()
		
		when:
		dataController.params.data = dataElement.id
		dataController.dataElementValueList()
		
		then:
		dataController.modelAndView.model.periods.equals([period1])
		dataController.modelAndView.model.entities.equals([])
		
		when:
		def dataElementValue = newNormalizedDataElementValue(dataElement, DataLocation.findByCode(BUTARO), period1, Status.VALID, Value.NULL_INSTANCE())
		dataController.dataElementValueList()
		
		then:
		dataController.modelAndView.model.periods.equals([period1])
		dataController.modelAndView.model.entities.equals([dataElementValue])
	}
	
	def "delete values"() {
		setup:
		setupLocationTree()
		def period1 = newPeriod()
		def dataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
		newNormalizedDataElementValue(dataElement, DataLocation.findByCode(BUTARO), period1, Status.VALID, v("1"))
		dataController = new DataController()
		
		when:
		dataController.params.data = dataElement.id
		dataController.deleteValues()
		
		then:
		NormalizedDataElementValue.count() == 0
	}
	
}
