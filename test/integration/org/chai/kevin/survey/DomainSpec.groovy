package org.chai.kevin.survey

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.ValueType;

class DomainSpec extends IntegrationTests {

	private static final Log log = LogFactory.getLog(DomainSpec.class)

	def setup() {

	}

	def "table question has data elements"() {
		setup:
		new DataElement(names:j(["en":"Element 8"]), descriptions:j([:]), code:"CODE8", type: ValueType.VALUE).save(failOnError: true, flush: true)
		
		//Adding a table type question
		def tableQ = new SurveyTableQuestion(
			names: j(["en":"For each training module:<br/>(a) Enter the total number of staff members that received training in this subject from July 2009 - June 2010, regardless of how many days' training they received.<br/>(b) Enter the cumulative number of training days spent on that module. To do so, add up all of the days spent by every person who participated in that module. "]),
			descriptions: j(["en":"Training Modules"]),
			order: 1,
			groups: []
		)
		//Add columns
		def tabColumnOne = new SurveyTableColumn(
			names: j(["en":"Number Who Attended Training"]),
			descriptions: j(["en":"Number Who Attended Training"]),
			order: 1,
			question: tableQ
		)
		tableQ.addColumn(tabColumnOne)

		Map<SurveyTableColumn,SurveyElement> dataElmntsLine1= new LinkedHashMap<SurveyTableColumn,SurveyElement>();
		dataElmntsLine1.put(tabColumnOne,new SurveyElement(dataElement: DataElement.findByCode("CODE8")))
					
		def tabRowOne = new SurveyTableRow(
			names: j(["en":"Clinical Pharmacy :"]),
			descriptions: j(["en":"Clinical Pharmacy :"]),
			order: 1,
			question: tableQ,
			surveyElements: dataElmntsLine1
		)
		tableQ.addRow(tabRowOne)
		tableQ.save(failOnError:true)
		
		when:
		def question = SurveyTableQuestion.list()[0]
		
		then:
		question.surveyElements.size() == 1
		question.surveyElements[0].equals(SurveyElement.findByDataElement(DataElement.findByCode("CODE8")))
	}
	
}
