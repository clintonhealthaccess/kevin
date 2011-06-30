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

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Info;
import org.chai.kevin.Organisation;
import org.chai.kevin.data.Calculation;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ManyToAny;
import org.hisp.dhis.period.Period;

@Entity(name="StrategicTarget")
@Table(name="dhsst_dashboard_target")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DashboardTarget extends DashboardEntry {

	private Calculation calculation;
	
	
//	@Cascade(value={CascadeType.ALL, CascadeType.DELETE_ORPHAN})
	@ManyToOne(targetEntity=Calculation.class, optional=false)
	@JoinColumn(nullable=false)
	public Calculation getCalculation() {
		return calculation;
	}
	
	public void setCalculation(Calculation calculation) {
		this.calculation = calculation;
	}
	
	@Override
	public DashboardExplanation getExplanation(ExplanationCalculator calculator, Organisation organisation, Period period, boolean isFacility) {
		if (isFacility) return calculator.explainLeafTarget(this, organisation, period);
		else return calculator.explainNonLeafTarget(this, organisation, period);
	}	

	@Override
	public DashboardPercentage getValue(PercentageCalculator calculator, Organisation organisation, Period period, boolean isFacility) {
		if (isFacility) return calculator.getPercentageForLeafTarget(this, organisation, period);
		else return calculator.getPercentageForNonLeafTarget(this, organisation, period);
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public String toString() {
		return "StrategicTarget [code=" + code + ", calculation=" + calculation + "]";
	}

	@Override
	@Transient
	public boolean isTarget() {
		return true;
	}

}
