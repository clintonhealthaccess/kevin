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
import java.util.Map;

import org.chai.kevin.ValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public class SurveyPage {
	
	private OrganisationUnit organisationUnit;
	private SurveySubStrategicObjective subObjective;
	private Map<Long, SurveyElementValue> surveyElements;

	public SurveyPage(){}
	
	public SurveyPage(OrganisationUnit organisationUnit,
			SurveySubStrategicObjective subObjective) {
		this.organisationUnit = organisationUnit;
		this.subObjective = subObjective;
	}

	public SurveyElementValue getSurveyElementValue(Long id) {
		return surveyElements.get(id);
	}
	
	public Period getPeriod() {
		return getSurvey().getPeriod();
	}

	public Survey getSurvey() {
		return subObjective.getObjective().getSurvey();
	}
	
	public OrganisationUnit getOrganisationUnit() {
		return organisationUnit;
	}
	
	public void setOrganisationUnit(OrganisationUnit organisationUnit) {
		this.organisationUnit = organisationUnit;
	}
	
	public SurveySubStrategicObjective getSubObjective() {
		return subObjective;
	}
	
	public void setSubObjective(SurveySubStrategicObjective subObjective) {
		this.subObjective = subObjective;
	}

	public Map<Long, SurveyElementValue> getSurveyElements() {
		return surveyElements;
	}
	
	public void setSurveyElements(Map<Long, SurveyElementValue> surveyElements) {
		this.surveyElements = surveyElements;
	}
	
	public void userValidation(ValidationService validationService) {
		for (SurveyElementValue surveyElementValue : surveyElements.values()) {
			surveyElementValue.userValidation(validationService, organisationUnit, getPeriod());
		}
	}
	
	public void saveValues(ValueService valueService) {
		for (SurveyElementValue surveyElementValue : surveyElements.values()) {
			valueService.save(surveyElementValue.getDataValue());
		}
	}
	
	public boolean isValid() {
		for (SurveyElementValue surveyElementValue : surveyElements.values()) {
			if (!surveyElementValue.isValid()) return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "SurveyPage [period=" + getPeriod() + ", organisation="
				+ organisationUnit + ", subObjective=" + subObjective
				+ ", surveyElements=" + getSurveyElements() + "]";
	}
	
}
