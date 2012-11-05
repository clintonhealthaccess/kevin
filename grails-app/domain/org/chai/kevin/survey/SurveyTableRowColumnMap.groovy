package org.chai.kevin.survey

import java.io.Serializable;

public class SurveyTableRowColumnMap implements Serializable {

	SurveyTableRow tableRow
	static belongsTo = [tableRow: SurveyTableRow]
	
	SurveyTableColumn tableColumn
	SurveyElement surveyElement
	
	static mapping = {
		table 'dhsst_survey_table_row_elements'
		id composite: ['tableRow', 'tableColumn']
		
		tableColumn column: 'survey_table_column'
		tableRow column: 'dhsst_survey_table_row'
		surveyElement column: 'surveyElements', cascade: 'save-update'
		version false
	}
	
	static constraints = {
		tableColumn (nullable: false)
		surveyElement (nullable: false)
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((surveyElement == null) ? 0 : surveyElement.hashCode());
		result = prime * result + ((tableColumn == null) ? 0 : tableColumn.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this.is(obj))
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SurveyTableRowColumnMap))
			return false;
		SurveyTableRowColumnMap other = (SurveyTableRowColumnMap) obj;
		if (surveyElement == null) {
			if (other.surveyElement != null)
				return false;
		} else if (!surveyElement.equals(other.surveyElement))
			return false;
		if (tableColumn == null) {
			if (other.tableColumn != null)
				return false;
		} else if (!tableColumn.equals(other.tableColumn))
			return false;
		return true;
	}
}
