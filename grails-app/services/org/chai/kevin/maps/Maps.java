package org.chai.kevin.maps;

import java.util.List;

import org.chai.kevin.Organisation;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
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
