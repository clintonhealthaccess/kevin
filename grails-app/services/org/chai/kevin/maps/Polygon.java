package org.chai.kevin.maps;

import org.chai.kevin.Organisation;

public class Polygon {

	private Organisation organisation;
	private Double value;
	
	public Polygon(Organisation organisation, Double value) {
		this.organisation = organisation;
		this.value = value;
	}
	
	public Organisation getOrganisation() {
		return organisation;
	}
	
	public Double getValue() {
		return value;
	}
	
	public String getColor() {
		return "#444444";
	}
	
	public String toJson() {
		return 
			"{" +
				"\"name\":\""+organisation.getOrganisationUnit().getName()+"\","+
				"\"coordinates\":"+organisation.getOrganisationUnit().getCoordinates()+","+
				"\"value\":"+value+","+
				"\"color\":\""+getColor()+"\""+
			"}";
	}

	@Override
	public String toString() {
		return "Polygon [organisation=" + organisation + ", value=" + value
				+ "]";
	}
	
}
