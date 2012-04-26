package org.chai.kevin.entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LanguageService;
import org.chai.kevin.entity.export.EntityHeaderSorter;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class EntityExportService {
	
	private static final Log log = LogFactory.getLog(EntityExportService.class);
	
	private SessionFactory sessionFactory;
	private LanguageService languageService;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}	
	
	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}
	
	private final static String CSV_FILE_EXTENSION = ".csv";	
	
	public String getExportFilename(Class clazz){
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
					entityFieldHeaders.add(field);
				}
				headerClass = headerClass.getSuperclass();
			}			
			Collections.sort(entityFieldHeaders, EntityHeaderSorter.BY_FIELD());			
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
	
	public List<Object> getEntities(Class clazz){
		List<Object> entities = new ArrayList<Object>();
		entities = (List<Object>) sessionFactory.getCurrentSession().createCriteria(clazz).list();
		return entities;
	}
	
	public List<String> getEntityData(Object entity, List<Field> fields){
		List<String> entityData = new ArrayList<String>();
		for(Field field : fields){
			Object value = null;			
			try {
				boolean isNotAccessible = false;
				if(!field.isAccessible()){ 
					field.setAccessible(true);
					isNotAccessible = true;
				}
				value = field.get(entity);
				if(value != null){
					if(value.toString() != null && !value.toString().isEmpty())
						entityData.add(value.toString());
					else
						entityData.add("null");
				}
				else
					entityData.add("null");
				if(isNotAccessible)
					field.setAccessible(false);	
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}			
		}
		return entityData;
	}
}