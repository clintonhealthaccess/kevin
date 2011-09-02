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
package org.chai.kevin.survey

/**
 * @author Jean Kahigiso M.
 *
 */
import org.chai.kevin.AbstractReportController;
import org.chai.kevin.GroupCollection
import org.chai.kevin.PeriodSorter
import org.chai.kevin.survey.SurveyAdminService;
import org.chai.kevin.survey.SurveySorter;
import org.chai.kevin.survey.validation.SurveySkipRule
import org.chai.kevin.survey.validation.SurveyValidationMessage
import org.chai.kevin.survey.validation.SurveyValidationRule
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

class AdminController extends AbstractReportController {

	SurveyAdminService surveyAdminService;
	List<OrganisationUnitGroup> groups = OrganisationUnitGroup.list();

	def index = {
		redirect (action: "survey", params: params)
	}

	def survey = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		List<Survey> surveys = Survey.list(params);
		if(surveys.size()>0)
			Collections.sort(surveys,new SurveySorter())

		render (view: '/survey/admin/list', model:[
					template:"surveyList",
					surveys: surveys,
					surveyCount: Survey.count()
				])
	}

	def objective = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		Survey survey = Survey.get(params.surveyId);
		List<SurveyObjective> objectives = survey.objectives;

		render (view: '/survey/admin/list', model:[
					template:"objectiveList",
					survey:survey,
					objectives: objectives,
					objectiveCount: objectives.size()

				])
	}

	def section = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		SurveyObjective objective = SurveyObjective.get(params.objectiveId)
		List<SurveySection> sections = objective.sections;

		render (view: '/survey/admin/list', model:[
					template:"sectionList",
					survey: objective.survey,
					objective: objective,
					sections: sections,
					sectionCount: sections.size()
				])
	}

	def question = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		SurveySection section = SurveySection.get(params.sectionId)
		List<SurveyQuestion> questions = section.questions;

		render (view: '/survey/admin/list', model:[
					template:"questionList",
					survey: section.objective.survey,
					objective: section.objective,
					section: section,
					questions: questions,
					questionCount: questions.size()
				])
	}
	
	def surveyElements={
		params.max = Math.min(params.max ? params.int('max') : 10, 100);
		List<SurveyElement> surveyElements = SurveyElement.list(params);
		render (view: '/survey/admin/list', model:[
					template:"surveyElementList",
					surveyElements: surveyElements,
					surveyElementCount: SurveyElement.count()
				])
	}
	def validations = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		SurveyElement surveyElement = SurveyElement.get(params.surveyId)
		Set<SurveyValidationRule> validationRules = surveyElement.validationRules;

		render (view: '/survey/admin/list', model:[
					template:"validationRuleList",
					surveyElement: surveyElement,
					validationRules: validationRules,
					validationRuleCount: validationRules.size()
				])
	}
	def messages = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		List<SurveyValidationMessage> validationMessages = SurveyValidationMessage.list(params);

		render (view: '/survey/admin/list', model:[
					template:"validationMessageList",
					validationMessages: validationMessages,
					validationMessageCount: SurveyValidationMessage.count()
				])
	}
	def skiprules ={
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		Survey survey = Survey.get(params.surveyId)
		Set<SurveySkipRule> skipRules = survey.skipRules;

		render(view: '/survey/admin/list', model:[
					template: "skipRuleList",
					survey: survey,
					skipRules: skipRules,
					skipRuleCount: skipRules.size()
				])
		
		
		
	}
}
