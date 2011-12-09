package org.chai.kevin.maps;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Organisation;
import org.chai.kevin.LocationService;
import org.chai.kevin.data.Info;
import org.chai.kevin.data.InfoService;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public class MapsService {

	private static final Log log = LogFactory.getLog(MapsService.class);
	
	private LocationService locationService;
	private ValueService valueService;
	private InfoService infoService;
	
	public Maps getMap(Period period, Organisation organisation, Integer level, MapsTarget target) {
		if (log.isDebugEnabled()) log.debug("getMap(period="+period+",organisation="+organisation+",target="+target+")");

		List<Polygon> polygons = new ArrayList<Polygon>();
		locationService.loadParent(organisation);
		locationService.loadLevel(organisation);
		
		List<OrganisationUnitLevel> levels = locationService.getAllLevels();
		levels.remove(0);
		
		if (levels.isEmpty()) {
			// TODO throw exception
		}
		
		if (level == null) {
			List<OrganisationUnitLevel> childLevels = locationService.getChildren(locationService.loadLevel(organisation));
			if (levels.size() > 0) level = childLevels.get(0).getLevel();
			else level = levels.get(0).getLevel();
		}
		
		// if we ask for an organisation level bigger than the organisation's, we go back to the right level
		while (level <= organisation.getLevel()) {
			organisation = organisation.getParent();
			locationService.loadParent(organisation);
		}
		
		if (target == null) return new Maps(period, target, organisation, level, polygons, levels);
		
		for (Organisation child : locationService.getChildrenOfLevel(organisation, level)) {
			locationService.loadLevel(child);
			
			Double value = null;
			// TODO groups
			CalculationValue<?> calculationValue = valueService.getCalculationValue(target.getCalculation(), child.getOrganisationUnit(), period, Utils.getUuids(locationService.getDataEntityTypes()));
			if (calculationValue != null) {
				if (!calculationValue.getValue().isNull()) {
					value = calculationValue.getValue().getNumberValue().doubleValue();
				}
			}
			
			polygons.add(new Polygon(child, value));
		}

		return new Maps(period, target, organisation, level, polygons, levels);
	}

	public Info<?> getExplanation(Period period, Organisation organisation, MapsTarget target) {
		// TODO groups
		return infoService.getCalculationInfo(target.getCalculation(), organisation, period, Utils.getUuids(locationService.getDataEntityTypes()));
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setOrganisationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
	}
	
}
