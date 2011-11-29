package org.chai.kevin.survey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LanguageService;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.OrganisationSorter;
import org.chai.kevin.Translation;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.data.Type.Visitor;
import org.chai.kevin.survey.export.SurveyExportData;
import org.chai.kevin.survey.export.SurveyExportDataPoint;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class SurveyExportService {
	private static final Log log = LogFactory.getLog(SurveyExportService.class);
	
//	private GrailsApplication grailsApplication;
	private SessionFactory sessionFactory;
	private LanguageService languageService;
	private OrganisationService organisationService;
	private SurveyValueService surveyValueService;
	
	private Set<Integer> skipLevels;
	
//	// for internal call through transactional proxy
//	public SurveyExportService getMe() {
//		return grailsApplication.getMainContext().getBean(SurveyExportService.class);
//	}
//	
//	public void setGrailsApplication(GrailsApplication grailsApplication) {
//		this.grailsApplication = grailsApplication;
//	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}
	
	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public void setSurveyValueService(SurveyValueService surveyValueService) {
		this.surveyValueService = surveyValueService;
	}

	private final static String CSV_FILE_EXTENSION = ".csv";
	
	public void setSkipLevels(Set<Integer> skipLevels) {
		this.skipLevels = skipLevels;
	}
	
	public Integer[] getSkipLevelArray() {
		return skipLevels.toArray(new Integer[skipLevels.size()]);
	}		
	
	private final static String ORGANISATION_UNIT_GROUP_HEADER = "Facility Type";
	private final static String SURVEY_HEADER = "Survey";
	private final static String OBJECTIVE_HEADER = "Objective";
	private final static String SECTION_HEADER = "Section";
	private final static String QUESTION_TYPE_HEADER = "Question Type";
	private final static String QUESTION_DATA_TYPE_HEADER = "Data Type";
	private final static String QUESTION_HEADER = "Question";
	private final static String DATA_VALUE_HEADER = "Value";	
	
	private String[] getExportDataHeaders() {
		List<String> headers = new ArrayList<String>();
		
		headers.add(SURVEY_HEADER);
		
		List<OrganisationUnitLevel> organisationUnitLevels = organisationService.getAllLevels(getSkipLevelArray());					
		for(OrganisationUnitLevel organisationUnitLevel : organisationUnitLevels){
			headers.add(organisationUnitLevel.getName());
		}
		headers.add(ORGANISATION_UNIT_GROUP_HEADER);
		headers.add(OBJECTIVE_HEADER);
		headers.add(SECTION_HEADER);
		headers.add(QUESTION_TYPE_HEADER);
		headers.add(QUESTION_DATA_TYPE_HEADER);
		headers.add(QUESTION_HEADER);
		headers.add(DATA_VALUE_HEADER);
		return headers.toArray(new String[0]);
	}	
	
	private String getExportFilename(Organisation organisation, SurveySection section, SurveyObjective objective, Survey survey){
		Translation translation = null;
		if(survey != null) translation = survey.getNames();
		if(objective != null) translation = objective.getNames();
		if(section != null) translation = section.getNames();
		String exportFilename = languageService.getText(translation).replaceAll("[^a-zA-Z0-9]", "") + "_" + 
								organisation.getName().replaceAll("[^a-zA-Z0-9]", "") + "_";
		return exportFilename;
	}
	
	@Transactional(readOnly=true)
	public File getSurveyExportFile(Organisation organisation, SurveySection section, SurveyObjective objective, Survey survey) throws IOException { 
		
		List<Organisation> facilities = organisationService.getChildrenOfLevel(organisation, organisationService.getFacilityLevel());
		Collections.sort(facilities, OrganisationSorter.BY_LEVEL);
		
		String csvFilename = getExportFilename(organisation, section, objective, survey);		
		File csvFile = File.createTempFile(csvFilename, CSV_FILE_EXTENSION);
		
		FileWriter csvFileWriter = new FileWriter(csvFile);
		ICsvListWriter writer = new CsvListWriter(csvFileWriter, CsvPreference.EXCEL_PREFERENCE);
		try {
			String[] csvHeaders = null;
			
			// headers
			if(csvHeaders == null){
				csvHeaders = getExportDataHeaders();
				writer.writeHeader(csvHeaders);
			}
			
			for(Organisation facility : facilities){	
//				facility = organisationService.getOrganisation(facility.getId());
//				if(survey != null) survey = (Survey)sessionFactory.getCurrentSession().load(Survey.class, survey.getId());
//				if(objective != null) objective = (SurveyObjective)sessionFactory.getCurrentSession().load(SurveyObjective.class, objective.getId());
//				if(section != null) section = (SurveySection)sessionFactory.getCurrentSession().load(SurveySection.class, section.getId());
//				
				if (log.isDebugEnabled()) log.debug("getSurveyExportFile(facility="+facility.getName()+")");
				
//				SurveyExportData surveyExportData = getMe().getSurveyExportDataInTransaction(facility, section, objective, survey);
				SurveyExportData surveyExportData = getSurveyExportData(facility, section, objective, survey);
				List<SurveyExportDataPoint> dataPoints = surveyExportData.getDataPoints();						
					
				// data
				for (SurveyExportDataPoint dataPoint : dataPoints){
					writer.write(dataPoint);
				}
//				sessionFactory.getCurrentSession().clear();
			}
		} catch (IOException ioe){
			// TODO is this good ?
			throw ioe;
		} finally {
			writer.close();
		}
		
		return csvFile;
	}	
	
//	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
//	public SurveyExportData getSurveyExportDataInTransaction(Organisation facility, SurveySection section, SurveyObjective objective, Survey survey) {	
//		return getSurveyExportData(facility, section, objective, survey);
//	}
	
	public SurveyExportData getSurveyExportData(Organisation facility, SurveySection section, SurveyObjective objective, Survey survey) {	
//		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
//		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		
		if(objective != null){
			survey = objective.getSurvey();
		}
		if(section != null){
			objective = section.getObjective();
			survey = section.getSurvey();
		}
			
		List<SurveyExportDataPoint> dataPoints = new ArrayList<SurveyExportDataPoint>();
		
		organisationService.loadGroup(facility);
		
		List<SurveyObjective> surveyObjectives = survey.getObjectives(facility.getOrganisationUnitGroup());
		Collections.sort(surveyObjectives);
		for (SurveyObjective surveyObjective : surveyObjectives) {
			if (objective != null && objective != surveyObjective) continue;						
			List<SurveySection> surveySections = surveyObjective.getSections(facility.getOrganisationUnitGroup());
			Collections.sort(surveySections);
			for (SurveySection surveySection : surveySections) {
				if (section != null && section != surveySection) continue;
				
				List<SurveyEnteredValue> surveyEnteredValues = surveyValueService.getSurveyEnteredValues(facility.getOrganisationUnit(), section, objective, survey);					
				Map<SurveyElement, SurveyEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, SurveyEnteredValue>();
				for(SurveyEnteredValue surveyEnteredValue : surveyEnteredValues){
					surveyElementValueMap.put(surveyEnteredValue.getSurveyElement(), surveyEnteredValue);
				}
				
				List<SurveyQuestion> surveyQuestions = surveySection.getQuestions(facility.getOrganisationUnitGroup());				
				Collections.sort(surveyQuestions);
				for (SurveyQuestion surveyQuestion : surveyQuestions) {
					if (log.isDebugEnabled()){
						log.debug("getSurveyExportData(" + 
									" question="+languageService.getText(surveyQuestion.getNames()) +
									" section="+languageService.getText(surveySection.getNames()) +
									" objective="+languageService.getText(surveyObjective.getNames()) + 
									" facility="+facility.getName() + ")");
					}
					List<SurveyExportDataPoint> surveyExportDataPoints = 
							getSurveyExportDataPoints(facility, survey, surveyObjective, surveySection, surveyQuestion, surveyElementValueMap);
					dataPoints.addAll(surveyExportDataPoints);
				}
			}
		}
		return new SurveyExportData(dataPoints);
	}

	private List<SurveyExportDataPoint> getSurveyExportDataPoints(Organisation facility, Survey survey, SurveyObjective surveyObjective, 
			SurveySection surveySection, SurveyQuestion surveyQuestion, Map<SurveyElement, SurveyEnteredValue> surveyElementValueMap){				
		
		List<SurveyExportDataPoint> surveyExportDataPoints = new ArrayList<SurveyExportDataPoint>();						
		
		switch(surveyQuestion.getType()){					
		
			case TABLE:
				SurveyTableQuestion surveyTableQuestion = (SurveyTableQuestion) surveyQuestion;
				List<SurveyTableRow> surveyTableRows = surveyTableQuestion.getRows(facility.getOrganisationUnitGroup());
				List<SurveyTableColumn> surveyTableColumns = surveyTableQuestion.getColumns(facility.getOrganisationUnitGroup());
				
				for (SurveyTableRow surveyTableRow : surveyTableRows) {
					for(SurveyTableColumn surveyTableColumn : surveyTableColumns){						
						SurveyElement surveyElement = surveyTableRow.getSurveyElements().get(surveyTableColumn);						
						List<String> surveyQuestionItems = new ArrayList<String>();	
						String surveyQuestionRow = languageService.getText(surveyTableRow.getNames());						
						String surveyQuestionColumn = languageService.getText(surveyTableColumn.getNames());											
						surveyQuestionItems.add(surveyQuestionRow);
						surveyQuestionItems.add(surveyQuestionColumn);						
						addDataPoints(facility, survey, surveyObjective, surveySection, surveyQuestion, surveyExportDataPoints, surveyElement, surveyQuestionItems, surveyElementValueMap);
					}
				}
				break;
			case CHECKBOX:
				SurveyCheckboxQuestion surveyCheckboxQuestion = (SurveyCheckboxQuestion) surveyQuestion;
				List<SurveyCheckboxOption> surveyCheckboxOptions = surveyCheckboxQuestion.getOptions(facility.getOrganisationUnitGroup());
				for(SurveyCheckboxOption surveyCheckboxOption : surveyCheckboxOptions){
					SurveyElement surveyElement = surveyCheckboxOption.getSurveyElement();					
					List<String> surveyQuestionItems = new ArrayList<String>();						
					String surveyCheckboxName = languageService.getText(surveyCheckboxOption.getNames());																	
					surveyQuestionItems.add(surveyCheckboxName);						
					addDataPoints(facility, survey, surveyObjective, surveySection, surveyQuestion, surveyExportDataPoints, surveyElement, surveyQuestionItems, surveyElementValueMap);						
				}
				break;
			case SIMPLE:
				SurveySimpleQuestion surveySimpleQuestion = (SurveySimpleQuestion) surveyQuestion;
				SurveyElement surveyElement = surveySimpleQuestion.getSurveyElement();				
				addDataPoints(facility, survey, surveyObjective, surveySection, surveyQuestion, surveyExportDataPoints, surveyElement, null, surveyElementValueMap);
				break;
			default:
				throw new NotImplementedException();	
		}
		return surveyExportDataPoints;
	}

	private void addDataPoints(Organisation facility, Survey survey, SurveyObjective surveyObjective, SurveySection surveySection, SurveyQuestion surveyQuestion,
			List<SurveyExportDataPoint> surveyExportDataPoints, SurveyElement surveyElement, List<String> surveyQuestionItems, Map<SurveyElement, SurveyEnteredValue> surveyElementValueMap) {
		if(surveyElement == null)
			surveyExportDataPoints.add(getBasicInfoDataPoint(facility, survey, surveyObjective, surveySection, surveyQuestion, null));
		else{
			if (log.isDebugEnabled()) log.debug("getSurveyExportDataPoints(type="+surveyElement.getDataElement().getType().getType()+")");
			
			SurveyExportDataPoint dataPoint = getBasicInfoDataPoint(facility, survey, surveyObjective, surveySection, surveyQuestion, surveyElement);
			List<SurveyExportDataPoint> dataPoints = new ArrayList<SurveyExportDataPoint>();
						
			DataElement dataElement = surveyElement.getDataElement();
			Type type = dataElement.getType();
			
			SurveyEnteredValue surveyEnteredValue = surveyElementValueMap.get(surveyElement);

			if(surveyEnteredValue != null){			
				Map<String, Translation> headers = surveyElement.getHeaders();
				DataPointVisitor dataPointVisitor = new DataPointVisitor(headers, surveyQuestionItems, dataPoint);
				type.visit(surveyEnteredValue.getValue(), dataPointVisitor);
				dataPoints = dataPointVisitor.getDataPoints();	
			}
			else{			
				dataPoint.add("null");
				dataPoints.add(dataPoint);
			}
			
			sessionFactory.getCurrentSession().evict(surveyEnteredValue);
			
			surveyExportDataPoints.addAll(dataPoints);
		}
	}
	
	private SurveyExportDataPoint getBasicInfoDataPoint(Organisation facility, Survey survey, SurveyObjective surveyObjective, 
			SurveySection surveySection, SurveyQuestion surveyQuestion, SurveyElement surveyElement){
		
		SurveyExportDataPoint dataPoint = new SurveyExportDataPoint();
		dataPoint.add(formatExportDataItem(languageService.getText(survey.getNames())));
		
		int facilityLevel = organisationService.loadLevel(facility);
		List<OrganisationUnitLevel> organisationUnitLevels = organisationService.getAllLevels(getSkipLevelArray());					
		for(OrganisationUnitLevel organisationUnitLevel : organisationUnitLevels){			
			if(facilityLevel == organisationUnitLevel.getLevel())
				dataPoint.add(formatExportDataItem(facility.getOrganisationUnit().getName()));
			else{
				Organisation parent = organisationService.getParentOfLevel(facility, new Integer(organisationUnitLevel.getLevel()));
				dataPoint.add(formatExportDataItem(parent.getOrganisationUnit().getName()));
			}
		}
		
		organisationService.loadGroup(facility);
		dataPoint.add(formatExportDataItem(facility.getOrganisationUnitGroup().getName()));			
		dataPoint.add(formatExportDataItem(languageService.getText(surveyObjective.getNames())));
		dataPoint.add(formatExportDataItem(languageService.getText(surveySection.getNames())));		
		dataPoint.add(formatExportDataItem(surveyQuestion.getType().toString()));
		
		if(surveyElement != null){
			DataElement dataElement = surveyElement.getDataElement();
			Type type = dataElement.getType();
			ValueType valueType = type.getType();
			
			String dataType = valueType.toString();	
			dataPoint.add(formatExportDataItem(dataType));		
			dataPoint.add(formatExportDataItem(languageService.getText(surveyQuestion.getNames())));	
		}
		
		return dataPoint;
	}

	private void addDataPointValue(SurveyExportDataPoint surveyExportDataPoint, Type dataType, Value dataValue){						
		if(dataValue == null || dataValue.isNull()) {
			surveyExportDataPoint.add(formatExportDataItem(null));
		}
		
		if(dataValue != null && !dataValue.isNull()){
			String value = null;	
			
			switch (dataType.getType()) {
			case NUMBER:
				value = dataValue.getNumberValue().toString();
				break;
			case BOOL:
				value = dataValue.getBooleanValue().toString();
				break;
			case STRING:
				value = dataValue.getStringValue();
				break;
			case TEXT:
				value = dataValue.getStringValue();
				break;
			case DATE:
				if(dataValue.getDateValue() != null){
					//TODO this should never be null!
					value = dataValue.getDateValue().toString();	
				}
				break;
			case ENUM:
				value = dataValue.getEnumValue();
				break;
			default:
				break;
			}			
			surveyExportDataPoint.add(formatExportDataItem(value));
		}
	}
	
	private String formatExportDataItem(String value){		
		if (value != null) value = Utils.stripHtml(value, value.length());
		if (value == null) value = "null";
		return value;
	}
	
	
	private class DataPointVisitor extends Visitor{

		private Map<String, Translation> headers;
		private List<String> surveyQuestionItems;
		private SurveyExportDataPoint baseDataPoint;
		private List<SurveyExportDataPoint> dataPoints;	
		
		public List<SurveyExportDataPoint> getDataPoints(){
			return dataPoints;
		}
		
		public DataPointVisitor(Map<String, Translation> headers, List<String> surveyQuestionItems, SurveyExportDataPoint baseDataPoint) {			
			this.headers = headers;
			this.surveyQuestionItems = surveyQuestionItems;			
			this.baseDataPoint = baseDataPoint;
			dataPoints = new ArrayList<SurveyExportDataPoint>();
		}

		@Override
		public void handle(Type type, Value value, String prefix, String genericPrefix) {				
			SurveyExportDataPoint dataPoint = new SurveyExportDataPoint(baseDataPoint);						
			if(!type.isComplexType()){
				addDataPointValue(dataPoint, type, value);
				if(surveyQuestionItems != null){
					for(String surveyQuestionItem : surveyQuestionItems)
						dataPoint.add(formatExportDataItem(surveyQuestionItem));				
				}
				for(String genericTypeKey : this.getGenericTypes().keySet()){
					if(!this.getGenericTypes().get(genericTypeKey).getType().equals(ValueType.LIST)){
						String surveyQuestionItem = languageService.getText(headers.get(genericTypeKey));
						if(surveyQuestionItem != null) dataPoint.add(formatExportDataItem(surveyQuestionItem));
					}
				}
				dataPoints.add(dataPoint);
			}
		}		
	}
}
