package org.chai.kevin.entity.export;

import java.util.ArrayList;

public class EntityExportDataPoint extends ArrayList<String> {
	
	private static final long serialVersionUID = -8812306436104509210L;

	public EntityExportDataPoint(){
		super();
	}

	public EntityExportDataPoint(EntityExportDataPoint exportDataPoint) {
		super(exportDataPoint);
	}

}