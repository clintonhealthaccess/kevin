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

import java.util.List;
import java.util.Map;

import org.chai.kevin.Organisation;
import org.chai.kevin.survey.validation.SurveyEnteredValue;

/**
 * @author Jean Kahigiso M.
 * 
 */
public class SurveyPrint {

	// we need a map for data binding to work
	// all entered survey elements displayed on the page
	private Map<Long, SurveyElementValue> surveyElements;

	private Organisation organisation;
	private Survey survey;
	private List<SurveyObjective> objectives;

	// entered values for the whole survey
	private Map<SurveyElement, SurveyEnteredValue> enteredValues;

	public SurveyPrint() {
	}

	public SurveyPrint(Survey survey, Organisation organisation,
			List<SurveyObjective> objectives,
			Map<Long, SurveyElementValue> surveyElements,
			Map<SurveyElement, SurveyEnteredValue> enteredValues) {
		this.survey = survey;
		this.organisation = organisation;
		this.surveyElements = surveyElements;
		this.enteredValues = enteredValues;
	}

	public Map<Long, SurveyElementValue> getSurveyElements() {
		return surveyElements;
	}

	public void setSurveyElements(Map<Long, SurveyElementValue> surveyElements) {
		this.surveyElements = surveyElements;
	}

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public List<SurveyObjective> getObjectives() {
		return objectives;
	}

	public void setObjectives(List<SurveyObjective> objectives) {
		this.objectives = objectives;
	}

	public Map<SurveyElement, SurveyEnteredValue> getEnteredValues() {
		return enteredValues;
	}

	public void setEnteredValues(
			Map<SurveyElement, SurveyEnteredValue> enteredValues) {
		this.enteredValues = enteredValues;
	}

}
