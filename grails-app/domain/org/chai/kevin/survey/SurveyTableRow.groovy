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

/**
 * @author JeanKahigiso
 *
 */

import i18nfields.I18nFields;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.chai.kevin.Exportable;
import org.chai.kevin.IntegerOrderable;
import org.chai.kevin.util.Utils;

@I18nFields
class SurveyTableRow extends IntegerOrderable implements Exportable {

	String code;
	Integer order;
	String typeCodeString;
	String names
	
	String jsonNames
	
	static hasMany = [surveyTableRowColumnMaps: SurveyTableRowColumnMap]	
	
	static i18nFields = ['names']
	
	static transients = ['surveyElements']
	
	static belongsTo = [question: SurveyTableQuestion]
	
	static mapping = {
		table 'dhsst_survey_table_row'
		code unique: true
		order column: 'ordering'
		question column: 'question'
		surveyTableRowColumnMaps cascade: "all-delete-orphan"
	}
	
	static constraints = {
		surveyElements(validator: {val, obj ->
			boolean valid = true;
			if (val != null) {
				val.values().each {
					if (it != null) {
						if (!["BOOL","ENUM","DATE","STRING","NUMBER"].contains(it.dataElement.type.type.name())) valid = false
					}
				}
			}
			return valid;
		})
		code (nullable: false, blank: false, unique: true)
		order (nullable: false)
		typeCodeString (nullable:false /*, blank:false*/)
		names (nullable: true)

		// deprecated
		jsonNames (nullable: true)
	}
	
	public Map<SurveyTableColumn, SurveyElement> getSurveyElements() {
		Map result = [:]
		surveyTableRowColumnMaps?.each {
			result.put(it.tableColumn, it.surveyElement)
		}
		return result
	}

	public void setSurveyElements(Map surveyElements) {
		surveyTableRowColumnMaps?.clear()
		surveyElements.each {
			if (it.value != null) addToSurveyTableRowColumnMaps(new SurveyTableRowColumnMap(tableColumn: it.key, surveyElement: it.value))
		} 
	}
			
	public Set<String> getTypeCodes() {
		return Utils.split(typeCodeString, Utils.DEFAULT_TYPE_CODE_DELIMITER);
	}
	
	public void setTypeCodes(Set<String> typeCodes) {
		this.typeCodeString = Utils.unsplit(typeCodes, Utils.DEFAULT_TYPE_CODE_DELIMITER);
	}

	public Set<String> getTypeApplicable() {
		return Utils.split(this.typeCodeString, Utils.DEFAULT_TYPE_CODE_DELIMITER);
	}
	
	protected SurveyTableRow deepCopy(SurveyCloner cloner, Map<Long, SurveyTableColumn> columns) {
    	SurveyTableRow copy = new SurveyTableRow();
    	copy.setCode(getCode() + " clone");
    	Utils.copyI18nField(this, copy, "Names")
    	copy.setTypeCodeString(getTypeCodeString());
    	copy.setOrder(getOrder());
    	copy.setQuestion((SurveyTableQuestion)cloner.getQuestion(getQuestion()));
    	for (Entry<SurveyTableColumn, SurveyElement> entry : getSurveyElements().entrySet()) {
			copy.getSurveyElements().put(columns.get(entry.getKey().getId()), cloner.getElement(entry.getValue()));
		}
    	return copy;
	}
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((names == null) ? 0 : names.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SurveyTableRow other = (SurveyTableRow) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (names == null) {
			if (other.names != null)
				return false;
		} else if (!names.equals(other.names))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SurveyTableRow[getId()=" + getId() + ", getNames()=" + getNames() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + ", " + toSurveyElementMapExportString() + "]";
	}
	
	public String toSurveyElementMapExportString(){
		String result = "";		
		Map<SurveyTableColumn, SurveyElement> surveyElementMap = getSurveyElements();
		if(getSurveyElements() != null){
			List<String> surveyElements = new ArrayList<String>();
			for(SurveyTableColumn column : surveyElementMap.keySet()){
				surveyElements.add(surveyElementMap.get(column).toExportString());
			}
			result = "[" + StringUtils.join(surveyElements, ", ") + "]";
		}		
		return result;
	}

}
