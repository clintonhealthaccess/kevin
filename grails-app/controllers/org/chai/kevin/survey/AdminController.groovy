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
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

class AdminController extends AbstractReportController {

	SurveyAdminService surveyAdminService;
	List<OrganisationUnitGroup> groups = OrganisationUnitGroup.list();

	def index = {
		redirect (action: 'admin', params: params)
	}

	def survey = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		List<Survey> surveys = Survey.list();
		if(surveys.count()>0)
			Collections.sort(surveys,new SurveySorter())

		render (view: '/survey/admin/list', model:[

					surveys: surveys, surveyCount: Survey.count()
				])
	}

	def objective = {

		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		Survey survey = Survey.get(params.survey);
		List<SurveyStrategicObjective> objectives = survey.objectives;

		Collections.sort(objectives,new SurveyStrategicObjectiveSorter())

		render (view: '/survey/admin/list', model:[

					survey:survey,
					groups:groups,
					objectives: objectives,
					objectiveCount: survey.objectives.count()

				])
	}

	def subobjective = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		SurveyStrategicObjective objective = SurveyStrategicObjective.get(params.objective)
		List<SurveySubStrategicObjective> subobjectives = objective.subObjectives;

		Collections.sort(subobjectives,new SurveySubStrategicObjectiveSorter())

		render (view: '/survey/admin/list', model:[
					survey: objective.survey,
					objective: objective,
					groups:groups,
					subobjectives: subobjectives,
					subobjectiveCount: subobjectives.count()
				])
	}

	def question = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		SurveySubStrategicObjective subobjective = SurveySubStrategicObjective.get(params.subobjective)
		List<SurveyQuestion> questions = subobjective.questions;

		Collections.sort(questions,new SurveyQuestionSorter())

		render (view: '/survey/admin/list', model:[

					survey: subobjective.objective.survey,
					objective: subobjective.objective,
					subobjective: subobjective,
					groups:groups,
					questions: questions,
					questionCount: questions.count()
				])
	}
}
