package org.chai.kevin.data

import org.chai.kevin.IntegrationTests;

class EnumOptionControllerSpec extends IntegrationTests {

	def enumOptionController
	
	def "list 404 when no enum"() {
		setup:
		enumOptionController = new EnumOptionController()
		
		when:
		enumOptionController.list()
		
		then:
		enumOptionController.modelAndView == null
	}
	
	def "create enum option and list"() {
		setup: 
		def enume = newEnume(CODE(1));
		enumOptionController = new EnumOptionController()
		
		when:
		enumOptionController.params["enume.id"] = enume.id
		enumOptionController.params["names_en"] = "Name"
		enumOptionController.params["descriptions_en"] = "Description"
		enumOptionController.params["code"] = "code"
		enumOptionController.params["value"] = "value"
		enumOptionController.saveWithoutTokenCheck()
		
		then:
		EnumOption.count() == 1
		EnumOption.list()[0].enume.equals(enume)
		EnumOption.list()[0].names_en.equals("Name")
		EnumOption.list()[0].descriptions_en.equals("Description")
		
		when:
		enumOptionController.params["enume.id"] = enume.id
		enumOptionController.list()
		
		then:
		enumOptionController.modelAndView.model.entities.size() == 1
		enumOptionController.modelAndView.model.entityCount == 1
	}
	
	def "search and list option"(){
		setup:
		def enume = newEnume(CODE(1));
		def option2 = newEnumOption(enume, "\"absent\"", ["en":1, "fr":2])
		def option3 = newEnumOption(enume, "\"options 3\"", ["en":3, "fr":3])
		def option1 = newEnumOption(enume, "\"testion\"", ["en":2, "fr":16])
		
		enumOptionController = new EnumOptionController()
		
		when:
		enumOptionController.params["enume.id"]=enume.id
		enumOptionController.params["enume"]=["id":enume.id]
		enumOptionController.params.q="on"
		enumOptionController.params.sort="order"
		enumOptionController.search()
		
		then:
		enumOptionController.modelAndView != null
		enumOptionController.modelAndView.model.entityCount==2
		enumOptionController.modelAndView.model.entities.equals([option1,option3])
		
		when:
		enumOptionController.params["enume.id"]=enume.id
		enumOptionController.list()
		
		then:
		enumOptionController.modelAndView != null
		enumOptionController.modelAndView.model.entityCount==3
		enumOptionController.modelAndView.model.entities.equals([option2,option1,option3])
		
	}
	
	def "delete enum option and list"() {
		setup:
		def enume = newEnume(CODE(1));
		def option2 = newEnumOption(enume, "\"absent\"", ["en":1, "fr":2])
		def option1 = newEnumOption(enume, "\"testion\"", ["en":2, "fr":1])
		
		enumOptionController = new EnumOptionController()
		
		when:
		enumOptionController.params["id"]=option2.id
		enumOptionController.delete()
		
		then:
		EnumOption.count() == 1
		
		when:
		enumOptionController.params["enume.id"]=enume.id
		enumOptionController.list()
		
		then:
		enumOptionController.modelAndView != null
		enumOptionController.modelAndView.model.entities.size() == 1
		enumOptionController.modelAndView.model.entityCount == 1
	}
	
}
