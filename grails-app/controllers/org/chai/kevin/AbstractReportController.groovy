package org.chai.kevin

/*
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

import org.apache.commons.lang.math.NumberUtils;
import org.chai.kevin.cost.CostObjective;
import org.chai.kevin.cost.CostTarget;
import org.chai.kevin.dashboard.DashboardObjectiveService;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardObjective;
import org.chai.kevin.data.Data;
import org.chai.kevin.dsr.DsrObjective;
import org.chai.kevin.dsr.DsrObjectiveService
import org.chai.kevin.maps.MapsTarget;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;
import org.chai.kevin.survey.SurveySorter
import org.chai.kevin.survey.Survey
import org.chai.kevin.survey.SurveyQuestion
import org.chai.kevin.survey.SurveyQuestionSorter
import org.chai.kevin.survey.SurveyStrategicObjective
import org.chai.kevin.survey.SurveyStrategicObjectiveSorter
import org.chai.kevin.survey.SurveySubStrategicObjective;
import org.chai.kevin.survey.SurveySubStrategicObjectiveSorter
import org.chai.kevin.survey.SurveyTranslatable;

abstract class AbstractReportController {

	DashboardObjectiveService dashboardObjectiveService;
	OrganisationService organisationService;
	DsrObjectiveService dsrObjectiveService;
	DataService dataService;

	protected def getObjective() {
		Translatable objective = null
		try {
			if (NumberUtils.isNumber(params['objective'])) {
				objective = DashboardObjective.get(params['objective']);
				if (objective == null) {
					objective = DashboardTarget.get(params['objective']);
				}
			}
			if (objective == null) {
				objective = dashboardObjectiveService.getRootObjective()
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return objective
	}

	protected def getCostObjective() {
		CostObjective objective = null
		try {
			if (NumberUtils.isNumber(params['objective'])) {
				objective = CostObjective.get(params['objective']);
			}
			if (objective == null) {
				// TODO what if there are no objectives ?
				objective = CostObjective.list()[0]
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return objective
	}

	protected def getCostTarget() {
		CostTarget target = null
		try {
			if (NumberUtils.isNumber(params['objective'])) {
				target = CostTarget.get(params['objective']);
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return target
	}

	protected def getMapsTarget() {
		MapsTarget target = null
		try {
			if (NumberUtils.isNumber(params['target'])) {
				target = MapsTarget.get(params['target']);
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return target
	}

	protected def getStrategicObjective() {
		Translatable objective = null
		try {
			if (NumberUtils.isNumber(params['objective'])) {
				objective = DashboardObjective.get(params['objective']);
			}
			if (objective == null) {
				objective = dashboardObjectiveService.getRootObjective()
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return objective
	}

	protected def getOrganisation(def defaultIfNull) {
		Organisation organisation = null;
		try {
			if (NumberUtils.isNumber(params['organisation'])) {
				organisation = organisationService.getOrganisation(new Integer(params['organisation']))
			}
			if (organisation == null && defaultIfNull) {
				organisation = organisationService.getRootOrganisation();
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return organisation
	}

	protected def getOrganisationUnitLevel() {
		Integer level = null;
		try {
			if (NumberUtils.isNumber(params['level'])) {
				level = new Integer(params['level'])
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return level
	}

	protected def getData() {
		Data data = null;
		try {
			if (NumberUtils.isNumber(params['data'])) {
				data = dataService.getData(Long.parseLong(params['data']))
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return data
	}

	protected def getPeriod() {
		Period period = null;
		try {
			if (NumberUtils.isNumber(params['period'])) {
				period = Period.get(params['period'])
			}
			if (period == null) {
				period = Period.findAll()[Period.count()-1]
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return period
	}

	protected def getStrategicObjectiveDsr() {
		Translatable objective = null
		try {
			if (NumberUtils.isNumber(params['objective'])) {
				objective = DsrObjective.get(params['objective']);
			}

			if (objective == null) {
				// TODO what if there are no objectives ?
				objective = DsrObjective.list()[0]
			}
		}
		catch (IllegalStateException e) {
			// TODO
			redirect (controller: '', action: '')
		}
		return objective
	}
	
	//Survey
	protected def getDefaultSurvey(def defaultIsNull){
		//TODO Default Survey have to be set via admin
		SurveyTranslatable survey = null;
		try{
			if(NumberUtils.isNumber(params['survey'])){
				survey = Survey.get(params['survey']);
			}
			if(survey ==  null && defaultIsNull){
				List<Survey> surveys = Survey.list();
				Collections.sort(surveys,new SurveySorter());
				survey= surveys[0];
			}
		}catch(IllegalStateException e){
			redirect (controller: '', action: '')
		}
		return survey
	}
	
	protected def getCurrentObjective(def defaultIsNull){
		SurveyTranslatable objective = null;
		try{
			if(NumberUtils.isNumber(params['objective'])){
				objective = SurveyStrategicObjective.get(params['objective']);
			}
			if (objective == null && defaultIsNull) {
				List<SurveyStrategicObjective> objectives = getDefaultSurvey(true).getObjectives();
				Collections.sort(objectives, new SurveyStrategicObjectiveSorter());
				objective=objectives[0];
			}
		}catch(IllegalStateException e){
			redirect (controller: '', action: '')
		}
		return objective;
	}

	protected def getCurrentSubObjective(def defaultIsNull){
		SurveyTranslatable subobjective = null
		try{
			if(NumberUtils.isNumber(params['subobjective'])){
				subobjective = SurveySubStrategicObjective.get(params['subobjective']);
			}
			if (subobjective == null && defaultIsNull) {
				List<SurveySubStrategicObjective> subobjectives = getCurrentObjective(true).getSubObjectives();
				Collections.sort(subobjectives, new SurveySubStrategicObjectiveSorter());
				subobjective = subobjectives[0];
			}
		}catch(IllegalStateException e){
			redirect (controller: '', action: '')
		}
		return subobjective
	}
	
	protected def getCurrentQuestion(def defaultIsNull){
		SurveyTranslatable question = null;
		try{
			if(NumberUtils.isNumber(params['question'])){
				survey = SurveyQuestion.get(params['question']);
			}
			if (question == null && defaultIsNull) {
				List<SurveyQuestion> questions = getCurrentSubObjective(true).getQuestions();
				Collections.sort(questions, new SurveyQuestionSorter());
				question = questions[0];
			}
		}catch(IllegalStateException e){
			redirect (controller: '', action: '')
		}
		return question
	}
}
