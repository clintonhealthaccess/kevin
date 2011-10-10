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

import org.chai.kevin.AbstractEntityController;
import org.apache.commons.lang.math.NumberUtils;
/**
 * @author Jean Kahigiso M.
 *
 */
class SurveyElementController {

	def surveyElementService;
	def localeService;
	
	def getHtmlData = {
		Survey survey = Survey.get(params.int('surveyId'));
		List<String> allowedTypes = params.list('include');
		Set<SurveyElement> surveyElements =surveyElementService.searchSurveyElements(params['searchText'], survey, allowedTypes);
		
		render(contentType:"text/json") {
			result = 'success'
			html = g.render(template:'/templates/surveyElements', model:[surveyElements: surveyElements])
		}
	}

	def getAjaxData = {
		Survey survey = Survey.get(params.int('surveyId'));
		List<String> allowedTypes = params.list('include');
		Set<SurveyElement> surveyElements =surveyElementService.searchSurveyElements(params['term'], survey, allowedTypes);

		render(contentType:"text/json") {
			elements = array {
				surveyElements.each { surveyElement ->
					elem (
						id: surveyElement.id,
						surveyElement: g.i18n(field: surveyElement.dataElement.names)+'['+surveyElement.id+']'
					)
				}
			}
		}
	}


	def getSurveyElementExplainer = {
		def surveyElement = null;
		if (NumberUtils.isNumber(params['surveyElement'])) {
			surveyElement = SurveyElement.get(params['surveyElement'])
		}

		if (surveyElement == null) {
			render(contentType:"text/json") { result = 'error' }
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: '/templates/surveyElementExplainer', model: [surveyElement: surveyElement])
			}
		}
	}
}