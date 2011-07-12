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
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

public class SurveyAdminPage {

	private Survey survey;
	private SurveyStrategicObjective objective;
	private SurveySubStrategicObjective subobjective;
	private SurveyQuestion question;
	private List<Survey> surveys;
	private List<OrganisationUnitGroup> organisationUnitGroups;

	public SurveyAdminPage(Survey survey, SurveyStrategicObjective objective,
			SurveySubStrategicObjective subobjective, SurveyQuestion question,
			List<Survey> surveys,
			List<OrganisationUnitGroup> organisationUnitGroups) {
		super();
		this.survey = survey;
		this.objective = objective;
		this.subobjective = subobjective;
		this.question = question;
		this.surveys = surveys;
		this.organisationUnitGroups = organisationUnitGroups;
	}

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public void setObjective(SurveyStrategicObjective objective) {
		this.objective = objective;
	}

	public SurveyStrategicObjective getObjective() {
		return objective;
	}

	public void setSubobjective(SurveySubStrategicObjective subobjective) {
		this.subobjective = subobjective;
	}

	public SurveySubStrategicObjective getSubobjective() {
		return subobjective;
	}

	public void setQuestion(SurveyQuestion question) {
		this.question = question;
	}

	public SurveyQuestion getQuestion() {
		return question;
	}

	public List<Survey> getSurveys() {
		return surveys;
	}

	public void setSurveys(List<Survey> surveys) {
		this.surveys = surveys;
	}

	public List<OrganisationUnitGroup> getOrganisationUnitGroups() {
		return organisationUnitGroups;
	}

	public void setOrganisationUnitGroups(
			List<OrganisationUnitGroup> organisationUnitGroups) {
		this.organisationUnitGroups = organisationUnitGroups;
	}

}
