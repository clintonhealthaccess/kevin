package org.chai.kevin.entity;

import java.lang.annotation.Annotation
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.util.regex.Matcher
import java.util.regex.Pattern

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.entity.export.EntityHeaderSorter;
import org.chai.kevin.entity.export.Importable
import org.chai.kevin.LocationService
import org.chai.kevin.Translation
import org.chai.kevin.data.EnumOption
import org.chai.kevin.importer.ImporterError
import org.chai.kevin.importer.ImporterErrorManager
import org.chai.kevin.util.Utils
import org.hibernate.SessionFactory
import org.supercsv.io.CsvMapReader
import org.supercsv.io.ICsvMapReader
import org.supercsv.prefs.CsvPreference;


public class EntityImportService {

	private static final Log log = LogFactory.getLog(EntityImportService.class);

	private SessionFactory sessionFactory;
	private LocationService locationService;
	private static final String ID_HEADER = "id";
	private static final String CODE_HEADER = "code";
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}	
	
	public void importEntityData(Reader reader, Class<?> clazz, ImporterErrorManager manager) throws IOException {		
		if (log.isDebugEnabled()) log.debug("importEntityData(Reader:" + reader + " Class:"+ clazz.name + "ImporterErrorManager: " + manager + ")");
			
		ICsvMapReader readFileAsMap = new CsvMapReader(reader, CsvPreference.EXCEL_PREFERENCE);		
		
		manager.setNumberOfSavedRows(0);
		manager.setNumberOfUnsavedRows(0);
		manager.setNumberOfRowsSavedWithError(0);
		
		try {						
			//headers
			final String[] headers = readFileAsMap.getCSVHeader(true);
			List<Field> fields = new ArrayList<Field>();			
			Class<?> headerClass = clazz;
			while(headerClass != null && headerClass != Object.class){				
				Field[] classFields = headerClass.getDeclaredFields();
				for(Field field : classFields){
					fields.add(field);
				}
				headerClass = headerClass.getSuperclass();
			}			
			Collections.sort(fields, EntityHeaderSorter.BY_FIELD());			
			
			List<String> fieldNames = new ArrayList<String>();
			for(Field field : fields){
				fieldNames.add(field.getName());
			}			
			if(!Arrays.asList(headers).containsAll(fieldNames))
				manager.getErrors().add(new ImporterError(readFileAsMap.getLineNumber(),Arrays.asList(headers).toString(),"import.error.message.unknown.header"));
			else{
				
				List<String> entityCodes = new ArrayList<String>();									
				Map<String, String> row = readFileAsMap.read(headers);
				
				//entities
				while (row != null) {					
					Object entity = null;
					
//					//TODO use code
//					String entityCode = row.get(CODE_HEADER);					
//					if(entityCodes.contains(entityCode)){
//						manager.getErrors().add(new ImporterError(readFileAsMap.getLineNumber(),CODE_HEADER,"entity code is already in use"));
//						manager.incrementNumberOfUnsavedRows();
//						continue;
//					}
//					entityCodes.add(entityCode);
					
					//TODO use code
					String entityId = row.get(ID_HEADER);					
					if(entityId == null || entityId.isEmpty()){
						try {
							entity = clazz.newInstance();
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					else{
						//TODO use code
						entity = findEntityById(entityId, clazz);					
					}
					
					if(entity == null){
						manager.getErrors().add(new ImporterError(readFileAsMap.getLineNumber(),ID_HEADER,"entity is null"));
						manager.incrementNumberOfUnsavedRows();
						continue;
					}									
						
					EntityImportSanitizer sanitizer = new EntityImportSanitizer(manager.getErrors());
					sanitizer.setHeaders(Arrays.asList(headers))
					sanitizer.setLineNumber(readFileAsMap.getLineNumber());
					sanitizer.setNumberOfErrorInRows(0);
												
					entity = setEntityData(row, entity, fields, sanitizer);						
					
					if (sanitizer.getNumberOfErrorInRows() > 0)
						manager.incrementNumberOfRowsSavedWithError(1);
					manager.incrementNumberOfSavedRows();
					
					if(!entity.validate()){
						manager.getErrors().add(new ImporterError(readFileAsMap.getLineNumber(),"blank","entity is invalid"));
						manager.incrementNumberOfUnsavedRows();
					}
					else{
						entity.save();
						manager.incrementNumberOfSavedRows();
					}
																
					row = readFileAsMap.read(headers);
				}								
			}

		} catch (IOException ioe) {
			// TODO Please throw something meaningful
			throw ioe;
		} finally {
			readFileAsMap.close();
		}
		
		
	}

	private Object setEntityData(Map<String, String> row, Object entity, List<Field> fields, EntityImportSanitizer sanitizer){					
		
		for(Field field : fields){
			String fieldName = field.getName();
			if (log.isDebugEnabled()) 
				log.debug("header: " + fieldName + ", field: " + row.get(fieldName));						

			try {
				boolean isNotAccessible = false;
				if(!field.isAccessible()){ 
					field.setAccessible(true);
					isNotAccessible = true;
				}
				
				if(fieldName.equalsIgnoreCase(ID_HEADER)){
					continue;
				}
										
				Object newValue = row.get(fieldName);
				if(newValue == null || newValue.isEmpty())
					newValue = "null";
				else{
					Class<?> clazz = null;
					clazz = field.getType();
					Class<?> innerClazz = null;
					if(clazz.equals(List.class)){
						ParameterizedType type = (ParameterizedType) field.getGenericType();
						innerClazz = (Class) type.getActualTypeArguments()[0];
					}
					newValue = sanitizer.sanitizeValue(fieldName, newValue, clazz, innerClazz);					
					field.set(entity, newValue);					
				}
				
				if(isNotAccessible)
					field.setAccessible(false);	
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}						
		}		

		return entity;
	}	

	private class EntityImportSanitizer {
		
		private List<ImporterError> errors;
		private List<String> headers;
		private Integer lineNumber;
		private Integer numberOfErrorInRows;		
		
		public EntityImportSanitizer(List<ImporterError> errors) {
			this.errors = errors;
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
				errors.add(new ImporterError(lineNumber, header, "import.error.message.unknown.header"));
				return importValue;
			}			
															
			boolean isAssignable = Importable.class.isAssignableFrom(valueClazz);
			Class<?>[] clazzInterfaces = valueClazz.getInterfaces();			
			Class<?> importableClazz = valueClazz;
			
			boolean isList = false;
			if(valueClazz.equals(List.class)){
				isAssignable = Importable.class.isAssignableFrom(innerClazz);
				clazzInterfaces = innerClazz.getInterfaces();
				importableClazz = innerClazz;
				isList = true;
			}
			
			Importable importable = null;
			boolean isImportable = false;			
			if(isAssignable && Arrays.asList(clazzInterfaces).contains(Importable.class)){
				importable = (Importable) importableClazz.newInstance();
				isImportable = true;
			}
			
			if(value != null && !value.isEmpty()){
				
				if(isImportable && importable != null){
					
					if(importable instanceof Translation){
						importValue = Utils.getImportValue(importable, value);
					}
					
					List<?> importEntities = new ArrayList<?>();
					String codePattern = Utils.CODE_PATTERN;
					Pattern pattern = Pattern.compile(codePattern);
					Matcher matcher = pattern.matcher(value);
					while(matcher.find()){
						String entityId = matcher.group();
						if(entityId != null && !entityId.isEmpty()){
							entityId = entityId.replaceAll(Utils.CODE_DELIMITER, "");
							//TODO use code
							Object importEntity = findEntityById(entityId, importableClazz);
							if(importEntity != null){
								importEntity = Utils.getImportValue(importable, importEntity);
								importEntities.add(importEntity);
							}
							else{
								this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
								errors.add(new ImporterError(lineNumber, header,"import.error.message.importentitynull"));
								break;
							}
						}
					}					
					if(isList)
						importValue = importEntities;
					else if(importEntities.size() > 1){
						this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
						errors.add(new ImporterError(lineNumber, header,"import.error.message.importentitiestoomany"));
					}
					else if(importEntities.size() == 1){
						importValue = importEntities.get(0);
					}
					else{
						//TODO is this good?
					}
					
				}
				else {
					importValue = value;
				}
			}
					
//			switch(clazz){
//				case List.class:
//					value = validateImportList(header, importValue, innerClazz)
//					break;
//				case Translation.class:
//					value = validateImportTranslation(header, importValue, clazz)
//					break;
//				default:
//					value = importValue;
//			}
			
			return importValue;
		}		
		
//		private Translation validateImportTranslation(String header, Object value, Class<T> clazz){
//			Translation translation = new Translation();
//			translation.setJsonText(value);
//			if(translation == null){
//				this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
//				errors.add(new ImporterError(lineNumber, header,"import.error.message.translation"));
//			}
//			return translation;
//		}
		
//		//TODO use code
//		private <T> List<T> validateImportList(String header, Object value, Class<T> clazz){
//			List<T> result = new ArrayList<T>();
//			//TODO check that its not some SQL injection attack ;)
//			String codePattern = Utils.CODE_PATTERN;
//			Pattern pattern = Pattern.compile(codePattern)
//			Matcher matcher = pattern.matcher(value);
//			while(matcher.find()){
//				String entityId = matcher.group();
//				if(entityId != null && !entityId.isEmpty()){
//					entityId = entityId.substring(1);
//					T entity = findEntityById(entityId, clazz)
//					if(entity != null){
//						result.add(entity)
//					}
//					else{
//						this.setNumberOfErrorInRows(this.getNumberOfErrorInRows()+1);
//						errors.add(new ImporterError(lineNumber, header,"import.error.message.list"));
//						break;
//					}
//				}
//			}
//			return result;
//		}
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