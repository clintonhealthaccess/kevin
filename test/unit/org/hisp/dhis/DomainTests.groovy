package org.hisp.dhis

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

import grails.test.GrailsUnitTestCase;
import groovy.util.GroovyTestCase;

class DomainTests extends GrailsUnitTestCase {
	
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

	public void testSuccess() {
		assertEquals 1, 1
	}
	
  	public void testOrganisations() {
		def organisationUnit = new OrganisationUnit(name: "orgunit");
		def organisationUnitGroup = new OrganisationUnitGroup(name: "orgunitgroup", members: [organisationUnit]);
		organisationUnit.groups = [organisationUnitGroup]
		
		mockDomain(OrganisationUnit, [organisationUnit]);
		mockDomain(OrganisationUnitGroup, [organisationUnitGroup]);
		
		assertEquals 1, OrganisationUnit.findByName("orgunit").getGroups().size()
		assertEquals 1, OrganisationUnitGroup.findByName("orgunitgroup").getMembers().size()
	}
}
