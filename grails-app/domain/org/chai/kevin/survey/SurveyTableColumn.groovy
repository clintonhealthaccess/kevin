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

import groovy.transform.EqualsAndHashCode;
import i18nfields.I18nFields

import org.chai.kevin.Exportable
import org.chai.kevin.util.Utils
import org.chai.kevin.util.DataUtils

@I18nFields
@EqualsAndHashCode(includes='code')
class SurveyTableColumn implements Exportable {

	String code;
	Integer order;
	String typeCodeString;
	String names
	
	static i18nFields = ['names']
	
	static hasMany = [surveyTableRowColumnMaps: SurveyTableRowColumnMap]
	static belongsTo = [question: SurveyTableQuestion]
	
	static mapping = {
		table 'dhsst_survey_table_column'
		code unique: true
		order column: 'ordering'
		question column: 'question'
	}
	
	static constraints = {
		code (nullable: false, blank: false, unique: true)
		order (nullable: false)
		typeCodeString (nullable:false /*, blank:false*/)
		names (nullable: true)
	}
	
	public Set<String> getTypeCodes() {
		return DataUtils.split(typeCodeString, DataUtils.DEFAULT_TYPE_CODE_DELIMITER);
	}
	
	public void setTypeCodes(Set<String> typeCodes) {
		this.typeCodeString = DataUtils.unsplit(typeCodes, DataUtils.DEFAULT_TYPE_CODE_DELIMITER);
	}

	public Set<String> getTypeApplicable() {
		return DataUtils.split(this.typeCodeString, DataUtils.DEFAULT_TYPE_CODE_DELIMITER);
	}

	protected SurveyTableColumn deepCopy(SurveyCloner cloner) {
		SurveyTableColumn copy = new SurveyTableColumn();
		copy.setCode(getCode() + " clone");
		Utils.copyI18nField(this, copy, "Names")
		copy.setTypeCodeString(getTypeCodeString());
		copy.setOrder(getOrder());
		copy.setQuestion((SurveyTableQuestion)cloner.getQuestion(getQuestion()));
		return copy;
	}
	
	@Override
	public String toString() {
		return "SurveyTableColumn[getId()=" + getId() + ", getNames()=" + getNames() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}

}
