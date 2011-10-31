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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Translation;

import org.chai.kevin.util.Utils;

@SuppressWarnings("serial")
@Entity(name = "SurveyTableColumn")
@Table(name = "dhsst_survey_table_column")
public class SurveyTableColumn extends SurveyTranslatable {

	private Long id;
	private Integer order;
	private String groupUuidString;
	private SurveyTableQuestion question;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Basic
	@Column(name="ordering")
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Lob
	public String getGroupUuidString() {
		return groupUuidString;
	}

	public void setGroupUuidString(String groupUuidString) {
		this.groupUuidString = groupUuidString;
	}
	
	@Transient
	public Set<String> getGroupUuids() {
		return Utils.split(groupUuidString);
	}
	
	public void setGroupUuids(Set<String> groupUuids) {
		this.groupUuidString = Utils.unsplit(groupUuids);
	}

	@ManyToOne(targetEntity=SurveyTableQuestion.class, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public SurveyTableQuestion getQuestion() {
		return question;
	}

	public void setQuestion(SurveyTableQuestion question) {
		this.question = question;
	}
	
	
	@Transient
	public Set<String> getOrganisationUnitGroupApplicable() {
		return Utils.split(this.groupUuidString);
	}

	@Transient
	protected SurveyTableColumn deepCopy(SurveyCloner cloner) {
		SurveyTableColumn copy = new SurveyTableColumn();
		copy.setNames(new Translation(getNames()));
		copy.setDescriptions(new Translation(getDescriptions()));
		copy.setGroupUuidString(getGroupUuidString());
		copy.setOrder(getOrder());
		copy.setQuestion((SurveyTableQuestion)cloner.getQuestion(getQuestion()));
		return copy;
	}

}
