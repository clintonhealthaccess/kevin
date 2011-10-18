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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Translation;
import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.apache.commons.collections.*;

@SuppressWarnings("serial")
@Entity(name = "SurveySection")
@Table(name = "dhsst_survey_section")
public class SurveySection extends SurveyTranslatable {

	private Long id;
	private Integer order;
	private SurveyObjective objective;
	private String groupUuidString;
	private List<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();

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

	public void setObjective(SurveyObjective objective) {
		this.objective = objective;
	}

	@ManyToOne(targetEntity = SurveyObjective.class, optional = false, fetch=FetchType.LAZY)
	@JoinColumn(nullable = false)
	public SurveyObjective getObjective() {
		return objective;
	}

	@Lob
	public String getGroupUuidString() {
		return groupUuidString;
	}

	public void setGroupUuidString(String groupUuidString) {
		this.groupUuidString = groupUuidString;
	}

	public void setQuestions(List<SurveyQuestion> questions) {
		this.questions = questions;
	}

	@OneToMany(targetEntity = SurveyQuestion.class, mappedBy = "section")
	@Cascade({ CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@OrderBy(value = "order")
	public List<SurveyQuestion> getQuestions() {
		return questions;
	}

	public void addQuestion(SurveyQuestion question) {
		question.setSection(this);
		questions.add(question);
		Collections.sort(questions);
	}

	@Transient
	public Survey getSurvey() {
		return objective.getSurvey();
	}

	@SuppressWarnings("unchecked")
	@Transient
	public Set<String> getOrganisationUnitGroupApplicable() {
		return new HashSet<String>(CollectionUtils.intersection(
				Utils.split(this.groupUuidString),
				this.objective.getOrganisationUnitGroupApplicable()));
	}

	@Transient
	public Integer getQuestionNumber(SurveyQuestion question) {
		return questions.indexOf(question);
	}
	
	@Transient
	public List<SurveyElement> getSurveyElements(OrganisationUnitGroup group) {
		List<SurveyElement> result = new ArrayList<SurveyElement>();
		for (SurveyQuestion question : getQuestions(group)) {
			result.addAll(question.getSurveyElements(group));
		}
		return result;
	}

	@Transient
	public List<SurveyQuestion> getQuestions(OrganisationUnitGroup group) {
		List<SurveyQuestion> result = new ArrayList<SurveyQuestion>();
		for (SurveyQuestion surveyQuestion : getQuestions()) {
			if (Utils.split(surveyQuestion.getGroupUuidString())
					.contains(group.getUuid()))
				result.add(surveyQuestion);
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SurveySection other = (SurveySection) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	protected void deepCopy(SurveySection copy, SurveyCloner surveyCloner) {
		copy.setNames(new Translation(getNames()));
		copy.setDescriptions(new Translation(getDescriptions()));
		copy.setGroupUuidString(getGroupUuidString());
		copy.setObjective(surveyCloner.getObjective(getObjective()));
		copy.setOrder(getOrder());
		for (SurveyQuestion question : getQuestions()) {
			copy.getQuestions().add(surveyCloner.getQuestion(question));
		}
	}
	
}
