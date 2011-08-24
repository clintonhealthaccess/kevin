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
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@SuppressWarnings("serial")
@Entity(name = "SurveyCheckboxOption")
@Table(name = "dhsst_survey_checkbox_option")
public class SurveyCheckboxOption extends SurveyTranslatable {

	private Long id;
	private Integer order;
	private String groupUuidString;
	private SurveyCheckboxQuestion question;
	private SurveyElement surveyElement;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Basic(optional = false)
	@Column(name = "ordering")
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public void setGroupUuidString(String groupUuidString) {
		this.groupUuidString = groupUuidString;
	}

	@Lob
	public String getGroupUuidString() {
		return groupUuidString;
	}

	@ManyToOne(targetEntity = SurveyCheckboxQuestion.class, optional = false)
	@JoinColumn(nullable = false)
	public SurveyCheckboxQuestion getQuestion() {
		return question;
	}

	public void setQuestion(SurveyCheckboxQuestion question) {
		this.question = question;
	}

	@OneToOne(optional = false, targetEntity = SurveyElement.class)
	@Cascade({ CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	public SurveyElement getSurveyElement() {
		return surveyElement;
	}

	public void setSurveyElement(SurveyElement surveyElement) {
		this.surveyElement = surveyElement;
	}

	public Set<String> getOrganisationUnitGroupApplicable() {
		return Utils.getGroupUuids(this.groupUuidString);
	}

	public SurveyCheckboxOption deepCopy(SurveyCloner cloner) {
		SurveyCheckboxOption copy = new SurveyCheckboxOption();
		copy.setDescriptions(getDescriptions());
		copy.setNames(getNames());
		copy.setGroupUuidString(getGroupUuidString());
		copy.setOrder(getOrder());
		copy.setSurveyElement(cloner.getElement(getSurveyElement()));
		copy.setQuestion((SurveyCheckboxQuestion)cloner.getQuestion(getQuestion()));
		return copy;
	}
	
}
