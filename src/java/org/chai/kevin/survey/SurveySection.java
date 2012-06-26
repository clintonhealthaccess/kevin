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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.collections.CollectionUtils;
import org.chai.kevin.Exportable;
import org.chai.kevin.Orderable;
import org.chai.kevin.Translation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity(name = "SurveySection")
@Table(name = "dhsst_survey_section", uniqueConstraints={@UniqueConstraint(columnNames="code")})
public class SurveySection extends Orderable<Integer> implements Exportable {

	private Long id;
	private String code;
	private Integer order;
	private SurveyProgram program;
	private String typeCodeString;
	private List<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();
	private Translation names = new Translation();

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

	public void setProgram(SurveyProgram program) {
		this.program = program;
	}

	@ManyToOne(targetEntity=SurveyProgram.class, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public SurveyProgram getProgram() {
		return program;
	}

	@Lob
	public String getTypeCodeString() {
		return typeCodeString;
	}

	public void setTypeCodeString(String typeCodeString) {
		this.typeCodeString = typeCodeString;
	}
	
	@Transient
	public Set<String> getTypeCodes() {
		return Utils.split(typeCodeString, DataLocationType.DEFAULT_CODE_DELIMITER);
	}
	public void setTypeCodes(Set<String> typeCodes) {
		this.typeCodeString = Utils.unsplit(typeCodes, DataLocationType.DEFAULT_CODE_DELIMITER);
	}

	@OneToMany(targetEntity=SurveyQuestion.class, mappedBy="section", orphanRemoval=true)
	@Cascade({ CascadeType.ALL })
	@Fetch(FetchMode.SELECT)
	public List<SurveyQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<SurveyQuestion> questions) {
		this.questions = questions;
	}

	public void addQuestion(SurveyQuestion question) {
		question.setSection(this);
		questions.add(question);
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonNames", nullable = false)) })
	public Translation getNames() {
		return names;
	}

	public void setNames(Translation names) {
		this.names = names;
	}

	@Transient
	public Survey getSurvey() {
		return program.getSurvey();
	}

	@SuppressWarnings("unchecked")
	@Transient
	public Set<String> getTypeApplicable() {
		return new HashSet<String>(CollectionUtils.intersection(
				Utils.split(this.typeCodeString, DataLocationType.DEFAULT_CODE_DELIMITER),
				this.program.getTypeApplicable()));
	}

	@Transient
	public List<SurveyElement> getSurveyElements(DataLocationType type) {
		List<SurveyElement> result = new ArrayList<SurveyElement>();
		for (SurveyQuestion question : getQuestions(type)) {
			result.addAll(question.getSurveyElements(type));
		}
		return result;
	}

	@Transient
	public List<SurveyQuestion> getQuestions(DataLocationType type) {
		List<SurveyQuestion> result = new ArrayList<SurveyQuestion>();
		for (SurveyQuestion surveyQuestion : getQuestions()) {
			if (Utils.split(surveyQuestion.getTypeCodeString(), DataLocationType.DEFAULT_CODE_DELIMITER).contains(type.getCode())) {
				result.add(surveyQuestion);
			}
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
		if (!(obj instanceof SurveySection))
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
		copy.setCode(getCode() + " clone");
		copy.setNames(new Translation(getNames()));
		copy.setTypeCodeString(getTypeCodeString());
		copy.setProgram(surveyCloner.getProgram(getProgram()));
		copy.setOrder(getOrder());
		for (SurveyQuestion question : getQuestions()) {
			copy.getQuestions().add(surveyCloner.getQuestion(question));
		}
	}

	@Override
	public String toString() {
		return "SurveySection[getId()=" + getId() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
