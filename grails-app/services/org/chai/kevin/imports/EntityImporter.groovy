package org.chai.kevin.imports;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.exports.EntityHeaderSorter;
import org.chai.kevin.Importable;
import org.chai.kevin.LocationService;
import org.chai.kevin.Translation;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.imports.ImporterError;
import org.chai.kevin.imports.ImporterErrorManager;
import org.chai.kevin.json.JSONMap;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.ValueService;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

public class EntityImporter extends FileImporter {

	private static final Log log = LogFactory.getLog(EntityImporter.class);
	private static final String CODE_HEADER = "code";
	
	private SessionFactory sessionFactory;
	private ImporterErrorManager manager;
	private Class<?> clazz;
	
	public EntityImporter(SessionFactory sessionFactory, ImporterErrorManager manager, Class<?> clazz) {
		this.sessionFactory = sessionFactory;
		this.manager = manager;
		this.clazz = clazz;
	}

	public void importData(String filename, ICsvMapReader csvMapReader) throws IOException {
		if (log.isDebugEnabled()) log.debug("importData(filename: " + filename + ", reader: "+csvMapReader+")");
			
		manager.setNumberOfSavedRows(0);
		manager.setNumberOfUnsavedRows(0);
		manager.setNumberOfRowsSavedWithError(0);
		
		try {						
			//headers
			final String[] headers = csvMapReader.getCSVHeader(true);
			List<Field> fields = new ArrayList<Field>();			
			Class<?> headerClass = clazz;
			while (headerClass != null && headerClass != Object.class) {				
				Field[] classFields = headerClass.getDeclaredFields();
				for (Field field : classFields) {
					fields.add(field);
				}
				headerClass = headerClass.getSuperclass();
			}			
			Collections.sort(fields, EntityHeaderSorter.BY_FIELD());			
			
			List<String> fieldNames = new ArrayList<String>();
			for (Field field : fields){
				if (!field.getName().equals("id")) fieldNames.add(field.getName());
			}
			Collection missingHeaders = CollectionUtils.subtract(fieldNames, headers as Collection)
			if (!missingHeaders.isEmpty()) {
				for (String missingHeader : missingHeaders) {
					manager.getErrors().add(new ImporterError(filename, csvMapReader.getLineNumber(), missingHeader, "import.error.message.missing.header"));
				}
			}
			Collection unknownHeaders = CollectionUtils.subtract(headers as Collection, fieldNames)
			if (!unknownHeaders.isEmpty()) {
				for (String unknownHeader : unknownHeaders) {
					manager.getErrors().add(new ImporterError(filename, csvMapReader.getLineNumber(), unknownHeader, "import.error.message.unknown.header"));
				}
			}
			
			List<String> entityCodes = new ArrayList<String>();									
			Map<String, String> row = readRow(filename, csvMapReader, headers, manager);
			
			//entities
			while (row != null) {
				if (!row.isEmpty()) {
					Object entity = null;
					
					// TODO what if there is no CODE_HEADER, what if the code is not called code ?
					String entityCode = row.get(CODE_HEADER);
					if (entityCode == null) {
						manager.getErrors().add(new ImporterError(filename, csvMapReader.getLineNumber(), "", "import.error.message.code.noheader"));
						manager.incrementNumberOfUnsavedRows();
					}
					else if (entityCodes.contains(entityCode)) {
						manager.getErrors().add(new ImporterError(filename, csvMapReader.getLineNumber(), CODE_HEADER, "import.error.message.data.duplicated"));
						manager.incrementNumberOfUnsavedRows();
					}
					else {
						entityCodes.add(entityCode);
															
						if (entityCode == null || entityCode.isEmpty()) {
							try {
								entity = clazz.newInstance();
							} catch (InstantiationException e) {
								// TODO change this
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								// TODO change this
								e.printStackTrace();
							}
						}
						else {
							entity = findEntityByCode(entityCode, clazz);
						}
						
						if (entity == null) {
							manager.getErrors().add(new ImporterError(filename, csvMapReader.getLineNumber(),CODE_HEADER,"import.error.message.entity.notfound"));
							manager.incrementNumberOfUnsavedRows();
						}									
						else {	
							EntityImportSanitizer sanitizer = new EntityImportSanitizer(manager.getErrors(), filename);
							sanitizer.setHeaders(Arrays.asList(headers))
							sanitizer.setLineNumber(csvMapReader.getLineNumber());
							sanitizer.setNumberOfErrorInRows(0);
														
							setEntityData(row, entity, fields, sanitizer);						
							
							if (sanitizer.getNumberOfErrorInRows() > 0)
								manager.incrementNumberOfRowsSavedWithError(1);
							
							if(!entity.validate()){
								manager.getErrors().add(new ImporterError(filename, csvMapReader.getLineNumber(),"","import.error.message.entity.invalid"));
								manager.incrementNumberOfUnsavedRows();
							}
							else{
								entity.save(flush: true);
								manager.incrementNumberOfSavedRows();
							}
						}
					}
				}
															
				row = readRow(filename, csvMapReader, headers, manager);
			}
		} catch (IOException ioe) {
			// TODO change this
			throw ioe;
		}
	}

	private void setEntityData(Map<String, String> row, Object entity, List<Field> fields, EntityImportSanitizer sanitizer){					
		
		for (Field field : fields) {
			String fieldName = field.getName();
			if (log.isDebugEnabled()) 
				log.debug("header: " + fieldName + ", field: " + row.get(fieldName));
				
			try {
				boolean isNotAccessible = false;
				if (!field.isAccessible()) { 
					field.setAccessible(true);
					isNotAccessible = true;
				}				
										
				Object newValue = row.get(fieldName);
				if (newValue == null || newValue.isEmpty()) newValue = "null";
				else {
					Class<?> clazz = null;
					clazz = field.getType();
					Class<?> innerClazz = null;
					
					//value is a list
					if(clazz.equals(List.class)){
						ParameterizedType type = (ParameterizedType) field.getGenericType();
						innerClazz = (Class) type.getActualTypeArguments()[0];
					}
					
					newValue = sanitizer.sanitizeValue(fieldName, newValue, clazz, innerClazz);					
					field.set(entity, newValue);			
				}
				
				if (isNotAccessible) field.setAccessible(false);	
			} catch (IllegalArgumentException e) {
				// TODO change this
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO change this
				e.printStackTrace();
			}						
		}		
	}	

	public class EntityImportSanitizer {
		
		private List<ImporterError> errors;
		private List<String> headers;
		private Integer lineNumber;
		private Integer numberOfErrorInRows;		
		private String filename;
		
		public EntityImportSanitizer(List<ImporterError> errors, String filename) {
			this.errors = errors;
			this.filename = filename;
		}
		
		public void setHeaders(List<String> headers){
			this.headers = headers;
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
		
		public Object sanitizeValue(String header, String value, Class<?> valueClazz, Class<?> innerClazz){
			
			Object importValue = null;			
			
			if(!headers.contains(header)){
				errors.add(new ImporterError(filename, lineNumber, header, "import.error.message.unknown.header"));
				return importValue;
			}			
																								
			if(value != null && !value.isEmpty()){
			
				//value is not a list
				boolean isAssignable = Importable.class.isAssignableFrom(valueClazz);
				Class<?>[] clazzInterfaces = valueClazz.getInterfaces();
				Class<?> importableClazz = valueClazz;
				
				//value is a list
				if(innerClazz != null){
					isAssignable = Importable.class.isAssignableFrom(innerClazz);
					clazzInterfaces = innerClazz.getInterfaces();
					importableClazz = innerClazz;
				}		
				
				Importable importable = null;
				
				//value is importable
				if(isAssignable && Arrays.asList(clazzInterfaces).contains(Importable.class)){

					importable = (Importable) importableClazz.newInstance();
					
					//value is a map
					if(importable instanceof JSONMap){
						importValue = getImportValue(importable, value);
					}
					else{																		
						
						List<?> importEntities = new ArrayList<?>();
						importEntities = getImportValues(importable, value, importableClazz, header);
						
						// value is a list
						if(innerClazz != null){							
							importValue = importEntities;
						}						
						// value is not a list
						else if(importEntities.size() == 1){
							importValue = importEntities.get(0);
						}
						else{
							this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
							// TODO what is this error message ?
							errors.add(new ImporterError(filename, lineNumber, header,"import.error.message.importentitiesinvalid"));
						}
					}										
					
				}
				//value is a primitive or 'wrapper to primitive' or string type
				else if(importableClazz.isPrimitive() || ClassUtils.wrapperToPrimitive(importableClazz) != null){				
					importValue = value;
				}
				//value is a string
				else if(importableClazz.equals(String.class)){
					importValue = value;
				}
				//value is a date
				else if(importableClazz.equals(Date.class)){
					//TODO				
				}
				//value is not importable or a primitive type
				else {
					// TODO we can catch this way before here
					this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
					errors.add(new ImporterError(filename, lineNumber, header,"import.error.message.entity.notimportable"));
				}
			}					
			
			return importValue;
		}		
		
		private Object getImportValue(Importable importable, Object value){
			Object result = importable.fromExportString(value);
			return result;
		}
		
		private List<?> getImportValues(Importable importable, Object value, Class<?> importableClazz, String header){
			List<?> importEntities = new ArrayList<Object>();
			String codePattern = Utils.CODE_PATTERN;
			Pattern pattern = Pattern.compile(codePattern);
			Matcher matcher = pattern.matcher(value);
			while(matcher.find()){
				String entityCode = matcher.group();
				if(entityCode != null && !entityCode.isEmpty()){
					entityCode = entityCode.replaceAll(Utils.CODE_DELIMITER, "");
					Object importEntity = findEntityByCode(entityCode, importableClazz);
					if(importEntity != null){
						importEntity = getImportValue(importable, importEntity);
						importEntities.add(importEntity);
					}
					else{
						this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
						// TODO what is this error message ?
						errors.add(new ImporterError(filename, lineNumber, header,"import.error.message.importentitynull"));
						break;
					}
				}
			}
			return importEntities;
		}		
	}
	
	public <T extends Object> T findEntityById(String id, Class<T> clazz) {
		Long entityId = Long.parseLong(id);
		return (T)sessionFactory.getCurrentSession().get(clazz, entityId);
	}
	
	public <T extends Object> T findEntityByCode(String code, Class<T> clazz) {
		return (T)sessionFactory.getCurrentSession().createCriteria(clazz)
				.add(Restrictions.eq("code", code)).uniqueResult();
	}

}