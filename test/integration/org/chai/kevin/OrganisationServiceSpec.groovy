package org.chai.kevin

/*
* Copyright (c) 2011, Clinton Health Access Initiative.
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the <organization> nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import grails.plugin.spock.IntegrationSpec;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;

class OrganisationServiceSpec extends IntegrationTests {

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
	
	def "get children level for level"() {
		when:
		def children = organisationService.getChildren(level);
		
		then:
		children.containsAll getOrganisationUnitLevels(expectedLevels);
		getOrganisationUnitLevels(expectedLevels).containsAll children
		
		where:
		level	| expectedLevels
		1		| [2, 3, 4]
		2		| [3, 4]
		3		| [4]
		4		| []
	}
	
	def "get children of level for organisation"() {
		when:
		def organisation = getOrganisation(organisationName)
		def organisations = organisationService.getChildrenOfLevel(organisation, level)
		
		then:
		organisations.containsAll getOrganisations(expectedOrganisations)
		getOrganisations(expectedOrganisations).containsAll organisations
		
		where:
		organisationName	| level	| expectedOrganisations
		"Rwanda"			| 2		| ["North", "Kigali City", "West", "East", "South"]
		"Rwanda"			| 3		| ["Burera", "Nyarugenge", "Gasabo", "Kicukiro"]
		"Rwanda"			| 4		| ["Butaro DH", "Kivuye HC"]
		
	}
	
	def "get level for organisation"() {
		when:
		def organisation = getOrganisation(organisationName)
		def level = organisationService.getLevel(organisation)
		
		then:
		level == OrganisationUnitLevel.findByLevel(expectedLevel).level
		organisation.getLevel() == OrganisationUnitLevel.findByLevel(expectedLevel).level
		
		where:
		organisationName	| expectedLevel
		"Rwanda"			| 1
		"North"				| 2
		"Burera"			| 3
		"Butaro DH"			| 4
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
	
	
}
