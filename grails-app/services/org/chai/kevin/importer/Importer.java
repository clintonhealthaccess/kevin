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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.Sanitizer;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.ValueService;

/**
 * @author Jean Kahigiso M.
 *
 */
public abstract class Importer {
	
	private static final Log log = LogFactory.getLog(Importer.class);
	static final String CODE_HEADER = "code";
	static final String DATA_ELEMENT_HEADER = "raw_data_element";
	static final String VALUE_HEADER = "data_value";
	
	protected ValueService valueService;
	
	public Importer(ValueService valueService) {
		super();
		this.valueService = valueService;
	}

	public void importCsvFile(String fileName, InputStream inputStream) throws IOException{
		importData(fileName,new InputStreamReader(inputStream));
	}
	
	public void importZipFiles(InputStream inputStream) throws IOException{
		ZipInputStream zipInputStream = null;
		zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
		ZipEntry zipEntry;
		while((zipEntry=zipInputStream.getNextEntry())!=null){
			if(!zipEntry.isDirectory())
				importData(zipEntry.getName(),new InputStreamReader(zipInputStream));
			if (log.isDebugEnabled()) log.debug("zipEntryName " +zipEntry.getName());
		}
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
	
		@Override
		public Object sanitizeValue(Object value, Type type, String prefix,String genericPrefix) {
			switch (type.getType()) {
			case ENUM:
				return validateImportEnum(fileName,genericPrefix, value);
			case BOOL:
				return validateImportBool(fileName,genericPrefix, value);
			case NUMBER:
				return validateImportNumber(fileName,genericPrefix, value);
			case TEXT:
				return validateImportString(fileName,genericPrefix, value);
			case STRING:
				return validateImportString(fileName,genericPrefix, value);
			case DATE:
				return validateImportDate(fileName,genericPrefix, value);
			default:
				errors.add(new ImporterError(fileName,lineNumber, prefix, "import.error.message.unknown.type")); 
				return null;
			}
		}
	
		private String validateImportEnum(String fileName, String header, Object value) {
			Enum enume = getAndStoreEnum(types.get(header).getEnumCode());
			if (enume != null) {
				EnumOption option = enume.getOptionForValue(value.toString());
				if (option != null) return option.getValue();
			}
			this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
			errors.add(new ImporterError(fileName,lineNumber, header,"import.error.message.enume"));
			return value.toString();
		}

		private Boolean validateImportBool(String fileName,String header, Object value){
			if (((String) value).equals("0") || ((String) value).equals("1"))
				if (((String) value).equals("1"))
					return true;
				else
					return false;
			this.setNumberOfErrorInRows(this.getNumberOfErrorInRows() + 1);
			errors.add(new ImporterError(fileName,lineNumber, header,"error.message.boolean"));
			return null;
		}
		
		private Number validateImportNumber(String fileName,String header, Object value) {
			try {
				return Double.parseDouble((String) value);
			} catch (NumberFormatException e) {
				if (log.isDebugEnabled()) log.debug("value in this cell [Line: " + lineNumber+ ",Column: " + header + "] has to be a Number"+ value, e);
			}
			this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
			errors.add(new ImporterError(fileName,lineNumber, header,"import.error.message.number"));
			return null;
		}
		
		private String validateImportString(String fileName,String header, Object value){
			if(value instanceof String || value.equals(""))
				return (String) value;
			this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
			errors.add(new ImporterError(fileName,lineNumber, header, "import.error.message.string.text")); 
			return null;
		}
		
		private Date validateImportDate(String fileName,String header, Object value){
			if(value instanceof String)
				try {
					return Utils.parseDate((String)value);
				} catch (ParseException e) {
					if (log.isDebugEnabled()) log.debug("value in this cell [Line: " + lineNumber+ ",Column: " + header + "] has to be a Date (dd-MM-yyyy)"+ value, e);
				}
			this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
			errors.add(new ImporterError(fileName,lineNumber, header, "import.error.message.date")); 
			return null;
		}
		
	}
	
	protected static List<String> getLineNumberString(Integer lineNumber) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i <= lineNumber; i++) {
			result.add("["+i+"]");
		}
		return result;
	}

	
	/**
	 * Imports one file.
	 * 
	 * @param fileName
	 * @param reader
	 * @throws IOException
	 */
	public abstract void importData(String fileName, Reader reader) throws IOException;
	
}
