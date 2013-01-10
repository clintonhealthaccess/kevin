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

import org.chai.kevin.Exportable;
import org.chai.kevin.util.Utils;
import org.chai.kevin.util.DataUtils;
import org.chai.location.DataLocationType;

import org.apache.commons.collections.CollectionUtils

public class SurveyCheckboxQuestion extends SurveyQuestion implements Exportable {

	static hasMany = [options: SurveyCheckboxOption]
	
	static mapping = {
		table 'dhsst_survey_checkbox_question'
		options cascade: "all-delete-orphan"
	}
	
	@Override
	public QuestionType getType() {
		return QuestionType.CHECKBOX;
	}

	@Override
	public List<SurveyElement> getSurveyElements(DataLocationType type) {
		List<SurveyElement> dataElements = new ArrayList<SurveyElement>();
		for (SurveyCheckboxOption option : getOptions(type)) {
			if (option.getSurveyElement() != null) dataElements.add(option.getSurveyElement());
		}
		return dataElements;
	}

//	@Override
//	public List<SurveyElement> getSurveyElements() {
//		List<SurveyElement> dataElements = new ArrayList<SurveyElement>();
//		for (SurveyCheckboxOption option : getOptions()) {
//			if (option.getSurveyElement() != null) dataElements.add(option.getSurveyElement());
//		}
//		return dataElements;
//	}

	@Override
	public void removeSurveyElement(SurveyElement surveyElement) {
		super.removeSurveyElement(surveyElement)
		for (SurveyCheckboxOption option : getOptions()) {
			if (option.getSurveyElement() != null && option.getSurveyElement().equals(surveyElement)) option.setSurveyElement(null);
		}
	}

	public List<SurveyCheckboxOption> getOptions(DataLocationType type) {
		List<SurveyCheckboxOption> result = new ArrayList<SurveyCheckboxOption>();
		for (SurveyCheckboxOption surveyCheckboxOption : getOptions()) {
			if (DataUtils.split(surveyCheckboxOption.getTypeCodeString(), DataUtils.DEFAULT_TYPE_CODE_DELIMITER)
					.contains(type.getCode()))
				result.add(surveyCheckboxOption);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Set<String> getTypeApplicable(SurveyElement surveyElement) {
		Set<String> optionOrgUnitUuIds = new HashSet<String>();

		boolean found = false;
		for (SurveyCheckboxOption option : this.getOptions()) {
			if (surveyElement.equals(option.getSurveyElement())) {
				found = true;
				optionOrgUnitUuIds.addAll(CollectionUtils.intersection(
					option.getTypeApplicable(),
					DataUtils.split(this.getTypeCodeString(), DataUtils.DEFAULT_TYPE_CODE_DELIMITER))
				);
			}
		}

		if (!found) {
			throw new IllegalArgumentException("survey element does not belong to question (options)");
		}
		return new HashSet<String>(CollectionUtils.intersection(optionOrgUnitUuIds,
				this.getSection().getTypeApplicable()));
	}

	@Override
	protected SurveyCheckboxQuestion newInstance() {
		return new SurveyCheckboxQuestion();
	}

	@Override
	protected void deepCopy(SurveyQuestion question, SurveyCloner cloner) {
		SurveyCheckboxQuestion copy = (SurveyCheckboxQuestion)question;
		super.deepCopy(copy, cloner);
		for (SurveyCheckboxOption option : getOptions()) {
			copy.addToOptions(option.deepCopy(cloner));
		}
	}

	@Override
	public String toString() {
		return "SurveyCheckboxQuestion[getId()=" + getId() + ", getNames()=" + getNames() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}
}
