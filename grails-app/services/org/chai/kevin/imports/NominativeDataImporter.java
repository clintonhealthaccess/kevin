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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.location.LocationService;
import org.chai.kevin.Period;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.location.DataLocation;
import org.chai.kevin.util.ImportExportConstant;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.hibernate.SessionFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.supercsv.io.ICsvMapReader;

/**
 * @author Jean Kahigiso M.
 *
 */
public class NominativeDataImporter extends DataImporter {
	
	private static final Log log = LogFactory.getLog(NominativeDataImporter.class);
	
	private LocationService locationService;
	private SessionFactory sessionFactory;
	
	private ImporterErrorManager manager;
	private RawDataElement rawDataElement;
	private Period period;
	

	public NominativeDataImporter(LocationService locationService,
			ValueService valueService, DataService dataService, 
			SessionFactory sessionFactory, PlatformTransactionManager transactionManager,
			ImporterErrorManager manager, RawDataElement rawDataElement,
			Period period) {
		super(valueService, dataService, transactionManager);
		this.locationService = locationService;
		this.sessionFactory = sessionFactory;
		this.manager = manager;
		this.rawDataElement = rawDataElement;
		this.period = period;
	}

	
	/**
	 * Imports numberLinesToImport lines and then returns. Returns true when file has been read entirely.
	 * 
	 * @param fileName
	 * @param readFileAsMap
	 * @param numberOfLinesToImport
	 * @param sanitizer
	 * @param headers
	 * @param positions
	 * @throws IOException
	 */
	private boolean importData(String fileName,ICsvMapReader readFileAsMap,Integer numberOfLinesToImport, ImportSanitizer sanitizer, String[] headers, Map<String,Integer> positions) throws IOException {

		// keep location in memory between rows to optimize
		String code = null;
		DataLocation dataLocation = null;
		
		// keep value in memory between rows to optimize
		RawDataElementValue rawDataElementValue = null;	
		Map<String,Object> positionsValueMap = new HashMap<String, Object>();
		
		Map<String,String> values = readFileAsMap.read(headers);
		
		int importedLines = 0;
		while (values != null && importedLines < numberOfLinesToImport) {
			if (log.isInfoEnabled()) log.info("starting import of line with values: "+values);
			sanitizer.setNumberOfErrorInRows(0);
			
			if(log.isDebugEnabled()) log.debug("current facility code: "+values.get(ImportExportConstant.DATA_LOCATION_CODE));
			
			if (values.get(ImportExportConstant.DATA_LOCATION_CODE)!=null && !values.get(ImportExportConstant.DATA_LOCATION_CODE).equals(code)) {
				// either we are reading the first line and there is no current location
				// or we change location
				
				// first we save the data of the preceding lines
				if (rawDataElementValue != null) {
					// we merge and save the current data
					saveAndMergeIfNotNull(rawDataElement, rawDataElementValue, positionsValueMap, sanitizer);
					
					// clear the value map since we are reading a line for a new location
					positionsValueMap.clear();

				}
				
				// second we get the new location
				// 1 update the current code
				code = values.get(ImportExportConstant.DATA_LOCATION_CODE);
				// 2 update and save the position	
				if (positions.get(code) == null) positions.put(code, 0);
				// 3 update the location
				dataLocation = locationService.findCalculationLocationByCode(code, DataLocation.class);
				// if the location is not found, we add an error
				Integer lineNumber=  readFileAsMap.getLineNumber();
				if (dataLocation == null) {
					manager.getErrors().add(new ImporterError(fileName,lineNumber,ImportExportConstant.DATA_LOCATION_CODE,"import.error.message.unknown.location"));
				} 
				else {
					// get the value associated to the new location
					rawDataElementValue = valueService.getDataElementValue(rawDataElement, dataLocation, period);
					if(rawDataElementValue == null) {
						rawDataElementValue = new RawDataElementValue(rawDataElement, dataLocation, period, new Value(""));
					}
				}
			}
			
			if (dataLocation == null) {
				manager.incrementNumberOfUnsavedRows();
			}
			else {
				// read values from line and put into valueMap
				for (String header : headers){
					if (!header.equals(ImportExportConstant.DATA_LOCATION_CODE)){
						sanitizer.addLineNumberMap(readFileAsMap.getLineNumber(), "[" + positions.get(code) + "]."+ header);
						positionsValueMap.put("[" + positions.get(code) + "]."+ header, values.get(header));
					}		
				}
				// increment number of lines read for this location
				positions.put(code, positions.get(code) + 1);
			
				if (sanitizer.getNumberOfErrorInRows()>0) manager.incrementNumberOfRowsSavedWithError(1);
				manager.incrementNumberOfSavedRows();
			}
			if (log.isInfoEnabled()) log.info("finished importing line");
			
			// we increment the number of imported lines to stop the while loop when it reaches numberOfLinesToImport
			importedLines++;
			
			// read new line
			if (importedLines < numberOfLinesToImport) values = readFileAsMap.read(headers);
		}
		
		saveAndMergeIfNotNull(rawDataElement, rawDataElementValue, positionsValueMap, sanitizer);
		return values == null;
	}
	
	/* (non-Javadoc)
	 * @see org.chai.kevin.importer.Importer#importData(java.lang.String, java.io.Reader)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void importData(final String fileName, final ICsvMapReader csvMapReader) throws IOException {
		if (log.isDebugEnabled()) log.debug("importData(FileName:"+fileName+"Reader:" + csvMapReader + ")");
		
		manager.setCurrentFileName(fileName);
		final String[] headers = csvMapReader.getCSVHeader(true);
		
		Map<String,Type> types = new HashMap<String,Type>();
		
		for (String header : headers)  { 
			try {
				if(!header.equals(ImportExportConstant.DATA_LOCATION_CODE))
					types.put("[_]."+header,rawDataElement.getType().getType("[_]."+header));
			} catch(IllegalArgumentException e){
				if(log.isWarnEnabled()) log.warn("Column type not found for header"+header, e);
				manager.getErrors().add(new ImporterError(fileName,1,header,"import.error.message.unknowm.column.type"));
			}
		}
				
		final ImportSanitizer sanitizer = new ImportSanitizer(fileName,manager.getErrors(), types, dataService);
		final Map<String,Integer> positions = new HashMap<String,Integer>();
		
		//check for duplicate column in the file
		Set<String> removeDuplicate = new HashSet<String>(new ArrayList<String>(Arrays.asList(headers)));
		if(log.isWarnEnabled()) log.warn(" removeDuplicate "+removeDuplicate.size()+" headers length "+headers.length);
		
		boolean readEntirely = false;
		if(!Arrays.asList(headers).contains(ImportExportConstant.DATA_LOCATION_CODE) || !(removeDuplicate.size() == headers.length)){
			if(removeDuplicate.size() < headers.length)
				manager.getErrors().add(new ImporterError(fileName,1,Arrays.asList(headers).toString(),"import.error.message.duplicate.column"));
			if(!Arrays.asList(headers).contains(ImportExportConstant.DATA_LOCATION_CODE))
				manager.getErrors().add(new ImporterError(fileName,1, Arrays.asList(headers).toString(),"import.error.message.unknowm.header"));
		}else{
			while (!readEntirely) {
				readEntirely = (Boolean)getTransactionTemplate().execute(new TransactionCallback() {
					@Override
					public Object doInTransaction(TransactionStatus arg0) {
						sessionFactory.getCurrentSession().refresh(rawDataElement);
						sessionFactory.getCurrentSession().refresh(period);
						
						try {
							return importData(fileName,csvMapReader,ImportExportConstant.NUMBER_OF_LINES_TO_IMPORT, sanitizer,  headers, positions);
						} catch (IOException e) {
							return true;
						}
					}
				});
				sessionFactory.getCurrentSession().clear();
			}
		}
	}

}
