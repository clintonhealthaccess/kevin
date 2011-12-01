package org.chai.kevin.dsr

import org.chai.kevin.data.Type;

class DsrReportServiceSpec extends DsrIntegrationTests {

	def reportService
	
	def "test dsr formatting"() {
		when:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "10")
		def objective = newDsrObjective(CODE(2))
		def target = newDsrTarget(CODE(3), expression, format, [(DISTRICT_HOSPITAL_GROUP), (HEALTH_CENTER_GROUP)], objective)
		refreshExpression()
		def organisation = getOrganisation(BURERA)
		
		def dsrTable = reportService.getDsrTable(organisation, objective, period)
		
		then:
		dsrTable.getDsrReport(getOrganisation(BUTARO), target).value == value
		
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
		refreshExpression()
		def organisation = getOrganisation(BURERA)
		
		def dsrTable = reportService.getDsrTable(organisation, objective, period)
		
		then:
		dsrTable.getDsrReport(getOrganisation(organisationName), target).value == null
		
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
		refreshExpression()
		def organisation = getOrganisation(BURERA)
		
		def dsrTable = reportService.getDsrTable(organisation, objective, period)
		
		then:
		dsrTable.getDsrReport(getOrganisation(BUTARO), target).value == "10"		
		dsrTable.getDsrReport(getOrganisation(KIVUYE), target).value == null
		
	}
}