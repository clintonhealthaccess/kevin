package org.chai.kevin

import org.apache.commons.lang.StringEscapeUtils;
import org.chai.kevin.util.Utils;

import org.chai.kevin.survey.Survey
import org.chai.kevin.survey.SurveyProgram;
import org.chai.kevin.survey.SurveySection;
import org.chai.kevin.survey.SurveyQuestion;
import org.chai.location.DataLocationType
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

class SurveyTagLib {
	
	def locationService
	
	def prettySurveyFacilityTypeList = { attrs, body ->
		def surveyEntity = attrs['surveyEntity']
		def typeEntities = attrs['typeEntities']
		def splitDelim = attrs['split'] ?: ';'
		def joinDelim = attrs['join'] ?: '<br />'

		def text = null

		if(typeEntities instanceof String){
			typeEntities = typeEntities.split(splitDelim)
		}
		if (log.isDebugEnabled()) log.debug("prettySurveyFacilityTypeList(typeEntities=${typeEntities})")

		def entityFacilityTypeCount = []
		for (String typeEntity : typeEntities) {
			DataLocationType type = locationService.findDataLocationTypeByCode(typeEntity);
			if (type != null){
				def facilityTypeCount = null
				def facilityTypeCountLabel = null
				if(surveyEntity instanceof SurveySection){
					def section = (SurveySection)surveyEntity
					facilityTypeCount = section.getQuestions(type).size()
					facilityTypeCountLabel = facilityTypeCount == 1 ? message(code:'survey.question.label') : message(code:'survey.questions.label')
				}
				else if(surveyEntity instanceof SurveyProgram){
					def program = (SurveyProgram)surveyEntity
					facilityTypeCount = program.getSections(type).size()
					facilityTypeCountLabel = facilityTypeCount == 1 ? message(code:'survey.section.label') : message(code:'survey.sections.label')
				}
				else if(surveyEntity instanceof Survey){
					def survey = (Survey)surveyEntity
					facilityTypeCount = survey.getPrograms(type).size()
					facilityTypeCountLabel = facilityTypeCount == 1 ? message(code:'survey.program.label') : message(code:'survey.programs.label')
				}

				if(facilityTypeCount != null)
					entityFacilityTypeCount.add(typeEntity+' ('+facilityTypeCount+' '+facilityTypeCountLabel+')')
			}
		}
		text = entityFacilityTypeCount.join(joinDelim)
		out << text
	}
}
