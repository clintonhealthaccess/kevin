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

import java.util.List;

import org.chai.kevin.Organisation;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;

public class Maps {

	private Organisation organisation;
	private Integer level;
	private Period period;
	private MapsTarget target;
	
	private List<Polygon> polygons;
	private List<OrganisationUnitLevel> levels;
	
	public Maps(Period period, MapsTarget target, Organisation organisation, Integer level, List<Polygon> polygons, List<OrganisationUnitLevel> levels) {
		this.period = period;
		this.target = target;
		this.polygons = polygons;
		this.organisation = organisation;
		this.levels = levels;
		this.level = level;
	}
	
	public Period getPeriod() {
		return period;
	}
	
	public MapsTarget getTarget() {
		return target;
	}
	
	public Organisation getOrganisation() {
		return organisation;
	}
	
	public Integer getLevel() {
		return level;
	}
	
	public List<Polygon> getPolygons() {
		return polygons;
	}
	
	
	public String toJson() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("\"polygons\":[");
		for (Polygon polygon : polygons) {
			builder.append(polygon.toJson());
			builder.append(",");
		}
		if (polygons.size() != 0) builder.deleteCharAt(builder.length()-1);
		builder.append("]");
		builder.append(",\n");
		builder.append("\"levels\":[");
		for (OrganisationUnitLevel level : levels) {
			builder.append("{");
			builder.append("\"level\":"+level.getLevel());
			builder.append(",");
			builder.append("\"name\":\""+level.getName()+"\"");
			builder.append("}");
			builder.append(",");
		}
		if (levels.size() != 0) builder.deleteCharAt(builder.length()-1);
		builder.append("]");
		builder.append(",");
		builder.append("\"selectedOrganisation\":"+organisation.getId());
		builder.append(",");
		if (organisation.getOrganisationUnit().getCoordinates() != null) {
			builder.append("\"selectedCoordinates\":"+organisation.getOrganisationUnit().getCoordinates());
			builder.append(",");
		}
		builder.append("\"selectedLevel\":"+level);
		builder.append(",");
		builder.append("\"selectedPeriod\":"+period.getId());
		if (target != null) {
			builder.append(",");
			builder.append("\"selectedTarget\":"+target.getId());
		}
		builder.append("}");
		return builder.toString();
	}

	@Override
	public String toString() {
		return "Map [polygons=" + polygons + "]";
	}
	
}
