package org.chai.kevin.location

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.value.Value;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.SyncChange

class DataLocationControllerSpec extends IntegrationTests {

	def dataLocationController
	
	def "test delete"() {
		setup:
		setupLocationTree()
		dataLocationController = new DataLocationController()
		
		when:
		dataLocationController.params.id = DataLocation.findByCode(BUTARO).id
		dataLocationController.delete()
		
		then:
		DataLocation.count() == 1
	}
	
	def "cannot delete data location if it still has values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(1))
		dataLocationController = new DataLocationController()
		
		when:
		dataLocationController.params.id = DataLocation.findByCode(BUTARO).id
		dataLocationController.delete()
		
		then:
		DataLocation.count() == 2
	}
	
	def "test delete deletes form entered values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newFormElement(dataElement)
		newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(1))
		dataLocationController = new DataLocationController()
		
		when:
		dataLocationController.params.id = DataLocation.findByCode(BUTARO).id
		dataLocationController.delete()
		
		then:
		FormEnteredValue.count() == 0
		DataLocation.count() == 1
	}
	
	def "test search"() {
		setup:
		setupLocationTree()
		dataLocationController = new DataLocationController()
		
		when:
		dataLocationController.params.q = 'but'
		dataLocationController.search()
		
		then:
		dataLocationController.modelAndView.model.entities == [DataLocation.findByCode(BUTARO)]
		dataLocationController.modelAndView.model.entityCount == 1
	}
	
	def "test list"() {
		setup:
		setupLocationTree()
		dataLocationController = new DataLocationController()
		
		when:
		dataLocationController.list()
		
		then:
		s(dataLocationController.modelAndView.model.entities) == s([DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)])
	}
	
	def "test list with location"() {
		setup:
		setupLocationTree()
		dataLocationController = new DataLocationController()
		
		when:
		dataLocationController.params.location = Location.findByCode(BURERA).id
		dataLocationController.list()
		
		then:
		s(dataLocationController.modelAndView.model.entities) == s([DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)])
		
		when:
		dataLocationController.params.location = Location.findByCode(RWANDA).id
		dataLocationController.list()
		
		then:
		dataLocationController.modelAndView.model.entities == []
	}
	
	def "test list with type"() {
		setup:
		setupLocationTree()
		dataLocationController = new DataLocationController()
		
		when:
		dataLocationController.params.type = DataLocationType.findByCode(HEALTH_CENTER_GROUP).id
		dataLocationController.list()
		
		then:
		dataLocationController.modelAndView.model.entities == [DataLocation.findByCode(KIVUYE)]
	}
	
	def "saving data location sets changes to reviewed"() {
		setup:
		setupLocationTree()
		def syncChange = new SyncChange(needsReview: true, reviewed: false)
		def dataLocation = DataLocation.findByCode(BUTARO)
		dataLocation.addToChanges(syncChange)
		dataLocation.save(failOnError: true, flush: true)
		dataLocationController = new DataLocationController()
		
		when:
		dataLocationController.params.id = DataLocation.findByCode(BUTARO).id
		dataLocationController.saveWithoutTokenCheck()
		
		then:
		SyncChange.list()[0].reviewed == true
	}
	
	def "saving data location sets needs review to false"() {
		setup:
		setupLocationTree()
		def dataLocation = DataLocation.findByCode(BUTARO)
		dataLocation.needsReview = true
		dataLocation.save(failOnError: true, flush: true)
		dataLocationController = new DataLocationController()
		
		when:
		dataLocationController.params.id = DataLocation.findByCode(BUTARO).id
		dataLocationController.saveWithoutTokenCheck()
		
		then:
		DataLocation.findByCode(BUTARO).needsReview == false
	}
	
}
