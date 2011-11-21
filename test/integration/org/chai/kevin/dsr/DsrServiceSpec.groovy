package org.chai.kevin.dsr

import org.chai.kevin.data.Type

class DsrServiceSpec extends DsrIntegrationTests {

	def dsrService
	
	def "test dsr formatting"() {
		when:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "10")
		def objective = newDsrObjective(CODE(2))
		def target = newDsrTarget(CODE(3), expression, format, [(DISTRICT_HOSPITAL_GROUP), (HEALTH_CENTER_GROUP)], objective)
		refreshNormalizedDataElement()
		def organisation = getOrganisation(BURERA)
		
		def dsrTable = dsrService.getDsr(organisation, objective, period)
		
		then:
		dsrTable.getDsr(getOrganisation(BUTARO), target).stringValue == value
		dsrTable.getDsr(getOrganisation(BUTARO), target).applies == true
		
		where:
		format	| value
		"#"		| "10"
		""		| "10"
		"#.0"	| "10.0"
		
	}

	def "test dsr with no groups should return no value"() {
		when:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "10")
		def objective = newDsrObjective(CODE(2))
		def target = newDsrTarget(CODE(3), expression, [], objective)
		refreshNormalizedDataElement()
		def organisation = getOrganisation(BURERA)
		
		def dsrTable = dsrService.getDsr(organisation, objective, period)
		
		then:
		dsrTable.getDsr(getOrganisation(organisationName), target).stringValue == null
		dsrTable.getDsr(getOrganisation(organisationName), target).applies == false
		
		where:
		organisationName << [BUTARO, KIVUYE]
		
	}
	
	def "test dsr with groups"() {
		when:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "10")
		def objective = newDsrObjective(CODE(2))
		def target = newDsrTarget(CODE(3), expression, [(DISTRICT_HOSPITAL_GROUP)], objective)
		refreshNormalizedDataElement()
		def organisation = getOrganisation(BURERA)
		
		def dsrTable = dsrService.getDsr(organisation, objective, period)
		
		then:
		dsrTable.getDsr(getOrganisation(BUTARO), target).stringValue == "10"
		dsrTable.getDsr(getOrganisation(BUTARO), target).applies == true
		
		dsrTable.getDsr(getOrganisation(KIVUYE), target).stringValue == null
		dsrTable.getDsr(getOrganisation(KIVUYE), target).applies == false
		
	}
	
}
