package org.chai.kevin

import grails.plugin.spock.IntegrationSpec;

import org.hisp.dhis.organisationunit.OrganisationUnit;

class OrganisationServiceSpec extends IntegrationSpec {

	def organisationService;
	
	def setup() {
		Initializer.createDummyStructure()
	}
	
	def "get organisations of level"() {
		expect:
		organisationService.getOrganisationsOfLevel(level).containsAll (getOrganisations(expectedOrganisations))
		
		where:
		level	| expectedOrganisations
		1		| ["Rwanda"]
		2		| ["North"]
		3		| ["Burera"]
		4		| ["Kivuye HC", "Butaro DH"]
	}
	
	def "get organisation tree until level"() {
		
		when:
		def organisationTree = organisationService.getOrganisationTreeUntilLevel(level)
		
		then:
		def organisation = getOrganisation("Rwanda")
		organisationTree == organisation
		assertIsLoaded(organisationTree, level)
		
		where:
		level << [1, 2, 3 ,4]
	}
	
	def assertIsLoaded(def organisation, def level) {
		def success = true;
		organisation.children.each { 
			if (!assertIsLoaded(it, level)) success = false
		}
		if (getLevel(organisation) == level) {
			if (organisation.children != null) success = false
		}
		else {
			if (organisation.children == null) success = false
		}
		return success;
	}	
	def organisationUnitService;
	def getLevel(def organisation) {
		return organisationUnitService.getLevelOfOrganisationUnit(organisation.organisationUnit)
	}
	
	static def getOrganisation(def name) {
		return new Organisation(OrganisationUnit.findByName(name))
	}
	
	static def getOrganisations(def names) {
		def result = []
		for (String name : names) {
			result.add(getOrganisation(name))
		}
		return result
	}
	
}
