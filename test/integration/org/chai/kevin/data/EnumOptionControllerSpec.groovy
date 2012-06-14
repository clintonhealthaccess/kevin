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
		def enume = newEnume(CODE("the code one"), "My Enum one", "Enum one for test one");
		enumOptionController = new EnumOptionController()
		
		when:
		enumOptionController.params["enume.id"] = enume.id
		enumOptionController.params["names"] = ["en": "Name"]
		enumOptionController.params["descriptions"] = ["en": "Description"]
		enumOptionController.params["code"] = "code"
		enumOptionController.params["value"] = "value"
		enumOptionController.saveWithoutTokenCheck()
		
		then:
		EnumOption.count() == 1
		EnumOption.list()[0].enume.equals(enume)
		EnumOption.list()[0].names.en.equals("Name")
		EnumOption.list()[0].descriptions.en.equals("Description")
		
		when:
		enumOptionController.params["enume.id"] = enume.id
		enumOptionController.list()
		
		then:
		enumOptionController.modelAndView.model.entities.size() == 1
		enumOptionController.modelAndView.model.entityCount == 1
	}
	
	def "search and list option"(){
		setup:
		def enume = newEnume(CODE("the code one"), "My Enum one", "Enum one for test one");
		def option2 = newEnumOption(enume, v("\"absent\""), o("en":1, "fr":2))
		def option3 = newEnumOption(enume, v("\"options 3\""), o("en":3, "fr":3))
		def option1 = newEnumOption(enume, v("\"testion\""), o("en":2, "fr":1))
		
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
		def enume = newEnume(CODE("the code one"), "My Enum one", "Enum one for test one");
		def option2 = newEnumOption(enume, v("\"absent\""), o("en":1, "fr":2))
		def option1 = newEnumOption(enume, v("\"testion\""), o("en":2, "fr":1))
		
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
