package org.hisp.dhis

import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

import grails.test.GrailsUnitTestCase;
import groovy.util.GroovyTestCase;

class DomainIntegrationTests extends GroovyTestCase {
	
    protected void setUp() {
        super.setUp()
		
		Initializer.createDummyStructure();
    }

    protected void tearDown() {
        super.tearDown()
    }

  	public void testOrganisations() {
		assertEquals 1, OrganisationUnit.findByName("Butaro DH").getGroups().size()
		assertEquals 1, OrganisationUnitGroup.findByName("District Hospital").getMembers().size()
	}
	  
}
