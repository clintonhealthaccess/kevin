package org.chai.kevin.fct

import org.chai.kevin.data.Type;

class FctReportServiceSpec extends FctIntegrationTests {

	def reportService

	def "test fct with no groups should return no value"() {
		when:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def expression1 = newExpression(CODE(1), Type.TYPE_NUMBER(), "10")
		def expression2 = newExpression(CODE(2), Type.TYPE_NUMBER(), "10")
		def sum = newSum([expression1, expression2], CODE(3), Type.TYPE_NUMBER())
		def objective = newFctObjective(CODE(4))
		def target = newFctTarget(CODE(5), sum, [], objective)
		refreshExpression()
		def organisation = getOrganisation(BURERA)
		def level = 4
		
		def fctTable = reportService.getFctTable(organisation, objective, period, level)
		
		then:
		fctTable.getFctReport(getOrganisation(organisationName), target).value == null
		
		where:
		organisationName << [BUTARO, KIVUYE]		
	}
	
	def "test fct with groups"() {
		when:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def expression1 = newExpression(CODE(1), Type.TYPE_NUMBER(), "10")
		def expression2 = newExpression(CODE(2), Type.TYPE_NUMBER(), "10")
		def sum = newSum([expression1, expression2], CODE(3), Type.TYPE_NUMBER())
		def objective = newFctObjective(CODE(4))				
		def target = newFctTarget(CODE(5), sum, [(DISTRICT_HOSPITAL_GROUP)], objective)
		refreshExpression()
		def organisation = getOrganisation(BURERA)
		def level = 4
		
		def fctTable = reportService.getFctTable(organisation, objective, period, level)
		
		then:
		fctTable.getFctReport(getOrganisation(BUTARO), target).value == "20"		
		fctTable.getFctReport(getOrganisation(KIVUYE), target).value == null		
	}
}