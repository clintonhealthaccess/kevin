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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LocationService;
import org.chai.kevin.Period;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.Sanitizer;
import org.chai.kevin.importer.Importer.ImportSanitizer;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.value.ValueService;
import org.hibernate.SessionFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

/**
 * @author Jean Kahigiso M.
 *
 */
public class ImportExporter extends Importer{
	
	static final String PERIOD_HEADER = "period";
	static final String VALUE_ADDRESS_HEADER = "value_address";
	private static final Integer NUMBER_OF_LINES_TO_IMPORT = 100;
	private static final Log log = LogFactory.getLog(ImportExporter.class);
	
	private LocationService locationService;
	private DataService dataService;
	private PlatformTransactionManager transactionManager;
	private SessionFactory sessionFactory;
	private ImporterErrorManager manager;
	private TransactionTemplate transactionTemplate;
	
	private TransactionTemplate getTransactionTemplate() {
		if (transactionTemplate == null) {
			transactionTemplate = new TransactionTemplate(transactionManager);
			transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
		}
		return transactionTemplate;
	}

	public ImportExporter(LocationService locationService,
			ValueService valueService, DataService dataService,
			SessionFactory sessionFactory,
			PlatformTransactionManager transactionManager,
			ImporterErrorManager manager) {
		super(valueService);
		this.locationService = locationService;
		this.dataService = dataService;
		this.sessionFactory = sessionFactory;
		this.transactionManager = transactionManager;
		this.manager = manager;

	}
	public boolean importData(String fileName,ICsvMapReader reader,Integer NUMBER_OF_LINES_TO_IMPORT, Sanitizer sanitizer,  String[]  headers, Map<String,Integer> positions){
		
		try {
			Map<DataLocation, Map<Period,Set<RawDataElement>>> savedData = new HashMap<DataLocation,  Map<Period,Set<RawDataElement>>>();

			Map<String, String> rows = reader.read(headers);
			while (rows != null) {
				
			}
		} catch (IOException e) {
			return true;
		}
		
		return true;
	}
	
	@Override
	public void importData(final String fileName, final Reader reader) throws IOException {
		if (log.isInfoEnabled()) log.info("importData( fileName "+fileName+" reader "+reader);
		manager.setCurrentFileName(fileName);
		
		final ICsvMapReader readFileAsMap = new CsvMapReader(reader, CsvPreference.EXCEL_PREFERENCE);
		final String[] headers = readFileAsMap.getCSVHeader(true);
		Map<String,Type> types = new HashMap<String,Type>();	
		
		final ImportSanitizer sanitizer = new ImportSanitizer(fileName,manager.getErrors(), types, dataService);
		final Map<String,Integer> positions = new HashMap<String,Integer>();
		
		boolean readEntirely = false;
		if(!Arrays.asList(headers).contains(LOCATION_CODE_HEADER) || !Arrays.asList(headers).contains(PERIOD_HEADER) || !Arrays.asList(headers).contains(DATA_HEADER) || !Arrays.asList(headers).contains(VALUE_ADDRESS_HEADER) || !Arrays.asList(headers).contains(VALUE_HEADER))
			manager.getErrors().add(new ImporterError(fileName,readFileAsMap.getLineNumber(), Arrays.asList(headers).toString(),"import.error.message.unknowm.header"));
		else{
			while (!readEntirely) {
				readEntirely = (Boolean)getTransactionTemplate().execute(new TransactionCallback() {
					@Override
					public Object doInTransaction(TransactionStatus arg0) {
						return importData(fileName, readFileAsMap, NUMBER_OF_LINES_TO_IMPORT, sanitizer,headers, positions);
					}
				});
				sessionFactory.getCurrentSession().clear();
			}
		}
		
	}

}
