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
 * @author JeanKahigiso
 *
 */
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.chai.kevin.Exportable;
import org.chai.kevin.util.Utils;
import org.chai.location.DataLocationType;

class SurveySimpleQuestion extends SurveyQuestion implements Exportable {

	static mapping = {
		table 'dhsst_survey_single_question'
	}
	
	SurveyElement getSurveyElement() {
		if (surveyElements != null && surveyElements.size() > 0) return surveyElements.iterator().next()
		else return null
	}
	
	@Override
	public QuestionType getType() {
		return QuestionType.SIMPLE;
	}

	@Override
	public List<SurveyElement> getSurveyElements(DataLocationType type) {
		List<SurveyElement> elements = new ArrayList<SurveyElement>();
		if (surveyElement != null) elements.add(surveyElement);
		return elements;
	}

	@SuppressWarnings("unchecked")
	public Set<String> getTypeApplicable(SurveyElement surveyElement) {
		if (!surveyElement.equals(this.surveyElement)) {
			throw new IllegalArgumentException("survey element does not belong to question (simple)");
		}
		return new HashSet<String>(CollectionUtils.intersection(
			Utils.split(this.getTypeCodeString(), Utils.DEFAULT_TYPE_CODE_DELIMITER),
			this.getSection().getTypeApplicable())
		);
	}
	
	@Override
	protected SurveySimpleQuestion newInstance() {
		return new SurveySimpleQuestion();
	}

	@Override
	protected void deepCopy(SurveyQuestion question, SurveyCloner surveyCloner) {
		SurveySimpleQuestion copy = (SurveySimpleQuestion)question;
		super.deepCopy(copy, surveyCloner);
	}

	@Override
	public String toString() {
		return "SurveySimpleQuestion[getId()=" + getId() + ", getNames()=" + getNames() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}
}
