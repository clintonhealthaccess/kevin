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
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

@SuppressWarnings("serial")
@Entity(name = "SurveyStrategicObjective")
@Table(name = "dhsst_survey_strategic_objective")
public class SurveyStrategicObjective extends SurveyTranslatable {

	private Integer id;
	private Integer order;
	private boolean status = true;
	private List<OrganisationUnitGroup> groups;
	private List<SurveySubStrategicObjective> subObjectives = new ArrayList<SurveySubStrategicObjective>();

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

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isStatus() {
		return status;
	}

	public void setSubObjectives(List<SurveySubStrategicObjective> subObjectives) {
		this.subObjectives = subObjectives;
	}

	@OneToMany(cascade = CascadeType.ALL, targetEntity = SurveySubStrategicObjective.class, mappedBy = "objective")
	public List<SurveySubStrategicObjective> getSubObjectives() {
		return subObjectives;
	}

	public void setGroups(List<OrganisationUnitGroup> groups) {
		this.groups = groups;
	}
	// optional has be set to false
	@ManyToOne(targetEntity = OrganisationUnitGroup.class, optional = true)
	public List<OrganisationUnitGroup> getGroups() {
		return groups;
	}

	public void addOrganisationGroup(OrganisationUnitGroup group) {
		groups.add(group);
	}

	public void addSubStrategicObjective(
			SurveySubStrategicObjective subObjective) {
		subObjective.setObjective(this);
		subObjectives.add(subObjective);
	}

	@Transient
	public Map<SurveySubStrategicObjective, List<SurveyQuestion>> getAllQuestionsOfSurveySection() {
		Map<SurveySubStrategicObjective, List<SurveyQuestion>> qSubObjective = null;
		if (!subObjectives.isEmpty()) {
			qSubObjective = new HashMap<SurveySubStrategicObjective, List<SurveyQuestion>>();
			for (SurveySubStrategicObjective subSection : subObjectives)
				qSubObjective.put(subSection, subSection.getQuestions());
		}

		return qSubObjective;

	}
}
