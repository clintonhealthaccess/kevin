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

import org.chai.kevin.Orderable;
import org.chai.kevin.Translation;
import org.chai.kevin.entity.export.Exportable;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity(name = "SurveyProgram")
@Table(name = "dhsst_survey_program")
public class SurveyProgram extends Orderable<Integer> implements Exportable {

	private Long id;
	private String code;
	private Integer order;
	private Survey survey;
	private List<SurveySection> sections = new ArrayList<SurveySection>();
	private String typeCodeString;
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
	
	@OneToMany(targetEntity=SurveySection.class, mappedBy="program")
	@Cascade({ CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@Fetch(FetchMode.SELECT)
	public List<SurveySection> getSections() {
		return sections;
	}

	public void setSections(List<SurveySection> sections) {
		this.sections = sections;
	}

	public void addSection(SurveySection section) {
		section.setProgram(this);
		sections.add(section);
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	@ManyToOne(targetEntity=Survey.class, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public Survey getSurvey() {
		return survey;
	}

	public void setTypeCodeString(String typeCodeString) {
		this.typeCodeString = typeCodeString;
	}

	@Lob
	public String getTypeCodeString() {
		return typeCodeString;
	}

	@Transient
	public Set<String> getTypeCodes() {
		return Utils.split(typeCodeString);
	}
	public void setTypeCodes(Set<String> typeCodes) {
		this.typeCodeString = Utils.unsplit(typeCodes);
	}
	

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonNames", nullable = false)) })
	public Translation getNames() {
		return names;
	}

	public void setNames(Translation names) {
		this.names = names;
	}
	
//	@ManyToOne(targetEntity = SurveyProgram.class, optional = true)
//	@JoinTable(name="dhsst_survey_program_dependencies")
//	public SurveyProgram getDependency() {
//		return dependency;
//	}
//
//	public void setDependency(SurveyProgram dependency) {
//		this.dependency = dependency;
//	}

	@Transient
	public Set<String> getTypeApplicable() {
		return Utils.split(this.typeCodeString);
	}

	@Transient
	public List<SurveySection> getSections(DataLocationType type) {
		List<SurveySection> result = new ArrayList<SurveySection>();
		for (SurveySection surveySection : getSections()) {
			if (Utils.split(surveySection.getTypeCodeString()).contains(type.getCode()))
				result.add(surveySection);
		}
		return result;
	}
	
	@Transient
	public List<SurveyElement> getElements(DataLocationType type) {
		List<SurveyElement> result = new ArrayList<SurveyElement>();
		for (SurveySection surveySection : getSections(type)) {
			result.addAll(surveySection.getSurveyElements(type));
		}
		return result;
	}
	
	@Transient
	public List<SurveyQuestion> getQuestions(DataLocationType type) {
		List<SurveyQuestion> result = new ArrayList<SurveyQuestion>();
		for (SurveySection surveySection : getSections(type)) {
			result.addAll(surveySection.getQuestions(type));
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
		if (!(obj instanceof SurveyProgram))
			return false;
		SurveyProgram other = (SurveyProgram) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	@Transient
	protected void deepCopy(SurveyProgram copy, SurveyCloner cloner) {
		copy.setNames(new Translation(getNames()));
//		if (getDependency() != null) copy.setDependency(cloner.getProgram(getDependency()));
		copy.setTypeCodeString(getTypeCodeString());
		copy.setOrder(getOrder());
		copy.setSurvey(cloner.getSurvey(getSurvey()));
		for (SurveySection section : getSections()) {
			copy.getSections().add(cloner.getSection(section));
		}
	}

	@Override
	public String toString() {
		return "SurveyProgram[getId()=" + getId() + "]";
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
