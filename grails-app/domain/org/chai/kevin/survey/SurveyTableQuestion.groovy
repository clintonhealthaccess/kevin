/** 
 * Copyright (c) 2011, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.chai.kevin.survey;

import i18nfields.I18nFields;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.chai.kevin.Exportable;
import org.chai.kevin.util.Utils;
import org.chai.location.DataLocationType;

@I18nFields
class SurveyTableQuestion extends SurveyQuestion implements Exportable {

	String tableNames

	// deprecated
	String jsonTableNames
	
	static i18nFields = ['tableNames']
	
	static hasMany = [rows: SurveyTableRow, columns: SurveyTableColumn]
	
	static mapping = {
		table 'dhsst_survey_table_question'
	}
	
	static constraints = {
		tableNames (nullable: true)
		
		// deprecated
		jsonTableNames (nullable: true)
	}
	
	@Override
	public QuestionType getType() {
		return QuestionType.TABLE;
	}

//	@Override
//	public List<SurveyElement> getSurveyElements() {
//		List<SurveyElement> dataElements = new ArrayList<SurveyElement>();
//		for (SurveyTableRow row : getRows()) {
//			for (SurveyTableColumn column : getColumns()) {
//				if (row.getSurveyElements().get(column) != null) dataElements.add(row.getSurveyElements().get(column));
//			}
//		}
//		return dataElements;
//	}

	@Override
	public List<SurveyElement> getSurveyElements(DataLocationType type) {
		List<SurveyElement> dataElements = new ArrayList<SurveyElement>();
		for (SurveyTableRow row : getRows(type)) {
			for (SurveyTableColumn column : getColumns(type)) {
				if (row.getSurveyElements().get(column) != null) dataElements.add(row.getSurveyElements().get(column));
			}
		}
		return dataElements;
	}

	@Override
	public void removeSurveyElement(SurveyElement surveyElement) {
		super.removeSurveyElement(surveyElement)
		for (SurveyTableRow row : getRows()) {
			for (SurveyTableColumn column : getColumns()) {
				if (row.getSurveyElements().get(column).equals(surveyElement)) {
					row.getSurveyElements().remove(column);
				}
			}
		}
	}
	
	public List<SurveyTableRow> getRows(DataLocationType type) {
		List<SurveyTableRow> result = new ArrayList<SurveyTableRow>();
		for (SurveyTableRow surveyTableRow : getRows()) {
			if (Utils.split(surveyTableRow.getTypeCodeString(), Utils.DEFAULT_TYPE_CODE_DELIMITER).contains(type.getCode()))
				result.add(surveyTableRow);
		}
		return result;
	}

	public List<SurveyTableColumn> getColumns(DataLocationType type) {
		List<SurveyTableColumn> result = new ArrayList<SurveyTableColumn>();
		for (SurveyTableColumn surveyTableColumn : getColumns()) {
			if (Utils.split(surveyTableColumn.getTypeCodeString(), Utils.DEFAULT_TYPE_CODE_DELIMITER).contains(type.getCode()))
				result.add(surveyTableColumn);
		}
		return result;
	}

	public Set<String> getTypeApplicable(SurveyElement surveyElement) {
		Set<String> columnRowOrgUnitUuIDs = new HashSet<String>();
		
		boolean found = false;
		for (SurveyTableRow row : this.rows) {
			if (row.getSurveyElements().values().contains(surveyElement)) {
				Iterator it = row.getSurveyElements().entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry) it.next();
					if (surveyElement.equals(pairs.getValue())) {
						found = true;
						columnRowOrgUnitUuIDs.addAll(
							CollectionUtils.intersection(
								row.getTypeApplicable(),
								((SurveyTableColumn) pairs.getKey()).getTypeApplicable()
							)
						);
					}
				}
			}
		}

		if (!found) throw new IllegalArgumentException("survey element does not belong to question (row-column)");
		return new HashSet<String>(
			CollectionUtils.intersection(
				CollectionUtils.intersection(
					columnRowOrgUnitUuIDs, 
					Utils.split(this.getTypeCodeString(), Utils.DEFAULT_TYPE_CODE_DELIMITER)
				),
				getSection().getTypeApplicable()	
			)
		);
	}

	@Override
	protected SurveyTableQuestion newInstance() {
		return new SurveyTableQuestion();
	}

	protected void deepCopy(SurveyQuestion question, SurveyCloner cloner) {
		SurveyTableQuestion copy = (SurveyTableQuestion)question;
		super.deepCopy(copy, cloner);
		Utils.copyI18nField(this, copy, "TableNames")
		Map<Long, SurveyTableColumn> columns = new HashMap<Long, SurveyTableColumn>();
		for (SurveyTableColumn tableColumn : getColumns()) {
			SurveyTableColumn columnCopy = tableColumn.deepCopy(cloner);
			columns.put(tableColumn.getId(), columnCopy);
			copy.addToColumns(columnCopy);
		}
		for (SurveyTableRow tableRow : getRows()) {
			copy.addToRows(tableRow.deepCopy(cloner, columns));
		}
	}

	@Override
	public String toString() {
		return "SurveyTableQuestion[getId()=" + getId() + ", getNames()=" + getNames() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}
}
