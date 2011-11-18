package org.chai.kevin.survey.export;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ift.CellProcessor;

public class SurveyExportDataPoint extends ArrayList<SurveyExportDataItem> {
	
	private List<? super Object> exportDataPoint;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4042708987342771437L;
	
	public SurveyExportDataPoint(){
		super();
		exportDataPoint = new ArrayList<Object>();
	}
	
	public SurveyExportDataPoint(List<SurveyExportDataItem> dataItems) {
		super();
		exportDataPoint = new ArrayList<Object>();
		for(SurveyExportDataItem dataItem : dataItems){
			add(dataItem);
		}
	}

	@Override
    public boolean add(SurveyExportDataItem surveyExportDataItem) {
		if(surveyExportDataItem.size() > 1){
			//TODO throw exception
			return false;
		}
		super.add(surveyExportDataItem);		
		Collection<Object> exportDataItems = surveyExportDataItem.values();
		for(Object exportDataItem : exportDataItems){
			exportDataPoint.add(exportDataItem);
		}
		return true;
    }
	
	public List<? super Object> getExportDataPoint() {		
		return exportDataPoint;
	}
	
	public CellProcessor[] getExportCellProcessors() {			
		List<ConvertNullTo> cellProcessor = new ArrayList<ConvertNullTo>();				
		for(SurveyExportDataItem surveyExportDataItem : this){
			cellProcessor.add(new ConvertNullTo("null"));
		}
		return cellProcessor.toArray(new CellProcessor[this.size()]);
	}
}