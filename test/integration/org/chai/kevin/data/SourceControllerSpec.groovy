package org.chai.kevin.data

import org.chai.kevin.IntegrationTests;

class SourceControllerSpec extends IntegrationTests {

	def sourceController
	
	def "create source"() {
		setup:
		sourceController = new SourceController()
		
		when:
		sourceController.params.code = "source"
		sourceController.save()
		
		then:
		Source.count() == 1
		Source.list()[0].code == "source"
	}
	
	def "list sources"() {
		setup:
		def source = newSource("source")
		sourceController = new SourceController()
		
		when:
		sourceController.list()
		
		then:
		sourceController.modelAndView.model.entities == [source]
	}
	
	def "delete source"() {
		setup:
		def source = newSource("source")
		sourceController = new SourceController()
		
		when:
		sourceController.params.id = source.id
		sourceController.delete()
		
		then:
		Source.count() == 0
	}
	
}
