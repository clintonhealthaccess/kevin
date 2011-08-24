package org.chai.kevin.survey

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.ValueType;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;

class DomainSpec extends IntegrationTests {

	private static final Log log = LogFactory.getLog(DomainSpec.class)

	def setup() {
		IntegrationTestInitializer.createDummyStructure()
	}
	
	def static createDummySection() {
		def survey = new Survey(period: Period.list()[0]).save(failOnError: true, flush: true).save(failOnError: true);
		def objective = new SurveyObjective(survey: survey, groupUuidString: "District Hospital", order: 1).save(failOnError: true);
		survey.addObjective objective
		survey.save(failOnError: true)
		def section = new SurveySection(objective: objective, groupUuidString: "District Hospital", order: 1).save(failOnError: true)
		objective.addSection section
		objective.save(failOnError: true)
		
		return section;
	}
	
	def static createDummyTableQuestion(def section) {
		//Adding a table type question
		def tableQ = new SurveyTableQuestion(
			names: j(["en":"For each training module:<br/>(a) Enter the total number of staff members that received training in this subject from July 2009 - June 2010, regardless of how many days' training they received.<br/>(b) Enter the cumulative number of training days spent on that module. To do so, add up all of the days spent by every person who participated in that module. "]),
			descriptions: j(["en":"Training Modules"]),
			order: 1,
			section: section,
			groupUuidString: "District Hospital"
		)
		//Add columns
		def tabColumnOne = new SurveyTableColumn(
			names: j(["en":"Number Who Attended Training"]),
			descriptions: j(["en":"Number Who Attended Training"]),
			order: 1,
			groupUuidString: "District Hospital",
			question: tableQ
		)
		tableQ.addColumn(tabColumnOne)

		Map<SurveyTableColumn,SurveyElement> dataElmntsLine1= new LinkedHashMap<SurveyTableColumn,SurveyElement>();
		dataElmntsLine1.put(tabColumnOne,new SurveyElement(surveyQuestion: tableQ, dataElement: DataElement.findByCode("CODE8")))
					
		def tabRowOne = new SurveyTableRow(
			names: j(["en":"Clinical Pharmacy :"]),
			descriptions: j(["en":"Clinical Pharmacy :"]),
			order: 1,
			question: tableQ,
			groupUuidString: "District Hospital",
			surveyElements: dataElmntsLine1
		)
		tableQ.addRow(tabRowOne)
		tableQ.save(failOnError:true)
		
		section.addQuestion(tableQ)
		section.save(failOnError: true)
	}

	def "table question has data elements"() {
		setup:
		new DataElement(names:j(["en":"Element 8"]), descriptions:j([:]), code:"CODE8", type: ValueType.VALUE).save(failOnError: true, flush: true)
		def section = createDummySurvey()
		createDummyTableQuestion(section)
		
		when:
		def question = SurveyTableQuestion.list()[0]
		
		then:
		question.surveyElements.size() == 1
		question.surveyElements[0].equals(SurveyElement.findByDataElement(DataElement.findByCode("CODE8")))
	}

	def "save survey cascades skiprule"() {
		when:
		def survey = new Survey(period: Period.list()[0]).save(failOnError: true, flush: true);
		def skipRule = new SurveySkipRule(survey: survey, expression: "1==1")
		survey.addSkipRule(skipRule)
		
		then:
		skipRule.id == null
		
		when:
		survey.save(failOnError: true, flush: true)
		
		then:
		skipRule.id != null
	}

//	def "skiprule element list"() {
//		when:
//		def survey = new Survey(period: Period.list()[0]).save(failOnError: true, flush: true);
//		def skipRule = new SurveySkipRule(survey: survey, expression: "1==1")
//		survey.addSkipRule(skipRule)
//		survey.save(failOnError: true, flush: true)
//		
//		then:
//		def skipRule = SurveySkipRule.list()[0].
//	}	
		
	
	def "test question table number of organisation unit applicable"(){
		
		setup:
		def dh = OrganisationUnitGroup.findByUuid("District Hospital")
		def hc = OrganisationUnitGroup.findByUuid("Health Center")
		
		
		def enume2 = new Enum(names:j(["en":"Enum 2"]), descriptions:j([:]), code:"ENUM2");
		def enumOption01 = new EnumOption(names:j(["en":"N/A Did not receive training"]), descriptions:j(["en":"N/A Did not receive training"]), value:"N/A Did not receive training", code:"OPTION01", enume: enume2);
		def enumOption02 = new EnumOption(names:j(["en":"NGO or Partner"]), descriptions:j(["en":"NGO or Partner"]), value:"NGO or Partner", code:"OPTION02", enume: enume2);
		def enumOption03 = new EnumOption(names:j(["en":"Ministry of Health"]), descriptions:j(["en":"Ministry of Health"]), value:"Ministry of Health", code:"OPTION03", enume: enume2);
		enume2.enumOptions = [
			enumOption01,
			enumOption02,
			enumOption03
		]
		enume2.save(failOnError: true)
		enumOption01.save(failOnError: true)
		enumOption02.save(failOnError: true, flush:true)
		enumOption03.save(failOnError: true)
	
		
		//Create DataElement
		new DataElement(names:j("en":"testTab"), code:"TESTTAB", type: ValueType.ENUM,  enume: Enum.findByCode('ENUM2')).save(failOnError:true)
		def dataElement = DataElement.findByCode("TESTTAB")
		
		//Creating Survey
		def surveyOne = new Survey(
			names: j(["en":"Survey Number 1"]),
			descriptions: j(["en":"Survey Number 1 Description"]),
			period: Period.list()[1],
			order: 0
		)
		//Creating Objective
		def hResourceHealth = new SurveyObjective(
				names: j(["en":"Human Resources for Health"]),
				descriptions: j(["en":"Human Resources for Health"]),
				order: 1,
				groupUuidString: "District Hospital,Health Center",
				)
		surveyOne.addObjective(hResourceHealth)
		surveyOne.save(failOnError:true)
		
		//Create Section
		def staffing=new SurveySection(
				names: j(["en":"Staffing"]),
				descriptions: j(["en":"Staffing"]),
				order: 1,
				objective: hResourceHealth,
				groupUuidString: "District Hospital,Health Center"
				)
		hResourceHealth.addSection(staffing)
		hResourceHealth.save(failOnError:true);
		
		//Create Question
		def tableQ = new SurveyTableQuestion(
				names: j(["en":"Table type question test"]),
				tableNames: j(["en":"Training Modules"]),
				order: 1,
				groupUuidString: "District Hospital"
				)
		staffing.addQuestion(tableQ)
		staffing.save(failOnError:true, flush: true)

		def tabColumnOne = new SurveyTableColumn(
				names: j(["en":"Number Who Attended Training"]),
				descriptions: j(["en":"Number Who Attended Training"]),
				order: 1,
				groupUuidString: "Health Center",
				question: tableQ
				)

		tableQ.addColumn(tabColumnOne)
		//Create Map Row-Column
		Map<SurveyTableColumn,SurveyElement> dataElmntsLine1= new LinkedHashMap<SurveyTableColumn,SurveyElement>();

		def surveyElementTable1 = new SurveyElement(dataElement: DataElement.findByCode("TESTTAB"), surveyQuestion: tableQ).save(failOnError: true)
		dataElmntsLine1.put(tabColumnOne, surveyElementTable1)

		
		//Add rows
		def tabRowOne = new SurveyTableRow(
			names: j(["en":"Clinical Pharmacy :"]),
			descriptions: j(["en":"Clinical Pharmacy :"]),
			order: 1,
			question: tableQ,
			groupUuidString: "District Hospital",
			surveyElements: dataElmntsLine1
		)
		
		tableQ.addRow(tabRowOne)
		tableQ.save(failOnError:true)
		
		when:
		def orgunitgroupList = tableQ.getOrganisationUnitGroupApplicable(surveyElementTable1)
		
		then:
		orgunitgroupList.size() == 0
	}
}
