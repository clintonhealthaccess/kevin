package org.chai.kevin.entity.export;

import java.util.List;

public class EntityExportData {
	
	private List<EntityExportDataPoint> dataPoints;

	public EntityExportData(List<EntityExportDataPoint> dataPoints){
		this.dataPoints = dataPoints;
	}
	
	public List<EntityExportDataPoint> getDataPoints() {
		return dataPoints;
	}

	public void setDataPoints(List<EntityExportDataPoint> dataPoints) {
		this.dataPoints = dataPoints;
	}
}
