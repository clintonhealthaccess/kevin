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

import org.hibernate.criterion.Restrictions;
import org.apache.commons.lang.StringUtils;
import org.chai.kevin.util.Utils;

/**
 * @author Jean Kahigiso M.
 *
 */
class SurveyQuestionService {

	static transactional = true
	def localeService
	def sessionFactory


	List<SurveyQuestion> searchSurveyQuestion(String text, Survey survey){
		// TODO replace by query
		List<SurveyQuestion> questions = null;
		if(survey) {
			questions = sessionFactory.currentSession.createCriteria(SurveyQuestion.class).createAlias("section", "sect").createAlias("sect.objective", "obj").add(Restrictions.eq("obj.survey", survey)).list();
		}else{
			questions = SurveyQuestion.list();
		}
		return searchQuestion(text,questions);
	}


	private List<SurveyQuestion> searchQuestion(String text,List<SurveyQuestion> questions){
		StringUtils.split(text).each { chunk ->
			questions.retainAll { question ->
				Utils.matches(chunk, question.names[localeService.getCurrentLanguage()])
			}
		}
		return questions.sort{it.names[localeService.getCurrentLanguage()]}
	}
}