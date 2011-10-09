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
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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

@SuppressWarnings("serial")
@Entity(name = "SurveyObjective")
@Table(name = "dhsst_survey_objective")
public class SurveyObjective extends SurveyTranslatable {

	private Long id;
	private Integer order;
	private Survey survey;
	private List<SurveySection> sections = new ArrayList<SurveySection>();
//	private SurveyObjective dependency;
	private String groupUuidString;

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

	@OneToMany(targetEntity = SurveySection.class, mappedBy = "objective")
	@Cascade({ CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@OrderBy(value = "order")
	public List<SurveySection> getSections() {
		return sections;
	}

	public void setSections(List<SurveySection> sections) {
		this.sections = sections;
	}

	public void addSection(SurveySection section) {
		section.setObjective(this);
		sections.add(section);
		Collections.sort(sections);
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	@ManyToOne(targetEntity = Survey.class, optional = false, fetch=FetchType.LAZY)
	@JoinColumn(nullable = false)
	public Survey getSurvey() {
		return survey;
	}

	public void setGroupUuidString(String groupUuidString) {
		this.groupUuidString = groupUuidString;
	}

	@Lob
	public String getGroupUuidString() {
		return groupUuidString;
	}

//	@ManyToOne(targetEntity = SurveyObjective.class, optional = true)
//	@JoinTable(name="dhsst_survey_objective_dependencies")
//	public SurveyObjective getDependency() {
//		return dependency;
//	}
//
//	public void setDependency(SurveyObjective dependency) {
//		this.dependency = dependency;
//	}

	@Transient
	public Set<String> getOrganisationUnitGroupApplicable() {
		return Utils.split(this.groupUuidString);
	}

	@Transient
	public List<SurveySection> getSections(OrganisationUnitGroup group) {
		List<SurveySection> result = new ArrayList<SurveySection>();
		for (SurveySection surveySection : getSections()) {
			if (Utils.split(surveySection.getGroupUuidString())
					.contains(group.getUuid()))
				result.add(surveySection);
		}
		return result;
	}
	
	@Transient
	public List<SurveyElement> getElements(OrganisationUnitGroup group) {
		List<SurveyElement> result = new ArrayList<SurveyElement>();
		for (SurveySection surveySection : getSections(group)) {
			result.addAll(surveySection.getSurveyElements(group));
		}
		return result;
	}
	
	@Transient
	public List<SurveyQuestion> getQuestions(OrganisationUnitGroup group) {
		List<SurveyQuestion> result = new ArrayList<SurveyQuestion>();
		for (SurveySection surveySection : getSections(group)) {
			result.addAll(surveySection.getQuestions(group));
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SurveyObjective))
			return false;
		SurveyObjective other = (SurveyObjective) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	@Transient
	protected void deepCopy(SurveyObjective copy, SurveyCloner cloner) {
		copy.setNames(new Translation(getNames()));
		copy.setDescriptions(new Translation(getDescriptions()));
//		if (getDependency() != null) copy.setDependency(cloner.getObjective(getDependency()));
		copy.setGroupUuidString(getGroupUuidString());
		copy.setOrder(getOrder());
		copy.setSurvey(cloner.getSurvey(getSurvey()));
		for (SurveySection section : getSections()) {
			copy.getSections().add(cloner.getSection(section));
		}
	}

}
