package org.chai.kevin.maps;

import java.util.List;

import org.chai.kevin.Organisation;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;

public class Maps {

	private List<Polygon> polygons;
	private Organisation selectedOrganisation;
	private List<OrganisationUnitLevel> levels;
	private OrganisationUnitLevel selectedLevel;
	
	public Maps(List<Polygon> polygons, Organisation selectedOrganisation, List<OrganisationUnitLevel> levels, OrganisationUnitLevel selectedLevel) {
		this.polygons = polygons;
		this.selectedOrganisation = selectedOrganisation;
		this.levels = levels;
		this.selectedLevel = selectedLevel;
	}
	
	public List<Polygon> getPolygons() {
		return polygons;
	}
	
	public Organisation getSelectedOrganisation() {
		return selectedOrganisation;
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
		builder.append("\"selectedOrganisation\":"+selectedOrganisation.toJson());
		builder.append(",");
		builder.append("\"selectedLevel\":"+selectedLevel.getLevel());
		builder.append("}");
		return builder.toString();
	}

	@Override
	public String toString() {
		return "Map [polygons=" + polygons + "]";
	}
	
}
