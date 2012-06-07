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
package org.chai.kevin.imports;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.LocationService;
import org.chai.kevin.Period;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

/**
 * @author Jean Kahigiso M.
 *
 */
public class GeneralDataImporter extends DataImporter{
	
	private LocationService locationService;
	private DataService dataService;
	
	private ImporterErrorManager manager;
	private Period period;
	
	public GeneralDataImporter(LocationService locationService,
			ValueService valueService, DataService dataService,
			ImporterErrorManager manager, Period period) {
		super(valueService);
		this.locationService = locationService;
		this.dataService = dataService;
		this.manager = manager;
		this.period = period;
	}

	@Override
	public void importData(String fileName, ICsvMapReader csvMapReader) throws IOException {
		if (log.isDebugEnabled()) log.debug("importData(FileName:"+fileName+"Reader:" + csvMapReader + ")");
		manager.setCurrentFileName(fileName);

		try {
			final String[] headers = csvMapReader.getCSVHeader(true);
			Map<String, String> rows = csvMapReader.read(headers);
			Map<DataLocation, Set<RawDataElement>> savedData = new HashMap<DataLocation, Set<RawDataElement>>();
			
			if(!Arrays.asList(headers).contains(CODE_HEADER) || !Arrays.asList(headers).contains(DATA_ELEMENT_HEADER) || !Arrays.asList(headers).contains(VALUE_HEADER))
				manager.getErrors().add(new ImporterError(fileName,csvMapReader.getLineNumber(), Arrays.asList(headers).toString(),"import.error.message.unknowm.header"));
			else{
				
				while (rows != null) {
					if (log.isInfoEnabled()) log.info("starting import of line with code: " + rows.get(CODE_HEADER)+ " raw_data_element: "+ rows.get(DATA_ELEMENT_HEADER) + " data_value "+ rows.get(VALUE_HEADER));
					
					Value value = null;
					Set<String> attributes = new HashSet<String>();
					Map<String, Object> map = new HashMap<String, Object>();
					Map<String, Type> types = new HashMap<String, Type>();
					
					DataLocation dataEntity = locationService.findCalculationLocationByCode(rows.get(CODE_HEADER), DataLocation.class);
					RawDataElement rawDataElement = dataService.getDataByCode(rows.get(DATA_ELEMENT_HEADER), RawDataElement.class);
					RawDataElementValue rawDataElementValue = null;
					
					if (dataEntity != null && rawDataElement != null) {
						if (log.isDebugEnabled()) log.debug("checking if dataElement: "+rawDataElement+"and location: "+dataEntity+"is in savedData : "+savedData+" errors: "+manager.getErrors());
						//Check if data is going to be override and save the error message
						if (savedData.get(dataEntity)==null || !savedData.get(dataEntity).contains(rawDataElement)) {
							// not imported yet
							if (!savedData.containsKey(dataEntity)) {
								savedData.put(dataEntity, new HashSet<RawDataElement>());
							}
							savedData.get(dataEntity).add(rawDataElement);
							if (log.isDebugEnabled()) log.debug("current savedData : "+savedData+" errors: "+manager.getErrors());
						} else {
							// already imported
							manager.getErrors().add(new ImporterError(fileName,csvMapReader.getLineNumber(),Arrays.asList(headers).toString(),"import.error.message.data.duplicated"));
						}
						
						
						types.put("", rawDataElement.getType());
						ImportSanitizer sanitizer = new ImportSanitizer(fileName,manager.getErrors(), types, dataService);
						sanitizer.setLineNumber(csvMapReader.getLineNumber());
						sanitizer.setNumberOfErrorInRows(0);
						
						rawDataElementValue = valueService.getDataElementValue(rawDataElement, dataEntity, period);
	                    //Check raw data element has a value associate to it otherwise create one
						if (rawDataElementValue != null) value = rawDataElementValue.getValue();
						else {
							value = new Value("");
							rawDataElementValue = new RawDataElementValue(rawDataElement, dataEntity, period, value);
						}
						map.put("", rows.get(VALUE_HEADER));
						
						// TODO refactor this into one method (same as NormalizedDataImporter)
						if (log.isDebugEnabled()) log.debug("merging with data from map of header and data "+ map);
						if (log.isTraceEnabled()) log.trace("value before merge" + value);
						value = rawDataElement.getType().mergeValueFromMap(value,map, "", attributes, sanitizer);
						if (log.isTraceEnabled()) log.trace("value after merge " + value);
	
						rawDataElementValue.setValue(value);
						valueService.save(rawDataElementValue);
						
						if (log.isTraceEnabled()) log.trace("saved rawDataElement: "+ rawDataElementValue.getValue());
	
						if (sanitizer.getNumberOfErrorInRows() > 0)
							manager.incrementNumberOfRowsSavedWithError(1);
						manager.incrementNumberOfSavedRows();
					} else {
						if(dataEntity==null) manager.getErrors().add(new ImporterError(fileName,csvMapReader.getLineNumber(),CODE_HEADER,"import.error.message.unknown.data.location"));
						if(rawDataElement==null) manager.getErrors().add(new ImporterError(fileName,csvMapReader.getLineNumber(),DATA_ELEMENT_HEADER,"import.error.message.unknown.raw.data.element"));	
						manager.incrementNumberOfUnsavedRows();
					}
					
					if (log.isInfoEnabled()) log.info("finished importing line");
					rows = csvMapReader.read(headers);
				}
			}

		} catch (IOException ioe) {
			// TODO Please through something meaningful
			throw ioe;
		} 
		
	}
	

}
