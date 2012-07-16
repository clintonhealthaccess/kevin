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

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.chai.kevin.data.DataService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.Sanitizer;
import org.chai.kevin.util.ImportExportConstant;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.ValueService;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Jean Kahigiso M.
 *
 */
public abstract class DataImporter extends FileImporter {
	
	protected ValueService valueService;
	protected PlatformTransactionManager transactionManager;
	protected TransactionTemplate transactionTemplate;
	
	protected TransactionTemplate getTransactionTemplate() {
		if (transactionTemplate == null) {
			transactionTemplate = new TransactionTemplate(transactionManager);
			transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
		}
		return transactionTemplate;
	}
	
	public DataImporter(ValueService valueService,PlatformTransactionManager transactionManager) {
		super();
		this.valueService = valueService;
		this.transactionManager = transactionManager;
	}

	protected class ImportSanitizer implements Sanitizer {
		private Map<String, Enum> enumMap = new HashMap<String, Enum>();
		
		private DataService dataService;
		private final List<ImporterError> errors;
		private final Map<String,Type> types;
		private final String fileName;
		
		public ImportSanitizer(String fileName,List<ImporterError> errors, Map<String,Type> types, DataService dataService) {
			super();
			this.fileName = fileName;
			this.errors = errors;
			this.types = types;
			this.dataService = dataService;
		}
		
		private Integer lineNumber;
		private Map<Integer,String> lineNumberAddress = new HashMap<Integer, String>();
		private Integer numberOfErrorInRows;
		
		private Enum getAndStoreEnum(String code) {
			if (!enumMap.containsKey(code)) {
				Enum enume = dataService.findEnumByCode(code);
				enumMap.put(code, enume);
			}
			return enumMap.get(code);
		}
		
		public void setLineNumber(Integer lineNumber) {
			this.lineNumber = lineNumber;
		}
		
		public Integer getNumberOfErrorInRows() {
			return numberOfErrorInRows;
		}
		
		public void setNumberOfErrorInRows(Integer numberOfErrorInRows) {
			this.numberOfErrorInRows = numberOfErrorInRows;
		}
		
		public void addType(String header,Type type){
			types.put(header, type);
		}
		public void clearType(){
			types.clear();
		}
		
		public void addLineNumberMap(Integer lineNumber,String address){
			lineNumberAddress.put(lineNumber,address);
		}
		public void clearLineNumberMap(){
			lineNumberAddress.clear();
		}

		public Map<Integer,String> getLineNumberAddress() {
			return lineNumberAddress;
		}

		public void setLineNumberAddress(Map<Integer,String> lineNumberAddress) {
			this.lineNumberAddress = lineNumberAddress;
		}
		
		@Override
		public Object sanitizeValue(Object value, Type type, String prefix, String genericPrefix) {
			switch (type.getType()) {
			case ENUM:
				return validateImportEnum(fileName,prefix,genericPrefix, value);
			case BOOL:
				return validateImportBool(fileName,prefix,genericPrefix, value);
			case NUMBER:
				return validateImportNumber(fileName,prefix,genericPrefix, value);
			case TEXT:
				return validateImportString(fileName,prefix,genericPrefix, value);
			case STRING:
				return validateImportString(fileName,prefix,genericPrefix, value);
			case DATE:
				return validateImportDate(fileName,prefix,genericPrefix, value);
			default:
				errors.add(new ImporterError(fileName,Utils.getKeyByValue(lineNumberAddress, prefix), genericPrefix, "import.error.message.unknown.type")); 
				return null;
			}
		}
		
	
		private String validateImportEnum(String fileName, String prefixHeader, String genericPrefixHeader, Object value) {
			if (log.isTraceEnabled()) log.trace("looking for map content: " + types + " prefixHeader" + prefixHeader + " genericPrefixHeader" + genericPrefixHeader);
			Type type;
			if (types.get(prefixHeader) != null)
				type = types.get(prefixHeader);
			else
				type = types.get(genericPrefixHeader);

			Enum enume = getAndStoreEnum(type.getEnumCode());
			if (enume != null) {
				EnumOption option = enume.getOptionForValue(value.toString());
				if (option != null)
					return option.getValue();
			}
			this.setNumberOfErrorInRows(this.getNumberOfErrorInRows() + 1);
			errors.add(new ImporterError(fileName, Utils.getKeyByValue(lineNumberAddress, prefixHeader+""+genericPrefixHeader), prefixHeader,
					"import.error.message.enume"));
			return value.toString();
		}

		private Boolean validateImportBool(String fileName,String prefixHeader,String header, Object value){
			if(log.isTraceEnabled()) log.trace("imported bool value :"+value);
			if (((String) value).equals(ImportExportConstant.TRUE) || ((String) value).equals(ImportExportConstant.FALSE))
				if (((String) value).equals(ImportExportConstant.TRUE))
					return true;
				else
					return false;
			
			this.setNumberOfErrorInRows(this.getNumberOfErrorInRows() + 1);
			errors.add(new ImporterError(fileName,Utils.getKeyByValue(lineNumberAddress, prefixHeader), header,"import.error.message.boolean"));
			return null;
		}
		
		private Number validateImportNumber(String fileName,String prefixHeader,String header, Object value) {
			try {
				return Double.parseDouble((String) value);
			} catch (NumberFormatException e) {
				if (log.isDebugEnabled()) log.debug("value in this cell [Line: " + lineNumber+ ",Column: " + header + "] has to be a Number"+ value, e);
			}
			this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
			errors.add(new ImporterError(fileName,Utils.getKeyByValue(lineNumberAddress, prefixHeader), header,"import.error.message.number"));
			return null;
		}
		
		private String validateImportString(String fileName,String prefixHeader,String header, Object value){
			if(value instanceof String || value.equals(""))
				return (String) value;
			this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
			errors.add(new ImporterError(fileName,Utils.getKeyByValue(lineNumberAddress, prefixHeader), header, "import.error.message.string.text")); 
			return null;
		}
		
		private Date validateImportDate(String fileName,String prefixHeader,String header, Object value){
			if(value instanceof String)
				try {
					return Utils.parseDate((String)value);
				} catch (ParseException e) {
					if (log.isDebugEnabled()) log.debug("value in this cell [Line: " + lineNumber+ ",Column: " + header + "] has to be a Date (dd-MM-yyyy)"+ value, e);
				}
			this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
			errors.add(new ImporterError(fileName,Utils.getKeyByValue(lineNumberAddress, prefixHeader), header, "import.error.message.date")); 
			return null;
		}

		@Override
		public String toString() {	
			return "ImportSanitizer [types=" + types + ", lineNumber="
					+ lineNumber + "]";
		}
		
	}
	
	protected void saveAndMergeIfNotNull(RawDataElementValue rawDataElementValue, Map<String,Object> positionsValueMap, ImportSanitizer sanitizer) {
		if (rawDataElementValue != null) {
			if (log.isTraceEnabled()) log.trace("sanitizer line: "+sanitizer);
			if (log.isDebugEnabled()) log.debug("merging with data from map of header and data "+ positionsValueMap);
			if (log.isTraceEnabled()) log.trace("value before merge" + rawDataElementValue.getValue());
			rawDataElementValue.setValue(
				rawDataElementValue.getData().getType().mergeValueFromMap(rawDataElementValue.getValue(), positionsValueMap, "", new HashSet<String>(), sanitizer)
			);
			if (log.isTraceEnabled()) log.trace("value after merge " + rawDataElementValue.getValue());
			
			valueService.save(rawDataElementValue);
			if (log.isTraceEnabled()) log.trace("saved rawDataElement: "+ rawDataElementValue.getValue());
		}
	}
	
	
}
