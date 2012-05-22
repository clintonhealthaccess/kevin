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
package org.chai.kevin.importer;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class NormalizedDataImporter extends Importer{
	
	private static final Log log = LogFactory.getLog(NormalizedDataImporter.class);
	private LocationService locationService;
	private ValueService valueService;	
	private DataService dataService;
	private ImporterErrorManager manager;
	private RawDataElement rawDataElement;
	private Period period;
	
	public NormalizedDataImporter(LocationService locationService,
			ValueService valueService, DataService dataService,
			ImporterErrorManager manager, RawDataElement rawDataElement,
			Period period) {
		super();
		this.locationService = locationService;
		this.valueService = valueService;
		this.dataService = dataService;
		this.manager = manager;
		this.rawDataElement = rawDataElement;
		this.period = period;

	}

	@Override
	public void importData(Reader reader) throws IOException {
		
		if (log.isDebugEnabled()) log.debug("importGeneralData(Reader:" + reader + ")");
		ICsvMapReader readFileAsMap = new CsvMapReader(reader, CsvPreference.EXCEL_PREFERENCE);
		
		try{			
			final String[] headers = readFileAsMap.getCSVHeader(true);
			String code=null;
			Map<String,Integer> positions = new HashMap<String,Integer>();
			Map<String,String> values = readFileAsMap.read(headers);
			Map<String,Type> types = new HashMap<String,Type>();
		
			for (String header : headers)  { 
				try {
					if(!header.equals(CODE_HEADER))
						types.put("[_]."+header,rawDataElement.getType().getType("[_]."+header));
				} catch(IllegalArgumentException e){
					if(log.isWarnEnabled()) log.warn("Column type not found for header"+header, e);
					manager.getErrors().add(new ImporterError(readFileAsMap.getLineNumber(),header,"import.error.message.unknowm.column.type"));
				}
			}
			Value value = null;
			DataLocation dataLocation=null;
			RawDataElementValue rawDataElementValue= null;			
			manager.setNumberOfSavedRows(0);
			manager.setNumberOfUnsavedRows(0);
			manager.setNumberOfRowsSavedWithError(0);

			ImportSanitizer sanitizer = new ImportSanitizer(manager.getErrors(), types, dataService);
			
			while (values != null) {
				Map <String,Object> map = new HashMap<String,Object>();
				Set<String> attributes = new HashSet<String>();
				sanitizer.setLineNumber(readFileAsMap.getLineNumber());
				sanitizer.setNumberOfErrorInRows(0);
				
				if(log.isWarnEnabled()) log.warn("Current facility code: "+values.get(CODE_HEADER));
				
				if(values.get(CODE_HEADER)!=null && !values.get(CODE_HEADER).equals(code)){
					// The location changes, we need to update the code, location, position, rawDataElementValue
					// 1 update the code
					code = values.get(CODE_HEADER);
					// 2 update the position	
					if (positions.get(code) == null) positions.put(code, 0);
					// 3 update the location
					dataLocation = locationService.findCalculationLocationByCode(code, DataLocation.class);
					if(dataLocation==null){
						manager.getErrors().add(new ImporterError(readFileAsMap.getLineNumber(),CODE_HEADER,"import.error.message.unknown.location"));
					}else{
						// 4 update the rawDataElementValue
						rawDataElementValue = valueService.getDataElementValue(rawDataElement, dataLocation, period);
						if(rawDataElementValue != null) value = rawDataElementValue.getValue();
						else{
							value = new Value("");		
							rawDataElementValue= new RawDataElementValue(rawDataElement,dataLocation,period,value);
						}
					}
				}
				
				for (String header : headers){
					if (!header.equals(CODE_HEADER)){
						map.put("[" + positions.get(code) + "]."+ header, values.get(header));
					}		
				}
				
				if(positions.get(code)!=null){
					map.put("", getLineNumberString(positions.get(code)));
					positions.put(code, positions.get(code) + 1);	
				}
				
				
				if (dataLocation == null)
					manager.incrementNumberOfUnsavedRows();
				else {
					
					if(log.isDebugEnabled()) log.debug("Marging with data from map of header and data "+map+" Value before marge"+value);
					value = rawDataElement.getType().mergeValueFromMap(value, map, "",attributes, sanitizer);
					if(log.isDebugEnabled()) log.debug("Value after marge "+value);	
					rawDataElementValue.setValue(value);
					valueService.save(rawDataElementValue);
					if(log.isDebugEnabled()) log.debug("The saved rawDataElementValue: "+rawDataElementValue.getValue());
					if(sanitizer.getNumberOfErrorInRows()>0)
						manager.incrementNumberOfRowsSavedWithError(1);
					manager.incrementNumberOfSavedRows();
				}
				values = readFileAsMap.read(headers);				
			}
					
		}catch(IOException ioe){
			// TODO Please through something meaningful
			throw ioe;
		}finally {
			readFileAsMap.close();
		}			
	}

}
