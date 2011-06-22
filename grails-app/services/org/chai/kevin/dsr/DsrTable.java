package org.chai.kevin.dsr;

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
import java.util.Map;

import org.chai.kevin.Organisation;
import org.hisp.dhis.period.Period;

public class DsrTable {
	private Period period;
	private Organisation organisation;
	private DsrObjective objective;
	private List<Organisation> organisations;
	private List<DsrTarget> targets;
	private Map<Organisation, Map<DsrTarget, Dsr>> values;


	public DsrTable(Organisation organisation,List<Organisation> organisations, Period period, DsrObjective objective,
			List<DsrTarget> targets,
			Map<Organisation, Map<DsrTarget, Dsr>> values) {
		super();
		this.organisation = organisation;
		this.organisations = organisations;
		this.period = period;
		this.objective = objective;
		this.targets = targets;
		this.values = values;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Period getPeriod() {
		return period;
	}

	public void setOrganisations(List<Organisation> organisations) {
		this.organisations = organisations;
	}

	public List<Organisation> getOrganisations() {
		return organisations;
	}

	public void setTargets(List<DsrTarget> targets) {
		this.targets = targets;
	}

	public List<DsrTarget> getTargets() {
		return targets;
	}

	public void setObjective(DsrObjective objective) {
		this.objective = objective;
	}

	public DsrObjective getObjective() {
		return objective;
	}
	
	public Map<Organisation, Map<DsrTarget, Dsr>> getValues() {
		return values;
	}

	public void setValues(Map<Organisation, Map<DsrTarget, Dsr>> values) {
		this.values = values;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public Organisation getOrganisation() {
		return organisation;
	}
	
	public Object getDsrValue(Organisation organisation, DsrTarget target) {

		return values.get(organisation).get(target).getValue();

	}

}
