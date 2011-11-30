package org.chai.kevin.cost;

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
import java.util.List;
import java.util.Map;

import org.chai.kevin.cost.CostTarget.CostType;

public class CostTable {

	private List<CostTarget> targets;
	private List<Integer> years;
	private Map<CostTarget, Map<Integer, Cost>> values;


	public CostTable(List<CostTarget> targets, List<Integer> years,
			Map<CostTarget, Map<Integer, Cost>> values) {
		super();
		this.years = years;
		this.values = values;
		this.targets = targets;
	}
	
	
	public List<CostTarget> getTargets() {
		return targets;
	}
	
	public List<CostTarget> getTargetsOfType(CostType type) {
		List<CostTarget> result = new ArrayList<CostTarget>();
		for (CostTarget costTarget : getTargets()) {
			if (costTarget.getCostType().equals(type)) result.add(costTarget);
		}
		return result;
	}
	
	public List<Integer> getYears() {
		return years;
	}
	
	public Cost getCost(CostTarget target, Integer year) {
		return values.get(target).get(year);
	}
	
}
