package org.chai.kevin.reports

import org.chai.kevin.FunctionalSpec;
import org.chai.kevin.data.SumEditPage;
import org.chai.kevin.data.CalculationListPage;
import org.openqa.selenium.Keys;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import spock.lang.Stepwise;
import geb.spock.GebSpec;

@Stepwise
class ReportSpec extends FunctionalSpec {

	def "report program edit page"() {
		given:
		login()
		
		when:
		to ReportProgramEditPage
		
		then:
		at ReportProgramEditPage
	}
	
	def "report program list page"() {
		given:
		login()
		
		when:
		to ReportProgramListPage
		
		then:
		at ReportProgramListPage
	}

	def "create report program"() {
		given:
		login()
		to ReportProgramEditPage
				
		when:
		code = 'program'
		save.click()
		
		then:
		at ReportProgramListPage
	}
	
	def "fct target edit page"() {
		given:
		login()
		
		when:
		to FctTargetEditPage
		
		then:
		at FctTargetEditPage
	}
	
	def "fct target list page"() {
		given:
		login()
		
		when:
		to FctTargetListPage
		
		then:
		at FctTargetListPage
	}
	
	def "create fct target"() {
		given:
		login()
		to FctTargetEditPage
		
		when:
		code = 'target'
		program = '1'
		save.click()
		
		then:
		at FctTargetListPage
	}
	
	def "fct target option edit page"() {
		given:
		login()
		
		when:
		to FctTargetOptionEditPage
		
		then:
		at FctTargetOptionEditPage
	}
	
	def "fct target option list page"() {
		given:
		login()
		
		when:
		to FctTargetOptionListPage
		
		then:
		at FctTargetOptionListPage
	}

	def "create sum"() {
		given:
		login()
		to SumEditPage
		
		when:
		code = 'sum'
		expression = '1'
		save.click()
	
		then:
		at CalculationListPage
	}
	
	// TODO somehow does not work with HTMLUnit
//	def "create fct target option"() {
//		given:
//		login()
//		to FctTargetOptionEditPage
//		
//		when:
//		code = 'target option'
//		target = '1'
//		sumContainer.click()
//		sumInput = 'sum'
//		sumInput << Keys.ENTER
//		Thread.sleep(2000)
//		sumContainer.find('.chzn-results').find('li', 1).click()
//		save.click()
//		
//		then:
//		at FctTargetOptionListPage
//	}
	
	def "dsr category edit page"() {
		given:
		login()
		
		when:
		to DsrCategoryEditPage
		
		then:
		at DsrCategoryEditPage
	}
	
	def "dsr category list page"() {
		given:
		login()
		
		when:
		to DsrCategoryListPage
		
		then:
		at DsrCategoryListPage
	}
	
	def "create dsr category"() {
		given:
		login()
		to DsrCategoryEditPage
		
		when:
		code = 'category'
		save.click()
		
		then:
		at DsrCategoryListPage
	}
	
	def "dsr target edit page"() {
		given:
		login()
		
		when:
		to DsrTargetEditPage
		
		then:
		at DsrTargetEditPage
	}
	
	def "dsr target list page"() {
		given:
		login()
		
		when:
		to DsrTargetListPage
		
		then:
		at DsrTargetListPage
	}
	
	// TODO somehow does not work with HTMLUnit
//	def "create dsr target"() {
//		given:
//		login()
//		to DsrTargetEditPage
//
//		when:
//		code = 'target'
//		category = '1'
//		program = '1'
//		dataContainer.click()
//		dataInput = 'sum'
//		dataInput << Keys.ENTER
//		Thread.sleep(2000)
//		dataContainer.find('.chzn-results').find('li', 1).click()
//		save.click()
//
//		then:
//		at DsrTargetListPage
//	}
	
	def "dashboard program edit page"() {
		given:
		login()
		
		when:
		to DashboardProgramEditPage
		
		then:
		at DashboardProgramEditPage
	}
	
	def "dashboard program list page"() {
		given:
		login()
		
		when:
		to DashboardProgramListPage
		
		then:
		at DashboardProgramListPage
	}
	
	def "create dashboard program"() {
		given:
		login()
		to DashboardProgramEditPage
		
		when:
		code = 'program'
		weight = '1'
		program = '1'
		save.click()
		
		then:
		at DashboardProgramListPage
	}
	
	def "dashboard target edit page"() {
		given:
		login()
		
		when:
		to DashboardTargetEditPage
		
		then:
		at DashboardTargetEditPage
	}
	
	def "dashboard target list page"() {
		given:
		login()
		
		when:
		to DashboardTargetListPage
		
		then:
		at DashboardTargetListPage
	} 
	
	// TODO somehow does not work with HTMLUnit
//	def "create dashboard target"() {
//		given:
//		login()
//		to DashboardTargetEditPage
//
//		when:
//		code = 'target'
//		program = '1'
//		weight = '1'
//		calculationContainer.click()
//		calculationInput = 'sum'
//		calculationInput << Keys.ENTER
//		Thread.sleep(2000)
//		calculationContainer.find('.chzn-results').find('li', 1).click()
//		save.click()
//
//		then:
//		at DashboardTargetListPage
//	}
	
}
