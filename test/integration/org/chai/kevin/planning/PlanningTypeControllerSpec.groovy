package org.chai.kevin.planning

import org.chai.kevin.data.Type;

class PlanningTypeControllerSpec extends PlanningIntegrationTests {

	def planningTypeController
	
	def "planning type list"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		def planningType = newPlanningType(newFormElement(newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")])))), "[_].key", "[_].key", planning)
		planningTypeController = new PlanningTypeController()
		
		when:
		planningTypeController.params['planning.id'] = planning.id
		planningTypeController.list()
		
		then:
		planningTypeController.modelAndView.model.entities.equals([planningType])
	}
	
	def "planning type list with no planning"() {
		setup:
		planningTypeController = new PlanningTypeController()
		
		when:
		planningTypeController.list()
		
		then:
		planningTypeController.modelAndView == null
	}
	
	def "create planning type works ok"() {
		setup:
		def period = newPeriod()
		def planning = newPlanning(period, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_ENUM("code")])))
		def formElement = newFormElement(dataElement)
		planningTypeController = new PlanningTypeController()
		
		when:
		planningTypeController.params['planning.id'] = planning.id
		planningTypeController.params['formElement.id'] = formElement.id
		planningTypeController.params['discriminator'] = '[_].key'
		planningTypeController.params['fixedHeader'] = '[_].key'
		planningTypeController.params['namesPlural'] = ['en': 'Activities']
		planningTypeController.params['namesPlural'] = ['en': 'Activities']	
		planningTypeController.params['headerList'] = ['[_].key']
		planningTypeController.params['headerList[[_].key].en'] = 'Header'
		planningTypeController.params['headerList[[_].key]'] = ['en': 'Header'] // this is not used
		planningTypeController.params['sectionList'] = ['[_].key']
		planningTypeController.params['sectionList[[_].key].en'] = 'Description' 
		planningTypeController.params['sectionList[[_].key]'] = ['en': 'Description'] // this is not used
		planningTypeController.saveWithoutTokenCheck()

		then:
		PlanningType.count() == 1
		PlanningType.list()[0].period.equals(period)
		PlanningType.list()[0].namesPlural.en.equals("Activities")
		PlanningType.list()[0].formElement.headers['[_].key'].en.equals('Header')
		PlanningType.list()[0].sectionDescriptions['[_].key'].en.equals('Description')
		
	}
	
}
