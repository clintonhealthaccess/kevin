package org.chai.kevin.data

import org.chai.kevin.FunctionalSpec;
import org.chai.kevin.user.UserListPage;

import spock.lang.Stepwise;
import geb.spock.GebSpec;

@Stepwise
class DataSpec extends FunctionalSpec {

	def "user list page"() {
		given:
		login()
		
		when:
		to SourceListPage
		
		then:
		at SourceListPage
	}
	
	def "source edit page"() {
		given:
		login()
		
		when:
		to SourceEditPage
		
		then:
		at SourceEditPage
	}
	
	def "create source"() {
		given:
		login()
		to SourceEditPage
				
		when:
		code = 'source'
		save.click()
		
		then:
		at SourceListPage
	}
	
	def "raw data element list page"() {
		given:
		login()
		
		when:
		to RawDataElementListPage
		
		then:
		at RawDataElementListPage
	}
	
	def "raw data element edit page"() {
		given:
		login()
		
		when:
		to RawDataElementEditPage
		
		then:
		at RawDataElementEditPage
	}
	
	def "create raw data element"() {
		given:
		login()
		to RawDataElementEditPage
				
		when:
		code = 'element'
		source = '1'
		type = '{"type":"string"}'
		save.click()
		
		then:
		at RawDataElementListPage
	}
	
}
