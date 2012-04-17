package org.chai.kevin.entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LanguageService;
import org.chai.kevin.LocationService;
import org.chai.kevin.LocationSorter;
import org.chai.kevin.Translation;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.data.Type.ValueVisitor;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportEntity;
import org.chai.kevin.survey.export.SurveyExportDataPoint;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
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
			String[] csvHeaders = null;
			List<String> entityHeaders = new ArrayList<String>();			
			Class headerClass = clazz;
			while(headerClass != null && headerClass != Object.class){				
				Field[] classFields = headerClass.getDeclaredFields();
				entityHeaders.addAll(getEntityHeaders(classFields));
				headerClass = headerClass.getSuperclass();
			}			
			if(entityHeaders.toArray(new String[0]) != null)
				writer.writeHeader(entityHeaders.toArray(new String[0]));
			
			//entities
			List<Object> entities = getEntities(clazz);
			for(Object entity : entities){
				if (log.isDebugEnabled()) log.debug("getExportFile(entity="+entity+")");				
				List<String> entityData = new ArrayList<String>();			
				Class dataClass = clazz;
				while(dataClass != null && dataClass != Object.class){
					Field[] classFields = dataClass.getDeclaredFields();
					entityData.addAll(getEntityData(entity, dataClass, classFields));
					dataClass = dataClass.getSuperclass();
				}				
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
	
	public List<String> getEntityHeaders(Field[] fields){
		List<String> headers = new ArrayList<String>();
		for(Field field : fields){
			String fieldName = field.getName();
			headers.add(fieldName);
		}
		return headers;
	}
	
	public List getEntities(Class clazz){
		List<Object> entities = new ArrayList<Object>();
		entities = (List<Object>) sessionFactory.getCurrentSession().createCriteria(clazz).list();
		return entities;
	}
	
	public List<String> getEntityData(Object entity, Class clazz, Field[] fields){
		List<String> csvData = new ArrayList<String>();
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
					Class valueClass = value.getClass();
					if(value.toString() != null && !value.toString().isEmpty())
						csvData.add(value.toString());
					else
						csvData.add("");
				}
				else
					csvData.add(null);
				if(isNotAccessible)
					field.setAccessible(false);	
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}			
		}
		return csvData;
	}
}