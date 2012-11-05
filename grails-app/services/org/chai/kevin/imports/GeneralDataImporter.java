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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.location.LocationService;
import org.chai.kevin.Period;
import org.chai.kevin.PeriodService;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.location.DataLocation;
import org.chai.kevin.util.ImportExportConstant;
import org.chai.kevin.value.DataValue;
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
public class GeneralDataImporter extends DataImporter{
	
	private static final Log log = LogFactory.getLog(GeneralDataImporter.class);
	private LocationService locationService;
	private SessionFactory sessionFactory;
	private ImporterErrorManager manager;
	private PeriodService periodService;
	

	public GeneralDataImporter(LocationService locationService,
			ValueService valueService, DataService dataService,	
			SessionFactory sessionFactory,
			PlatformTransactionManager transactionManager,
			ImporterErrorManager manager,
			PeriodService periodService) {
		super(valueService,dataService,transactionManager);
		this.locationService = locationService;
		this.sessionFactory = sessionFactory;
		this.manager = manager;
		this.periodService=periodService;
	}
	
	private boolean importData(String fileName,ICsvMapReader reader,Integer numberOfLinesToImport, ImportSanitizer sanitizer, String[] headers, Set<DuplicateHelper> duplicateHelpers, Map<String,Integer> positions) throws IOException{
		// keep data location in memory between rows to optimize
		DataLocation dataLocation = null;
		String dataLocationCode = null;
		
		// keep data element in memory between rows to optimize
		RawDataElement dataElement = null;
		String dataElementCode = null;
		
		// keep period in memory between rows to optimize
		Period period = null;
		String periodCode = null;
		
		// keep value in memory between rows to optimize
		Map<String, Object> positionsValueMap = new HashMap<String, Object>();
		RawDataElementValue rawDataElementValue = null;
		
		Map<String, String> rowValues = reader.read(headers);
		
		int importedLines = 0;
		while (rowValues != null && importedLines < numberOfLinesToImport) {
			if (log.isInfoEnabled()) log.info("starting import of line with values: "+rowValues);
			
				
			sanitizer.addLineNumberMap(reader.getLineNumber(), rowValues.get(ImportExportConstant.DATA_VALUE_ADDRESS));
			Integer lineNumber=  reader.getLineNumber();
			sanitizer.setNumberOfErrorInRows(0);
			
			String newDataLocationCode = rowValues.get(ImportExportConstant.DATA_LOCATION_CODE);
			String newDataElementCode = rowValues.get(ImportExportConstant.DATA_CODE);
			String newPeriodCode = rowValues.get(ImportExportConstant.PERIOD_CODE);
			
			if (!(newDataLocationCode.equals(dataLocationCode) && newDataElementCode.equals(dataElementCode) && newPeriodCode.equals(periodCode))) {
				// we are on a row with a new value
				
				// first we save the current value
				if (rawDataElementValue != null) {
					// we merge and save the current data
					saveAndMergeIfNotNull(dataElement, rawDataElementValue, positionsValueMap, sanitizer);
					
					// clear the value map since we are reading a line for a new location
					positionsValueMap.clear();
					sanitizer.clearType();
				}
				
				dataLocationCode = newDataLocationCode;
				dataLocation = locationService.findCalculationLocationByCode(dataLocationCode, DataLocation.class);
				dataElementCode = newDataElementCode;
				
				dataElement = dataService.getDataByCode(dataElementCode, RawDataElement.class);
				if (dataElement == null) {
					manager.getErrors().add(new ImporterError(fileName,lineNumber,ImportExportConstant.DATA_CODE,"import.error.message.not.raw.data.element"));	
				}	
				periodCode = newPeriodCode;
				period = periodService.getPeriodByCode(periodCode);
				
				rawDataElementValue = null;
				
				// we get the new value
				if (dataLocation != null && dataElement != null && period != null) {
					// get the value associated to the new location
					rawDataElementValue = valueService.getDataElementValue(dataElement, dataLocation, period);
					if(rawDataElementValue == null) {
						rawDataElementValue = new RawDataElementValue(dataElement, dataLocation, period, new Value(""));
					}
				}
			}
			
			if (log.isDebugEnabled()) log.debug("Current line data: dataLocation "+dataLocation+" dataElement "+dataElement+" period "+period+" lineNumber "+reader.getLineNumber());	
			
			if (dataElement != null && dataLocation != null && period != null) {
				DuplicateHelper duplicateHelper = new DuplicateHelper(dataLocation, period, (Data)dataElement, rowValues.get(ImportExportConstant.DATA_VALUE_ADDRESS));
				if (log.isDebugEnabled()) log.debug("Check if data is going to be overrided. duplicateHelper: "+duplicateHelper+" errors: "+manager.getErrors());
				//Check if data is going to be override and save the error message
				if (!duplicateHelpers.contains(duplicateHelper)) {
					// not imported yet
					duplicateHelpers.add(duplicateHelper);
				} else {
					// already imported
					manager.getErrors().add(new ImporterError(fileName,lineNumber,Arrays.asList(headers).toString(),"import.error.message.data.duplicated"));
				}
				
				sanitizer.addType(rowValues.get(ImportExportConstant.DATA_VALUE_ADDRESS), dataElement.getType().getType(rowValues.get(ImportExportConstant.DATA_VALUE_ADDRESS)));
				positionsValueMap.put(rowValues.get(ImportExportConstant.DATA_VALUE_ADDRESS), rowValues.get(ImportExportConstant.DATA_VALUE));
				
				if (sanitizer.getNumberOfErrorInRows() > 0)
					manager.incrementNumberOfRowsSavedWithError(1);
				manager.incrementNumberOfSavedRows();

			} else {
					if(dataLocation==null) manager.getErrors().add(new ImporterError(fileName,lineNumber,ImportExportConstant.DATA_LOCATION_CODE,"import.error.message.unknown.data.location"));
					if(period==null) manager.getErrors().add(new ImporterError(fileName,lineNumber,ImportExportConstant.PERIOD_CODE,"import.error.message.unknown.period"));	
					manager.incrementNumberOfUnsavedRows();
			}
				
			if (log.isInfoEnabled()) log.info("finished importing line");
			
			// we increment the number of imported lines to stop the while loop when it reaches numberOfLinesToImport
			importedLines++;
						
			// read new line
			if (importedLines < numberOfLinesToImport) rowValues = reader.read(headers);
		}
		
		saveAndMergeIfNotNull(dataElement, rawDataElementValue, positionsValueMap, sanitizer);
		sanitizer.clearType();
		return rowValues == null;
	}

	
	@SuppressWarnings({"rawtypes", "unchecked" })
	@Override
	public void importData(final String fileName, final ICsvMapReader csvMapReader) throws IOException {
		if (log.isInfoEnabled()) log.info("importData( fileName "+fileName+" reader "+csvMapReader+")");
		manager.setCurrentFileName(fileName);
		final String[] headers = csvMapReader.getCSVHeader(true);
		Map<String,Type> types = new HashMap<String,Type>();

		final ImportSanitizer sanitizer = new ImportSanitizer(fileName,manager.getErrors(), types, dataService);
		final Map<String,Integer> positions = new HashMap<String,Integer>();
		final Set<DuplicateHelper> duplicateHelpers = new HashSet<DuplicateHelper>();
		
		boolean readEntirely = false;
		if(!Arrays.asList(headers).contains(ImportExportConstant.DATA_LOCATION_CODE) || !Arrays.asList(headers).contains(ImportExportConstant.PERIOD_CODE) || !Arrays.asList(headers).contains(ImportExportConstant.DATA_CODE) || !Arrays.asList(headers).contains(ImportExportConstant.DATA_VALUE) || !Arrays.asList(headers).contains(ImportExportConstant.DATA_VALUE_ADDRESS))
			manager.getErrors().add(new ImporterError(fileName,csvMapReader.getLineNumber(), Arrays.asList(headers).toString(),"import.error.message.unknowm.header"));
		else{
			while (!readEntirely) {
				readEntirely = (Boolean)getTransactionTemplate().execute(new TransactionCallback() {
					@Override
					public Object doInTransaction(TransactionStatus arg0) {
						try {
							return importData(fileName, csvMapReader, ImportExportConstant.NUMBER_OF_LINES_TO_IMPORT, sanitizer, headers, duplicateHelpers, positions);
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
