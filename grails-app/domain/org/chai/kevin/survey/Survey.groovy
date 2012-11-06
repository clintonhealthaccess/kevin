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
 * @author Jean Kahigiso M.
 *
 */
import groovy.transform.EqualsAndHashCode;
import i18nfields.I18nFields;

import java.util.List;

import org.chai.kevin.Exportable;
import org.chai.kevin.Period;
import org.chai.kevin.util.Utils;
import org.chai.location.DataLocationType;

@I18nFields
@EqualsAndHashCode(includes='code')
class Survey implements Exportable {
	
	// deprecated
	Long id
	
	String code;
	Period lastPeriod;
	Period period;
	Boolean active = false; 
	
	String names
	String descriptions
	
	static i18nFields = ['names', 'descriptions']
	
	static hasMany = [programs: SurveyProgram, skipRules: SurveySkipRule]
	
	static mapping = {
		table 'dhsst_survey'
		code unique: true
		programs cascade: "all-delete-orphan"
		skipRules cascade: "all-delete-orphan"
		period column: 'period'
		lastPeriod column: 'last_period'
	}
	
	static constraints ={
		code (nullable: false, blank: false, unique: true)
		period (nullable:false)
		names (nullable: true)
		descriptions (nullable: true)
		lastPeriod (nullable: true)
	}
	
	List<SurveySection> getSections() {
		List<SurveySection> result = new ArrayList<SurveySection>();
		for (SurveyProgram surveyProgram : getPrograms()) {
			result.addAll(surveyProgram.getSections());
		}
		return result;
	}
	
	List<SurveyProgram> getAllPrograms() {
		return new ArrayList<SurveyProgram>(programs?:[])
	}
	
	List<SurveySkipRule> getAllSkipRules() {
		return new ArrayList<SurveySkipRule>(skipRules?:[])
	}
	
	List<SurveyProgram> getPrograms(DataLocationType type) {
		if (log.debugEnabled) log.debug('getPrograms(type='+type+')')
		List<SurveyProgram> result = new ArrayList<SurveyProgram>();
		for (SurveyProgram surveyProgram : getPrograms()) {
			if (surveyProgram.getTypeCodes().contains(type.code)) result.add(surveyProgram);
		}
		if (log.debugEnabled) log.debug('getPrograms()='+result)
		return result;
	}
	
	protected void deepCopy(Survey copy, SurveyCloner cloner) {
		copy.setCode(getCode() + " clone");
		Utils.copyI18nField(this, copy, "Names")
		Utils.copyI18nField(this, copy, "Descriptions")
		copy.setActive(getActive());
		copy.setPeriod(getPeriod());
		for (SurveyProgram program : getPrograms()) {
			copy.addToPrograms(cloner.getProgram(program));
		}
	}

	protected void copyRules(Survey copy, SurveyCloner cloner) {
		for (SurveySkipRule skipRule : getSkipRules()) {
			copy.addToSkipRules(cloner.getSkipRule(skipRule));
		}
	}

	@Override
	public String toString() {
		return "Survey[getId()=" + getId() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}

}
