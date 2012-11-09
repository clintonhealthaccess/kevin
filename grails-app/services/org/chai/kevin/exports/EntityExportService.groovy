package org.chai.kevin.exports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.util.ImportExportConstant;
import org.chai.kevin.util.Utils;
import org.hibernate.SessionFactory;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class EntityExportService {
	
	private static final Log log = LogFactory.getLog(EntityExportService.class);
	
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}		
	
	private final static String CSV_FILE_EXTENSION = ".csv";	
	
	public String getExportFilename(Class<?> clazz){
		String exportFilename = clazz.getSimpleName().replaceAll("[^a-zA-Z0-9]", "") + "_";
		return exportFilename;
	}		
	
	@Transactional(readOnly=true)
	public File getExportFile(String filename, Class<?> clazz) throws IOException { 				
		
		File csvFile = File.createTempFile(filename, CSV_FILE_EXTENSION);		
		
		FileWriter csvFileWriter = new FileWriter(csvFile);
		ICsvListWriter writer = new CsvListWriter(csvFileWriter, CsvPreference.EXCEL_PREFERENCE);
		try {			
			
			// headers
			List<Field> entityFieldHeaders = new ArrayList<Field>();			
			Class<?> headerClass = clazz;
			while(headerClass != null && headerClass != Object.class){				
				Field[] classFields = headerClass.getDeclaredFields();
				for(Field field : classFields){
					if(	!field.getName().equals("errors") && !field.getName().equals("version") 
						&& !field.getName().startsWith("\$") && !field.getName().startsWith("__") 
						&& !field.isSynthetic() && !Modifier.isStatic(field.getModifiers())) {
						entityFieldHeaders.add(field);
					}
				}
				headerClass = headerClass.getSuperclass();
			}
			Collections.sort(entityFieldHeaders, EntityHeaderSorter.BY_FIELD());
			
			//TODO custom headers/values
			//ability to add custom headers
			//and a custom "handle" method to add the custom values to each row			
						
			List<String> entityHeaders = new ArrayList<String>();
			for(Field field : entityFieldHeaders){
				entityHeaders.add(field.getName());				
			}			
			if(entityHeaders.toArray(new String[0]) != null)
				writer.writeHeader(entityHeaders.toArray(new String[0]));									
			
			//entities
			List<Object> entities = getEntities(clazz);
			for(Object entity : entities){
				if (log.isDebugEnabled()) log.debug("getExportFile(entity="+entity+")");				
				List<String> entityData = getEntityData(entity, entityFieldHeaders);
				
				//TODO custom headers/values
				//ability to add custom headers
				//and a custom "handle" method to add the custom values to each row
				
				if(entityData != null && !entityData.isEmpty())
					writer.write(entityData);
			}
			
		} catch (IOException ioe){
			// TODO is this good ?
			throw ioe;
		} finally {
			writer.close();
		}
		
		return csvFile;
	}	
	
	@SuppressWarnings("unchecked")
	private List<Object> getEntities(Class<?> clazz){
		return (List<Object>) sessionFactory.getCurrentSession().createCriteria(clazz).list();
	}
	
	public List<String> getEntityData(Object entity, List<Field> fields){
		List<String> entityData = new ArrayList<String>();
		for (Field field : fields) {			
			try {
				boolean isNotAccessible = false;
				if (!field.isAccessible()) { 
					field.setAccessible(true);
					isNotAccessible = true;
				}
				
				Object value = field.get(entity);								
				if (log.isDebugEnabled()) log.debug("header: " + field.getName() + ", field: " + value);
				
				if (value != null){
					if (log.isDebugEnabled()) log.debug("exporting value of class: "+field.getType());
					entityData.add(exportValue(value));
				}
				else {
					entityData.add("");
				}
				
				if(isNotAccessible) field.setAccessible(false);	
			} catch (IllegalArgumentException e) {
				if (log.isWarnEnabled()) log.warn("could not read field value: "+field+", on value: "+entity, e);
			} catch (IllegalAccessException e) {
				if (log.isWarnEnabled()) log.warn("could not access field value: "+field+", on value: "+entity, e);
			}
		}
		return entityData;
	}

	private String exportValue(Object value) {
		String exportValue = "";
		
		Class<?> valueClazz = null;
		if (value instanceof HibernateProxy) {
			valueClazz = ((HibernateProxy) value).getHibernateLazyInitializer().getImplementation().getClass();
			value = valueClazz.cast(value);
		}
		else {
			valueClazz = value.getClass();
		}
		
		if (log.isDebugEnabled()) log.debug("exporting value for value: "+value+", class: "+valueClazz);
		
		//value is a primitive or 'wrapper to primitive' or string type
		if (isExportablePrimitive(valueClazz) != null){
			exportValue = value.toString();
		}
		//value is a string
		else if (valueClazz.equals(String.class)){
			exportValue = value.toString();
		}
		//value is a date
		else if(valueClazz.equals(Date.class)){
			exportValue = Utils.formatDate((Date) value);
		}
		else if (Collection.class.isAssignableFrom(valueClazz)){
			//value is a collection
			List<String> collectionExportValue = new ArrayList<String>();
			value.each { element ->
				collectionExportValue.add(
					this.exportValue(element)
				);
			}
			exportValue = "[" + StringUtils.join(collectionExportValue, ", ") + "]";
		}					
		//value is exportable
		else if (hasField(value, "id") || hasField(value, "code")) {
			if (hasField(value, "id")) {
				exportValue += (Long)getValue(value, "id");
			}
			if (hasField(value, "code")) {
				if (!exportValue.isEmpty()) exportValue += ImportExportConstant.CODE_DELIMITER;
				exportValue += (String)getValue(value, "code");
			}
		}
		//value is not exportable or a primitive type
		else {
			exportValue = ImportExportConstant.VALUE_NOT_EXPORTABLE;
		}
		
		if (log.isDebugEnabled()) log.debug("export value: "+exportValue);
		return exportValue;
	}

	private static Object getValue(Object object, String fieldName) {
		return object."$fieldName"
	}
	
	private static boolean hasField(def object, String field) {
		return object.hasProperty(field);
	}						
	
	private static Class<?> isExportablePrimitive(Class<?> clazz) {
		Class<?> exportableClazz = null;		
		if(clazz.isPrimitive() || ClassUtils.wrapperToPrimitive(clazz) != null){
			exportableClazz = clazz;
		}
		return exportableClazz;
	}
	
}