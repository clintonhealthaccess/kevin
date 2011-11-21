package org.chai.kevin.maps;

public class MapsTargetSpec extends MapsIntegrationTests {

	def "target calculation cannot be null"() {
		when:
		new MapsTarget(code: CODE(1)).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		def calculation = newAverage("1", CODE(2))
		new MapsTarget(code: CODE(2), calculation: calculation).save(failOnError: true)
		
		then:
		MapsTarget.count() == 1
	}
	
}
