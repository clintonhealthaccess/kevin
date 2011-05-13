package org.chai.kevin.maps;

import java.util.List;

public class Map {

	private List<Polygon> polygons;
	
	public Map(List<Polygon> polygons) {
		this.polygons = polygons;
	}
	
	public List<Polygon> getPolygons() {
		return polygons;
	}
	
	public String toJson() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\"polygons\":[");
		for (Polygon polygon : polygons) {
			builder.append(polygon.toJson());
			builder.append(",");
		}
		if (polygons.size() != 0) builder.deleteCharAt(builder.length()-1);
		builder.append("]}");
		return builder.toString();
	}

	@Override
	public String toString() {
		return "Map [polygons=" + polygons + "]";
	}
	
}
