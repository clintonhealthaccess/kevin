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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@SuppressWarnings("serial")
@Entity(name = "SurveyTableRow")
@Table(name = "dhsst_survey_table_row")
public class SurveyTableRow extends SurveyTranslatable {

	private Long id;
	private Integer order;
	private String groupUuidString;
	private SurveyTableQuestion question;
	private Map<SurveyTableColumn, SurveyElement> surveyElements = new LinkedHashMap<SurveyTableColumn, SurveyElement>();

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

	@Lob
	public String getGroupUuidString() {
		return groupUuidString;
	}

	public void setGroupUuidString(String groupUuidString) {
		this.groupUuidString = groupUuidString;
	}

	@ManyToOne(targetEntity = SurveyTableQuestion.class, optional = false)
	@JoinColumn(nullable = false)
	public SurveyTableQuestion getQuestion() {
		return question;
	}

	public void setQuestion(SurveyTableQuestion question) {
		this.question = question;
	}

	@OneToMany(targetEntity = SurveyElement.class)
	@JoinTable(name = "dhsst_survey_table_row_elements", joinColumns=@JoinColumn(nullable=false))
	@MapKeyJoinColumn(nullable = false, name="survey_table_column")
	@Cascade({ CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	public Map<SurveyTableColumn, SurveyElement> getSurveyElements() {
		return surveyElements;
	}

	public void setSurveyElements(
			Map<SurveyTableColumn, SurveyElement> surveyElements) {
		this.surveyElements = surveyElements;
	}

	@Transient
	public Set<String> getOrganisationUnitGroupApplicable() {
		return Utils.split(this.groupUuidString);
	}
	
    @Transient
	protected SurveyTableRow deepCopy(SurveyCloner cloner, Map<Long, SurveyTableColumn> columns) {
    	SurveyTableRow copy = new SurveyTableRow();
    	copy.setNames(getNames());
    	copy.setDescriptions(getDescriptions());
    	copy.setGroupUuidString(getGroupUuidString());
    	copy.setOrder(getOrder());
    	copy.setQuestion((SurveyTableQuestion)cloner.getQuestion(getQuestion()));
    	for (Entry<SurveyTableColumn, SurveyElement> entry : getSurveyElements().entrySet()) {
			copy.getSurveyElements().put(columns.get(entry.getKey().getId()), cloner.getElement(entry.getValue()));
		}
    	return copy;
	}

}
