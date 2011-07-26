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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.survey.validation.SurveySkipRule;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;

@SuppressWarnings("serial")
@Entity(name="Survey")
@Table(name="dhsst_survey")
public class Survey extends SurveyTranslatable {
	
	private Long id;
	private Integer order;
	private boolean open = true;
	private Period period;
	private List<SurveyObjective> objectives = new ArrayList<SurveyObjective>();
	private Set<SurveySkipRule> skipRules = new HashSet<SurveySkipRule>();
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Basic
	@Column(name = "ordering")
	public Integer getOrder() {
		return order;
	}
	
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	@Basic
	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
	
	@Column(name="iteration")
	public Period getPeriod() {
		return period;
	}
	
	public void setPeriod(Period period) {
		this.period = period;
	}
	
	@OneToMany(targetEntity = SurveyObjective.class, mappedBy="survey", fetch=FetchType.EAGER)
	@Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
	@OrderBy(value="order")
	public List<SurveyObjective> getObjectives() {
		return objectives;
	}
	
	public void setObjectives(List<SurveyObjective> objectives) {
		this.objectives = objectives;
	}
	
	@Transient
	public void addObjective(SurveyObjective objective){
		objective.setSurvey(this);
		objectives.add(objective);
		Collections.sort(objectives);
	}
	
	@OneToMany(mappedBy="survey", targetEntity=SurveySkipRule.class)
	@Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
	public Set<SurveySkipRule> getSkipRules() {
		return skipRules;
	}
	
	public void setSkipRules(Set<SurveySkipRule> skipRules) {
		this.skipRules = skipRules;
	}
	
	public void addSkipRule(SurveySkipRule skipRule) {
		skipRule.setSurvey(this);
		skipRules.add(skipRule);
	}
	
	@Transient
	public List<SurveyObjective> getObjectives(OrganisationUnitGroup group) {
		List<SurveyObjective> result = new ArrayList<SurveyObjective>();
		for (SurveyObjective surveyObjective : getObjectives()) {
			if (surveyObjective.getGroups().contains(group)) result.add(surveyObjective);
		}
		return result;
	}

}
