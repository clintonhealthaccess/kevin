package org.chai.kevin.maps;

import org.chai.kevin.Gradient;
import org.chai.kevin.Organisation;

public class Polygon extends Gradient {

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
	
	public String toJson() {
		return 
			"{" +
				"\"organisation\":"+organisation.toJson()+",\n"+
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
