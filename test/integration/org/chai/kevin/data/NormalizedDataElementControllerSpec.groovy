package org.chai.kevin.data;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.NormalizedDataElementController;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.Status;

class NormalizedDataElementControllerSpec extends IntegrationTests {

	def normalizedDataElementController

	def "deleting normalized data elemetn deletes values"() {
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
	
//	def "cannot delete expression if there are associated calculations"() {
//		setup:
//		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
//		def calculation = newAverage("\$"+normalizedDataElement.id, CODE(2))
//		def organisation = newOrganisationUnit(BUTARO)
//		def period = newPeriod()
//		normalizedDataElementController = new NormalizedDataElementController()
//		
//		when:
//		normalizedDataElementController.params.id = normalizedDataElement.id
//		normalizedDataElementController.delete()
//		
//		then:
//		NormalizedDataElement.count() == 1
//		
//	}
	
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
