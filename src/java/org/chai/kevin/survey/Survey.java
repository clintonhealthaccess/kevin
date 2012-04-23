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
 * @author Jean Kahigiso M.
 *
 */
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Period;
import org.chai.kevin.Translation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity(name="Survey")
@Table(name="dhsst_survey")
public class Survey {
	
	private Long id;
	private Period lastPeriod;
	private Period period;
	private boolean active = false;
	private List<SurveyProgram> programs = new ArrayList<SurveyProgram>();
	private List<SurveySkipRule> skipRules = new ArrayList<SurveySkipRule>();
	private Translation names = new Translation();
	private Translation descriptions = new Translation();
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Basic
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@ManyToOne(targetEntity=Period.class)
	@JoinColumn(name="period", nullable= false)
	public Period getPeriod() {
		return period;
	}
	
	public void setPeriod(Period period) {
		this.period = period;
	}
	
	@ManyToOne(targetEntity=Period.class)
	@JoinColumn(name="last_period")
	public Period getLastPeriod() {
		return lastPeriod;
	}
	
	public void setLastPeriod(Period lastPeriod) {
		this.lastPeriod = lastPeriod;
	}
	
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonNames", nullable = false)) })
	public Translation getNames() {
		return names;
	}

	public void setNames(Translation names) {
		this.names = names;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonDescriptions", nullable = false)) })
	public Translation getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(Translation descriptions) {
		this.descriptions = descriptions;
	}
	
	@OneToMany(targetEntity = SurveyProgram.class, mappedBy="survey")
	@Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
	@Fetch(FetchMode.SELECT)
	public List<SurveyProgram> getPrograms() {
		return programs;
	}
	
	public void setPrograms(List<SurveyProgram> programs) {
		this.programs = programs;
	}
	
	@Transient
	public void addProgram(SurveyProgram program){
		program.setSurvey(this);
		programs.add(program);
	}
	
	@OneToMany(mappedBy="survey", targetEntity=SurveySkipRule.class)
	@Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
	public List<SurveySkipRule> getSkipRules() {
		return skipRules;
	}
	
	public void setSkipRules(List<SurveySkipRule> skipRules) {
		this.skipRules = skipRules;
	}
	
	public void addSkipRule(SurveySkipRule skipRule) {
		skipRule.setSurvey(this);
		skipRules.add(skipRule);
	}
	
	@Transient
	public List<SurveySection> getSections() {
		List<SurveySection> result = new ArrayList<SurveySection>();
		for (SurveyProgram surveyProgram : getPrograms()) {
			result.addAll(surveyProgram.getSections());
		}
		return result;
	}
	
	@Transient
	public List<SurveyProgram> getPrograms(DataLocationType type) {
		List<SurveyProgram> result = new ArrayList<SurveyProgram>();
		for (SurveyProgram surveyProgram : getPrograms()) {
			if (Utils.split(surveyProgram.getTypeCodeString()).contains(type.getCode())) result.add(surveyProgram);
		}
		return result;
	}
	
	@Transient
	protected void deepCopy(Survey copy, SurveyCloner cloner) {
		copy.setNames(new Translation(getNames()));
		copy.setDescriptions(new Translation(getDescriptions()));
		copy.setActive(isActive());
		copy.setPeriod(getPeriod());
		for (SurveyProgram program : getPrograms()) {
			copy.getPrograms().add(cloner.getProgram(program));
		}
	}

	@Transient
	protected void copyRules(Survey copy, SurveyCloner cloner) {
		for (SurveySkipRule skipRule : getSkipRules()) {
			copy.getSkipRules().add(cloner.getSkipRule(skipRule));
		}
	}

	@Override
	public String toString() {
		return "Survey [getId()=" + getId() + "]";
	}
	
//	@Override
//	public String toString() {
//		return "Survey [getId()=" + getId() + ", getCode()="
//				+ getCode() + "]";
//	}
}
