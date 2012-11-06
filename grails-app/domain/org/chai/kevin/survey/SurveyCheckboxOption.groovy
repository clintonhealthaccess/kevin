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

@I18nFields
@EqualsAndHashCode(includes='code')
class SurveyCheckboxOption implements Exportable {

	String code;
	Integer order;
	String typeCodeString;
	SurveyElement surveyElement;
	
	String names
	
	static i18nFields = ['names']
	
	static belongsTo = [question: SurveyCheckboxQuestion]
			
	static mapping = {
		table 'dhsst_survey_checkbox_option'
		code unique: true
		order column: 'ordering'
		surveyElement column: 'surveyElement', cascade: 'all'
		question column: 'question'
	}
	
	static constraints ={
		surveyElement(nullable: true, validator: {val, obj ->
			if (val != null) {
				return val.dataElement.type.type.name() == "BOOL"
			}
			else return true;
		})
		code (nullable: false, blank: false, unique: true)
		order (nullable: false)
		typeCodeString (nullable:false /*, blank:false*/)
		names (nullable: true)
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

	public SurveyCheckboxOption deepCopy(SurveyCloner cloner) {
		SurveyCheckboxOption copy = new SurveyCheckboxOption();
		copy.setCode(getCode() + " clone");
		Utils.copyI18nField(this, copy, "Names")
		copy.setTypeCodeString(getTypeCodeString());
		copy.setOrder(getOrder());
		copy.setSurveyElement(cloner.getElement(getSurveyElement()));
		copy.setQuestion((SurveyCheckboxQuestion)cloner.getQuestion(getQuestion()));
		return copy;
	}
	
	@Override
	public String toString() {
		return "SurveyCheckboxOption[getId()=" + getId() + ", getNames()=" + getNames() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + ", " + getSurveyElement().toExportString() + "]";
	}

}
