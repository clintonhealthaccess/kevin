package org.chai.kevin.survey

import java.io.Serializable;

public class SurveyTableRowColumnMap implements Serializable {

	static belongsTo = [tableRow: SurveyTableRow]
	
	SurveyTableColumn tableColumn
	SurveyElement surveyElement
	
	static mapping = {
		table 'dhsst_survey_table_row_elements'
		id composite: ['tableRow', 'tableColumn']
		
		tableColumn column: 'survey_table_column'
		tableRow column: 'dhsst_survey_table_row'
		surveyElement column: 'surveyElements'
		version false
	}
	
	static constraints = {
		tableColumn (nullable: false)
		surveyElement (nullable: false)
	}

}
