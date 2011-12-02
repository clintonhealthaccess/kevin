package org.chai.kevin.data;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.NormalizedDataElementController;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.Status;
import org.hisp.dhis.organisationunit.OrganisationUnit;

class NormalizedDataElementControllerSpec extends IntegrationTests {

	def organisationService
	def normalizedDataElementController

	def "deleting normalized data element deletes values"() {
		setup:
		def organisation = newOrganisationUnit(BUTARO)
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		newNormalizedDataElementValue(normalizedDataElement, organisation, period, Status.VALID, v("1"))
		normalizedDataElementController.params.id = normalizedDataElement.id
		normalizedDataElementController.delete()
		
		then:
		NormalizedDataElement.count() == 0
		NormalizedDataElementValue.count() == 0
	}
	
	def "saving normalized data element sets timestamp"() {
		setup:
		setupOrganisationUnitTree()
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
		setupOrganisationUnitTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
		newNormalizedDataElementValue(normalizedDataElement, OrganisationUnit.findByName(BUTARO), period, Status.VALID, v("1"))
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
	
}
