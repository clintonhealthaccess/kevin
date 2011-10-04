package org.chai.kevin.dashboard;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Organisation;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hisp.dhis.period.Period;

@Entity(name="StrategicObjective")
@Table(name="dhsst_dashboard_objective")
public class DashboardObjective extends DashboardEntry {

	private List<DashboardObjectiveEntry> objectiveEntries = new ArrayList<DashboardObjectiveEntry>();
	
	@OneToMany(mappedBy="parent", targetEntity=DashboardObjectiveEntry.class)
	@OrderBy(value="order")
	public List<DashboardObjectiveEntry> getObjectiveEntries() {
		return objectiveEntries;
	}
	
	public void setObjectiveEntries(List<DashboardObjectiveEntry> objectiveEntries) {
		this.objectiveEntries = objectiveEntries;
	}
	
	public void addObjectiveEntry(DashboardObjectiveEntry objectiveEntry) {
		objectiveEntry.setParent(this);
		objectiveEntry.getEntry().setParent(objectiveEntry);
		objectiveEntries.add(objectiveEntry);
		Collections.sort(objectiveEntries);
	}
	
	@Override
	public DashboardPercentage getValue(PercentageCalculator calculator, Organisation organisation, Period period, boolean isFacility) {
		return calculator.getPercentageForObjective(this, organisation, period);
	}
	
	@Override
	public DashboardExplanation getExplanation(ExplanationCalculator calculator, Organisation organisation, Period period, boolean isFacility) {
		return calculator.explainObjective(this, organisation, period);
	}
	
	@Override
	public boolean hasChildren() {
		return objectiveEntries.size() > 0;
	}
	
	@Override
	@Transient
	public boolean isTarget() {
		return false;
	}
	
}
