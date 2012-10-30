/**
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
package org.chai.kevin.exports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Period;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.util.ImportExportConstant;
import org.chai.kevin.value.DataValue;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

/**
 * @author Jean Kahigiso M.
 */
public class DataElementExportService extends ExportService {
	
	private static final Log log = LogFactory.getLog(DataElementExportService.class);
	
	@Override
	public File exportData(DataExport export, String language) throws IOException{
		if (log.isDebugEnabled()) log.debug("exportData("+export+")");
		Set<DataLocationType> types = new HashSet<DataLocationType>();
		
		for(String code: export.getTypeCodes()){
			DataLocationType type = locationService.findDataLocationTypeByCode(code);
			if(type!=null) types.add(type);
		}
		
		List<DataLocation> dataLocations = locationService.getDataLocationsOfType(new HashSet(export.getAllLocations()),types);
		
		if (log.isDebugEnabled()) log.debug(" Exporter dataLocations "+dataLocations+")");
		return this.exportDataElements(languageService.getText(export.getDescriptions(), language), dataLocations,export.getAllPeriods(),((DataElementExport) export).getAllDataElements(), language);
	}
		
	public File exportDataElements(String fileName,List<DataLocation> dataLocations,List<Period> periods,List<DataElement> dataElements, String language) throws IOException{
		if (log.isDebugEnabled()) log.debug(" exportDataElement(String "+fileName+" List<DataLocation>: " + dataLocations + " List<Period>: "+ periods + " Set<DataElement<DataValue>>: " + dataElements + ")");
		File csvFile = File.createTempFile(fileName, ImportExportConstant.CSV_FILE_EXTENSION);
		FileWriter csvFileWriter = new FileWriter(csvFile);
		ICsvListWriter writer = new CsvListWriter(csvFileWriter, CsvPreference.EXCEL_PREFERENCE);
		this.writeDataElements(writer, dataLocations, periods, dataElements, language);
		return csvFile;

	}

	private void writeDataElements(ICsvListWriter writer, List<DataLocation> dataLocations, List<Period> periods,List<DataElement> dataElements,String language) throws IOException {
		try{
			String[] csvHeaders = null;
			// headers
			if(csvHeaders == null){
				csvHeaders = this.getExportDataHeaders(language).toArray(new String[getExportDataHeaders(language).size()]);
				writer.writeHeader(csvHeaders);
			}
			for(DataLocation location: dataLocations)
				for(Period period: periods)
					for(DataElement<DataValue> dataElement: dataElements){
						List<List<String>> lines=this.getExportLineForValue(location,period,dataElement,language);
						for(List<String> line: lines)
							writer.write(line);
					}
		} catch (IOException ioe){
			// TODO throw something that make sense
			throw ioe;
		} finally {
			writer.close();
		}
	}
	
	public List<List<String>> getExportLineForValue(DataLocation location,Period period, DataElement<DataValue> dataElement, String language){
		DataPointVisitor dataPointVisitor = new DataPointVisitor();
		if(dataElement!=null){
			DataValue dataValue = valueService.getDataElementValue(dataElement, location, period);
			if(dataValue!=null){
				List<String> basicInfo = this.getBasicInfo(location,period,dataElement,language);
				dataPointVisitor.setBasicInfo(basicInfo);
				dataElement.getType().visit(dataValue.getValue(), dataPointVisitor);
			}
			
			sessionFactory.getCurrentSession().evict(dataValue);
		}
		return dataPointVisitor.getLines();
	}
	
	public List<String> getBasicInfo(DataLocation location,Period period, DataElement<DataValue> dataElement, String language){
		List<String> basicInfo = new ArrayList<String>();
		for (LocationLevel level : locationService.listLevels()){
			Location parent = location.getParentOfLevel(level);
			if (parent != null) basicInfo.add(languageService.getText(parent.getNames(), language));
			else basicInfo.add("");
		}
		basicInfo.add(location.getCode());
		basicInfo.add(languageService.getText(location.getNames(), language));
		basicInfo.add(languageService.getText(location.getType().getNames(), language));
		basicInfo.add(period.getCode()+"");
		basicInfo.add("[ "+period.getStartDate().toString()+" - "+period.getEndDate().toString()+" ]");
		basicInfo.add(dataElement.getClass().getSimpleName());
		basicInfo.add(dataElement.getCode()+"");
		basicInfo.add(languageService.getText(dataElement.getNames(), language));
		return basicInfo;
	}
	
	@Override
	public List<String> getExportDataHeaders(String language) {
		List<String> headers = new ArrayList<String>();
		for (LocationLevel level : locationService.listLevels())
			headers.add(languageService.getText(level.getNames(), language));
		headers.add(ImportExportConstant.DATA_LOCATION_CODE);
		headers.add(ImportExportConstant.DATA_LOCATION_NAME);
		headers.add(ImportExportConstant.LOCATION_TYPE);
		headers.add(ImportExportConstant.PERIOD_CODE);
		headers.add(ImportExportConstant.PERIOD);
		headers.add(ImportExportConstant.DATA_CLASS);
		headers.add(ImportExportConstant.DATA_CODE);
		headers.add(ImportExportConstant.DATA_NAME);
		headers.add(ImportExportConstant.DATA_VALUE);
		headers.add(ImportExportConstant.DATA_VALUE_ADDRESS);
		return headers;
	}
}
