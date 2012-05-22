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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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

/**
 * @author Jean Kahigiso M.
 *
 */
public abstract class Importer {
	
	private static final Log log = LogFactory.getLog(Importer.class);
	static final String CODE_HEADER = "code";
	static final String DATA_ELEMENT_HEADER = "raw_data_element";
	static final String VALUE_HEADER = "data_value";
	
	
	public void importUnzipFile(File file) throws IOException{
		if (Utils.isValidZip(file)) {
			FileInputStream fileInputStream = null;
			ZipInputStream zipInputStream = null;
			try {
				fileInputStream = new FileInputStream(file.getName());
				zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
				ZipEntry zipEntry;
				while((zipEntry=zipInputStream.getNextEntry())!=null){
					if(!zipEntry.isDirectory())
						importData(new InputStreamReader(zipInputStream));
				}
			} catch (FileNotFoundException e) {
				// TODO throw something meaningful
			}finally{
				zipInputStream.close();
				fileInputStream.close();
			}
		}
	}
		
	
	protected class ImportSanitizer implements Sanitizer {
		private DataService dataService;
		private final List<ImporterError> errors;
		private final Map<String,Type> types;
		
		public ImportSanitizer(List<ImporterError> errors, Map<String,Type> types,DataService dataService) {
			this.errors = errors;
			this.types = types;
			this.dataService = dataService;
		}
		
		private Integer lineNumber;
		private Integer numberOfErrorInRows;
		
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
				return validateImportEnum(genericPrefix, value);
			case BOOL:
				return validateImportBool(genericPrefix, value);
			case NUMBER:
				return validateImportNumber(genericPrefix, value);
			case TEXT:
				return validateImportString(genericPrefix, value);
			case STRING:
				return validateImportString(genericPrefix, value);
			case DATE:
				return validateImportDate(genericPrefix, value);
			default:
				errors.add(new ImporterError(lineNumber, prefix, "import.error.message.unknown.type")); 
				return null;
			}
		}
	
		private String validateImportEnum(String header, Object value) {
			Enum enumValue = new Enum();
			List<EnumOption> enumOptions = new ArrayList<EnumOption>();
			enumValue = dataService.findEnumByCode(types.get(header).getEnumCode());
			if (enumValue != null) {
				enumOptions = enumValue.getEnumOptions();
				for (EnumOption enumOption : enumOptions)
					if (enumOption.getValue().equals(value))
						return enumOption.getValue();
						
			}
			this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
			errors.add(new ImporterError(lineNumber, header,"import.error.message.enume"));
			return null;
		}

		private Boolean validateImportBool(String header, Object value){
			if (((String) value).equals("0") || ((String) value).equals("1"))
				if (((String) value).equals("1"))
					return true;
				else
					return false;
			this.setNumberOfErrorInRows(this.getNumberOfErrorInRows() + 1);
			errors.add(new ImporterError(lineNumber, header,
					"error.message.boolean"));
			return null;
		}
		
		private Number validateImportNumber(String header, Object value) {
			try {
				return Double.parseDouble((String) value);
			} catch (NumberFormatException e) {
				if (log.isDebugEnabled()) log.debug("Value in this cell [Line: " + lineNumber+ ",Column: " + header + "] has to be a Number"+ value, e);
			}
			this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
			errors.add(new ImporterError(lineNumber, header,"import.error.message.number"));
			return null;
		}
		
		private String validateImportString(String header, Object value){
			if(value instanceof String || value.equals(""))
				return (String) value;
			this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
			errors.add(new ImporterError(lineNumber, header, "import.error.message.string.text")); 
			return null;
		}
		
		private Date validateImportDate(String header, Object value){
			if(value instanceof String)
				try {
					return Utils.parseDate((String)value);
				} catch (ParseException e) {
					if (log.isDebugEnabled()) log.debug("Value in this cell [Line: " + lineNumber+ ",Column: " + header + "] has to be a Date (dd-MM-yyyy)"+ value, e);
				}
			this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
			errors.add(new ImporterError(lineNumber, header, "import.error.message.date")); 
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

	public abstract void importData(Reader reader) throws IOException;
	
}
