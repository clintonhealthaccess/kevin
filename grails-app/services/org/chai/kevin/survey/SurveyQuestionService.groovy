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

import java.util.List;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.apache.commons.lang.StringUtils;
import org.chai.kevin.util.LanguageUtils;
import org.chai.kevin.util.Utils;

/**
 * @author Jean Kahigiso M.
 *
 */
class SurveyQuestionService {

	static transactional = true
	
	def sessionFactory

	List<SurveyQuestion> searchSurveyQuestions(String text, Survey survey, def params = [:]) {
		def criteria = getSearchCriteria(text, survey)
		if (params['offset'] != null) criteria.setFirstResult(params['offset'])
		if (params['max'] != null) criteria.setMaxResults(params['max'])
		else criteria.setMaxResults(500)
		
		List<SurveyQuestion> questions = criteria.addOrder(Order.asc("id")).list()
		
		StringUtils.split(text).each { chunk ->
			questions.retainAll { question ->
				Utils.matches(chunk, question.names[LanguageUtils.getCurrentLanguage()]) ||
				Utils.matches(chunk, question.descriptions[LanguageUtils.getCurrentLanguage()])
			}
		}
		
		return questions;
	}
	
	Integer countSurveyQuestions(String text, Survey survey) {
		return getSearchCriteria(text, survey).setProjection(Projections.count("id")).uniqueResult()
	}

	private def getSearchCriteria(String text, Survey survey) {
		def criteria = sessionFactory.currentSession.createCriteria(SurveyQuestion.class)
		
		def textRestrictions = Restrictions.conjunction()
		StringUtils.split(text).each { chunk ->
			def disjunction = Restrictions.disjunction();
			
			disjunction.add(Restrictions.ilike("names.jsonText", chunk, MatchMode.ANYWHERE))
			disjunction.add(Restrictions.ilike("descriptions.jsonText", chunk, MatchMode.ANYWHERE))

			textRestrictions.add(disjunction)
		}
		criteria.add(textRestrictions)
		
		if (survey != null) {
			criteria.createAlias("section", "ss")
			.createAlias("ss.objective", "so")
			.add(Restrictions.eq("so.survey", survey))
		}
		
		return criteria
	}
	
	
}