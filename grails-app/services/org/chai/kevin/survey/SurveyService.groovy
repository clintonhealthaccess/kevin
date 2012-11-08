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

import java.util.Date;
import java.util.List
import java.util.Map
import java.util.Set

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.math.NumberUtils
import org.apache.shiro.SecurityUtils;
import org.chai.kevin.data.RawDataElement
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.form.FormSkipRuleElementMap;
import org.chai.kevin.form.FormValidationRuleDependency;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType
import org.chai.kevin.util.Utils
import org.hibernate.FlushMode
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Order
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions

/**
 * @author Jean Kahigiso M.
 *
 */
class SurveyService {

	static transactional = true
	
	def languageService
	def dataService
	def locationService
	def sessionFactory

	static void setUserAndTimestamp(def enteredValue) {
		enteredValue.setUserUuid(SecurityUtils.getSubject().getPrincipal())
		enteredValue.setTimestamp(new Date())
	}
	
	void deleteQuestion(def question) {
		SurveyEnteredQuestion.executeUpdate("delete from SurveyEnteredQuestion where question = :question", ['question': question])
		// we delete all the referenced survey skip rule
		SurveySkipRule.withCriteria{skippedSurveyQuestions {eq('id', question.id)}}.each { skipRule ->
			skipRule.removeFromSkippedSurveyQuestions(question)
			skipRule.save()
		}
		question.section.removeFromQuestions(question)
		// we delete all the survey elements
		question.surveyElements.each { element ->
			deleteSurveyElement(element)
		}
		question.delete()
	}
	
	void deleteSurveyElement(def surveyElement) {
		// we delete the form entered value
		FormEnteredValue.executeUpdate("delete from FormEnteredValue where formElement = :formElement", ['formElement': surveyElement])
		// we delete all the survey elements on all the skip rules they are referenced from
		FormSkipRuleElementMap.findAllByFormElement(surveyElement).each { map ->
			def skipRule = map.skipRule
			skipRule.removeFromFormSkipRuleElementMaps(map)
			map.delete()
			map.skipRule = skipRule
			skipRule.save()
		}
		// we delete all the dependencies
		FormValidationRuleDependency.findAllByFormElement(surveyElement).each { dependency ->
			def validationRule = dependency.validationRule
			validationRule.removeFromValidationRuleDependencies(dependency)
			dependency.delete()
			dependency.validationRule = validationRule
			validationRule.save()
		}
		// we delete the survey element from the question
		def question = surveyElement.question
		question.removeFromSurveyElements(surveyElement)
		surveyElement.delete()
	}
	
	SurveyQuestion getSurveyQuestion(Long id) {
		// TODO test this with Grails 2.0
		return sessionFactory.currentSession.get(SurveyQuestion.class, id)
	}
	
	List<SurveyQuestion> searchSurveyQuestions(String text, Survey survey, def params = [:]) {
		
		def dbFieldName = 'names_' + languageService.currentLanguage;
		def dbFieldDescription = 'names_' + languageService.currentLanguage;
		
		def criteria = SurveyQuestion.createCriteria()
		return criteria.list(offset:params.offset, max:params.max, sort:params.sort ?:"id", order: params.order ?:"asc"){
			if (survey != null) {
				createAlias('section', 'ss')
				createAlias('ss.program', 'so')
				eq ('so.survey', survey)
			}
			StringUtils.split(text).each { chunk ->
				 or{
					 ilike("code","%"+chunk+"%")
					 ilike(dbFieldName,"%"+chunk+"%")
					 ilike(dbFieldDescription,"%"+chunk+"%")
				 }
			}
		}
	}
	
	Set<SurveyElement> getSurveyElements(RawDataElement dataElement, Survey survey) {
		if (log.isDebugEnabled()) log.debug("getSurveyElements(dataElement=${dataElement}, survey=${survey})")
		def c = sessionFactory.currentSession.createCriteria(SurveyElement.class)
		if (survey != null) {
			c.createAlias("question", "sq")
			.createAlias("sq.section", "ss")
			.createAlias("ss.program", "so")
			.add(Restrictions.eq("so.survey", survey))
		}
		if (dataElement != null) {
			c.add(Restrictions.eq("dataElement", dataElement))
		}
		
		def result = c.setFlushMode(FlushMode.COMMIT).list()
		if (log.isDebugEnabled()) log.debug("getSurveyElements(...)=${result}")
		return result
	}

	Integer getNumberOfApplicableDataLocationTypes(SurveyElement surveyElement) {
		Set<String> typeCodes = surveyElement.getTypeApplicable();
		int number = 0;
		for (String typeCode : typeCodes) {
			DataLocationType type = locationService.findDataLocationTypeByCode(typeCode);
			if (type != null) number += DataLocation.countByType(type)
		}
		return number;
	}
	
}