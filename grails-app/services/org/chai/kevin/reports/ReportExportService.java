package org.chai.kevin.reports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LanguageService;
import org.chai.kevin.LocationService;
import org.chai.kevin.Period;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.value.Value;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class ReportExportService {
	
	private static final Log log = LogFactory.getLog(ReportExportService.class);
	
	private LanguageService languageService;
	private LocationService locationService;
	private Set<String> skipLevels;
	
	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	public void setSkipLevels(Set<String> skipLevels) {
		this.skipLevels = skipLevels;
	}
	
	public List<LocationLevel> getLevels() {
		List<LocationLevel> result = locationService.listLevels();
		for (String level : skipLevels) {
			result.remove(locationService.findLocationLevelByCode(level));
		}
		return result;
	}
	
	private final static String CSV_FILE_EXTENSION = ".csv";

	private final static String LOCATION_HEADER = "Location";
	
	private String[] getExportDataHeaders(List<ReportTableIndicator> indicators) {
		List<String> headers = new ArrayList<String>();		
		headers.add(LOCATION_HEADER);
		for(ReportTableIndicator indicator : indicators){
			headers.add(languageService.getText(indicator.getNames()));
		}
		return headers.toArray(new String[0]);
	}	
	
	public String getExportFilename(CalculationLocation location, ReportProgram program, Period period){
		String exportFilename = languageService.getText(program.getNames()).replaceAll("[^a-zA-Z0-9]", "") + "_" + 
				languageService.getText(location.getNames()).replaceAll("[^a-zA-Z0-9]", "") + "_" + 
				period.getCode();
		return exportFilename;
	}
	
	@Transactional(readOnly=true)
	public File getReportExportFile(String filename, ReportTable reportTable) throws IOException { 
				
		List<CalculationLocation> locations = reportTable.getLocations();
		//TODO Collections.sort(locations, LocationSorter.BY_NAME(languageService.getCurrentLanguage()));
		
		File csvFile = File.createTempFile(filename, CSV_FILE_EXTENSION);
		
		FileWriter csvFileWriter = new FileWriter(csvFile);
		ICsvListWriter writer = new CsvListWriter(csvFileWriter, CsvPreference.EXCEL_PREFERENCE);
		try {
			String[] csvHeaders = null;
			
			List<ReportTableIndicator> indicators = reportTable.getIndicators();
			
			// headers
			if(csvHeaders == null){
				csvHeaders = getExportDataHeaders(indicators);
				writer.writeHeader(csvHeaders);
			}
			
			for(CalculationLocation location : locations){	
				if (log.isDebugEnabled()) log.debug("getReportExportFile(location="+location+")");
				
				ArrayList<String> reportExportRow = new ArrayList<String>();
				
				//Location
				List<String> rowLocations = new ArrayList<String>();
				for (LocationLevel level : getLevels()){			
					Location parent = locationService.getParentOfLevel(location, level);
					if (parent != null){
						rowLocations.add(languageService.getText(parent.getNames()));
					}
				}
				rowLocations.add(languageService.getText(location.getNames()));
				String locationNames = StringUtils.join(rowLocations, "-");
				reportExportRow.add(locationNames);
				
				//Indicators
				for(ReportTableIndicator indicator: indicators){
					 Value value = reportTable.getValue(location, indicator);
					 if(value != null && !value.isNull())
						 reportExportRow.add(languageService.getStringValue(value, indicator.getType()));
					 else{
						 //TODO
						 reportExportRow.add("N/A");
					 }
				}
				
				writer.write(reportExportRow);
													
			}
		} catch (IOException ioe){
			// TODO is this good ?
			throw ioe;
		} finally {
			writer.close();
		}
		
		return csvFile;
	}
}
