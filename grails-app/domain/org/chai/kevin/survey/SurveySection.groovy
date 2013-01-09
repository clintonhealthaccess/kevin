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
import groovy.transform.EqualsAndHashCode
import i18nfields.I18nFields

import org.apache.commons.collections.CollectionUtils
import org.chai.kevin.Exportable
import org.chai.kevin.util.Utils
import org.chai.kevin.util.DataUtils
import org.chai.location.DataLocationType

@I18nFields
@EqualsAndHashCode(includes='code')
class SurveySection implements Exportable {

	// deprecated
	Long id
	
	String code
	Integer order
	String typeCodeString
	String names
	
	static i18nFields = ['names']
	
	SurveyProgram program
	static belongsTo = [program: SurveyProgram]
	static hasMany = [questions: SurveyQuestion]
	
	static mapping = {
		table 'dhsst_survey_section'
		code unique: true
		order column: 'ordering'
		questions cascade: "all-delete-orphan"
		program column: 'program'
	}
	
	static constraints ={
		code (nullable: false, blank: false, unique: true)
		order (nullable: false)
		typeCodeString (nullable:false /*, blank:false*/)
		names (nullable: true)
	}
	
	public List<SurveyQuestion> getAllQuestions() {
		return new ArrayList<SurveyQuestion>(questions?:[])
	}
	
	public Set<String> getTypeCodes() {
		return DataUtils.split(typeCodeString, DataUtils.DEFAULT_TYPE_CODE_DELIMITER);
	}
	
	public void setTypeCodes(Set<String> typeCodes) {
		this.typeCodeString = DataUtils.unsplit(typeCodes, DataUtils.DEFAULT_TYPE_CODE_DELIMITER);
	}

	public Survey getSurvey() {
		return program.getSurvey();
	}

	@SuppressWarnings("unchecked")
	public Set<String> getTypeApplicable() {
		return new HashSet<String>(CollectionUtils.intersection(
				DataUtils.split(this.typeCodeString, DataUtils.DEFAULT_TYPE_CODE_DELIMITER),
				this.program.getTypeApplicable()));
	}

	public List<SurveyElement> getSurveyElements(DataLocationType type) {
		List<SurveyElement> result = new ArrayList<SurveyElement>();
		for (SurveyQuestion question : getQuestions(type)) {
			result.addAll(question.getSurveyElements(type));
		}
		return result;
	}

	public List<SurveyQuestion> getQuestions(DataLocationType type) {
		List<SurveyQuestion> result = new ArrayList<SurveyQuestion>();
		for (SurveyQuestion surveyQuestion : getQuestions()) {
			if (surveyQuestion.getTypeCodes().contains(type.getCode())) result.add(surveyQuestion);
		}
		return result;
	}

	protected void deepCopy(SurveySection copy, SurveyCloner surveyCloner) {
		copy.setCode(getCode() + " clone");
		Utils.copyI18nField(this, copy, "Names")
		copy.setTypeCodeString(getTypeCodeString());
		copy.setProgram(surveyCloner.getProgram(getProgram()));
		copy.setOrder(getOrder());
		for (SurveyQuestion question : getQuestions()) {
			copy.addToQuestions(surveyCloner.getQuestion(question));
		}
	}

	@Override
	public String toString() {
		return "SurveySection[getId()=" + getId() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}

}
