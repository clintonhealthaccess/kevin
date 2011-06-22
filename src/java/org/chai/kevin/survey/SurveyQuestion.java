package org.chai.kevin.survey;

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

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

@SuppressWarnings("serial")
@Entity(name = "SurveyQuestion")
@Table(name = "dhsst_survey_question")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SurveyQuestion extends SurveyTranslatable {
	public static enum Answered {
		ANSWERED, NOTANSWERED, ANSWERNOTCORRECT
	};

	private Integer id;
	private Integer order;
	private boolean status = true;
	private Answered answered = Answered.NOTANSWERED;
	private SurveySubSection subSection;
	//private List<OrganisationUnitGroup> groups;

	public void setId(Integer id) {
		this.id = id;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Basic
	@Column(name = "ordering")
	public Integer getOrder() {
		return order;
	}

	public void setSubSection(SurveySubSection subSection) {
		this.subSection = subSection;
	}

	@ManyToOne(targetEntity = SurveySubSection.class, optional = false)
	public SurveySubSection getSubSection() {
		return subSection;
	}

//	public void setGroups(List<OrganisationUnitGroup> groups) {
//		this.groups = groups;
//	}
//
//	// optional has be set to false
//	@ManyToOne(targetEntity = OrganisationUnitGroup.class, optional = true)
//	public List<OrganisationUnitGroup> getGroups() {
//		return groups;
//	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isStatus() {
		return status;
	}

	public void setAnswered(Answered answered) {
		this.answered = answered;
	}

	public Answered getAnswered() {
		return answered;
	}

//	public void addOrganisationGroup(OrganisationUnitGroup group) {
//		groups.add(group);
//	}

	@Transient
	public String toString() {
		if (this.answered == Answered.ANSWERED)
			return "answered";
		if (this.answered == Answered.NOTANSWERED)
			return "notanswered";
		else
			return "answernotcorrect";

	}

	@Transient
	public abstract String getTemplate();

}
