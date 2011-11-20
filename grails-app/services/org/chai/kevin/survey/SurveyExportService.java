package org.chai.kevin.survey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.chai.kevin.LanguageService;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.Translation;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.ValuePredicate;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.data.Type.Visitor;
import org.chai.kevin.survey.SurveyQuestion.QuestionType;
import org.chai.kevin.survey.export.SurveyExportData;
import org.chai.kevin.survey.export.SurveyExportDataHeader;
import org.chai.kevin.survey.export.SurveyExportDataItem;
import org.chai.kevin.survey.export.SurveyExportDataPoint;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.value.Value;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class SurveyExportService {

	private LanguageService languageService;
	private OrganisationService organisationService;
	private SurveyValueService surveyValueService;
	
	private Set<Integer> skipLevels;
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}
	
	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public void setSurveyValueService(SurveyValueService surveyValueService) {
		this.surveyValueService = surveyValueService;
	}

	public void setSkipLevels(Set<Integer> skipLevels) {
		this.skipLevels = skipLevels;
	}
	
	public Integer[] getSkipLevelArray() {
		return skipLevels.toArray(new Integer[skipLevels.size()]);
	}
	
	public String CSV_FILE_PREFIX_SURVEY = "SurveyExport_";
	public String CSV_FILE_PREFIX_OBJECTIVE = "ObjectiveExport_";
	public String CSV_FILE_PREFIX_SECTION = "SectionExport_";
	public String CSV_FILE_EXTENSION = ".csv";
	
	public String ZIP_FILE_PREFIX = "SurveyExport_";
	public String ZIP_FILE_EXTENSION = ".zip";	
	
	private String PERIOD_HEADER = "Year";
	private String ORGANISATION_UNIT_GROUP_HEADER = "Facility Type";
	private String SURVEY_HEADER = "Survey";
	private String OBJECTIVE_HEADER = "Objective";
	private String SECTION_HEADER = "Section";
	private String QUESTION_TYPE_HEADER = "Question Type";
	private static String QUESTION_DATA_TYPE_HEADER = "Data Type";
	private static String QUESTION_HEADER = "Question";
	private static String QUESTION_DATA_HEADER = "Name";
	private static String DATA_TYPE_HEADER = "Data Type";
	private static String DATA_NAME_HEADER = "Name";
	private static String DATA_VALUE_HEADER = "Value";		
	
	public List<SurveyExportDataHeader> getDefaultSurveyExportDataHeaders() {
		
		List<SurveyExportDataHeader> surveyExportDataHeaders = new ArrayList<SurveyExportDataHeader>();				
				
		surveyExportDataHeaders.add(new SurveyExportDataHeader(PERIOD_HEADER, surveyExportDataHeaders.size()));
		List<OrganisationUnitLevel> organisationUnitLevels = organisationService.getAllLevels(getSkipLevelArray());					
		for(OrganisationUnitLevel organisationUnitLevel : organisationUnitLevels){
			surveyExportDataHeaders.add(new SurveyExportDataHeader(organisationUnitLevel.getName(), surveyExportDataHeaders.size()));	
		}
		surveyExportDataHeaders.add(new SurveyExportDataHeader(ORGANISATION_UNIT_GROUP_HEADER, surveyExportDataHeaders.size()));				
		surveyExportDataHeaders.add(new SurveyExportDataHeader(SURVEY_HEADER, surveyExportDataHeaders.size()));
		surveyExportDataHeaders.add(new SurveyExportDataHeader(OBJECTIVE_HEADER, surveyExportDataHeaders.size()));
		surveyExportDataHeaders.add(new SurveyExportDataHeader(SECTION_HEADER, surveyExportDataHeaders.size()));
		surveyExportDataHeaders.add(new SurveyExportDataHeader(QUESTION_TYPE_HEADER, surveyExportDataHeaders.size()));
		surveyExportDataHeaders.add(new SurveyExportDataHeader(QUESTION_DATA_TYPE_HEADER, surveyExportDataHeaders.size()));
		surveyExportDataHeaders.add(new SurveyExportDataHeader(QUESTION_HEADER, surveyExportDataHeaders.size()));
//		surveyExportDataHeaders.add(new SurveyExportDataHeader(DATA_NAME_HEADER, surveyExportDataHeaders.size()));
//		surveyExportDataHeaders.add(new SurveyExportDataHeader(DATA_VALUE_HEADER, surveyExportDataHeaders.size()));
		
		return surveyExportDataHeaders;
	}	
	
//	public List<SurveyExportDataHeader> getAdditionalSurveyExportDataHeaders(List<SurveyExportDataHeader> surveyExportDataHeaders) {
//		surveyExportDataHeaders.add(new SurveyExportDataHeader(DATA_NAME_HEADER, surveyExportDataHeaders.size()));
//		return surveyExportDataHeaders;
//	}
	
	public File getSurveyExportZipFile(Organisation organisation, SurveySection section, SurveyObjective objective, Survey survey) {
		if(organisation == null || (section == null && objective == null && survey == null))
			return null;
		
		File csvFile = getSurveyExportFiles(organisation, section, objective, survey);
		
		if(!csvFile.exists()) return null;
		
		File zipFile = null;
		try {
			zipFile = File.createTempFile(ZIP_FILE_PREFIX, ZIP_FILE_EXTENSION);
		} catch (IOException e) {
		}
				
		byte[] buffer = new byte[1024];
		
		try {						
			
			FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
		    ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
		    	
	        FileInputStream fileInputStream = new FileInputStream(csvFile);
	        ZipEntry zipEntry = new ZipEntry(csvFile.getName());
	        zipOutputStream.putNextEntry(zipEntry);
	        
	        int length;
	        while ((length = fileInputStream.read(buffer)) > 0) {
	            zipOutputStream.write(buffer, 0, length);
	        }

	        zipOutputStream.closeEntry();
	        fileInputStream.close();

		    zipOutputStream.close();
		    
		} catch (IOException e) {
		}

		return zipFile;
	}
	
	public File getSurveyExportFiles(Organisation organisation, SurveySection section, SurveyObjective objective, Survey survey) { 
		if(organisation == null || (section == null && objective == null && survey == null))
			return null;
		
		SurveyExportSummary surveyExportSummary = getSurveyExportSummary(organisation, section, objective, survey);		
		
		Map<Organisation, SurveyExportData> facilityDataPoints = surveyExportSummary.getFacilityDataPoints();		
		Set<Organisation> facilities = facilityDataPoints.keySet();		
		
		String csvFilename = null;
		if(survey != null) csvFilename = CSV_FILE_PREFIX_SURVEY;
		if(objective != null) csvFilename = CSV_FILE_PREFIX_OBJECTIVE;
		if(section != null) csvFilename = CSV_FILE_PREFIX_SECTION;
		
		File csvFile = null;
		try {
			csvFile = File.createTempFile(csvFilename, CSV_FILE_EXTENSION);
		} catch (IOException e) {
		}
			
			try {
				FileWriter csvFileWriter = new FileWriter(csvFile);
				ICsvListWriter writer = new CsvListWriter(csvFileWriter, CsvPreference.EXCEL_PREFERENCE);
				String[] csvHeaders = null;
				
				for(Organisation facility : facilities){
					
					SurveyExportData surveyExportData = facilityDataPoints.get(facility);						
					
					List<SurveyExportDataHeader> dataHeaders = surveyExportData.getDataHeaders();
					List<SurveyExportDataPoint> dataPoints = surveyExportData.getDataPoints();
						
						// headers
						if(csvHeaders == null){
							Collections.sort(dataHeaders);
							csvHeaders = surveyExportData.getExportDataHeaders();
							writer.writeHeader(csvHeaders);
						}
						
						// data
						for (SurveyExportDataPoint dataPoint : dataPoints){
							List<SurveyExportDataItem> surveyExportDataItems = dataPoint;
							Collections.sort(surveyExportDataItems);
							List<? super Object> csvDataPoint = dataPoint.getExportDataPoint();
							CellProcessor[] nullCellProcessor = dataPoint.getExportCellProcessors();
							writer.write(csvDataPoint, nullCellProcessor);
						}
				}
				writer.close();
						
			} catch(IOException ioe){
				
			}		
		
		return csvFile;
	}
	
	public SurveyExportSummary getSurveyExportSummary(Organisation organisation, SurveySection section, SurveyObjective objective, Survey survey) {
		if(organisation == null || (section == null && objective == null && survey == null))
			return new SurveyExportSummary(null, null);
		
		//TODO sort facilities by province, district, facility
		List<Organisation> facilities = organisationService.getChildrenOfLevel(organisation, organisationService.getFacilityLevel());						
		Map<Organisation, SurveyExportData> facilityDataPoints = new HashMap<Organisation, SurveyExportData>();		
		for (Organisation facility : facilities) {			
			SurveyExportData surveyExportData = getSurveyExportData(facility, section, objective, survey);			
			facilityDataPoints.put(facility, surveyExportData);
		}		
		return new SurveyExportSummary(organisation, facilityDataPoints);		
	}
	
	public SurveyExportData getSurveyExportData(Organisation facility, SurveySection section, SurveyObjective objective, Survey survey) {
		if(facility == null || (section == null && objective == null && survey == null))
			return new SurveyExportData(null, null);		
		
		if(objective != null){
			survey = objective.getSurvey();
		}
		if(section != null){
			objective = section.getObjective();
			survey = section.getSurvey();
		}
			
		List<SurveyExportDataHeader> dataHeaders = getDefaultSurveyExportDataHeaders();
		List<SurveyExportDataPoint> dataPoints = new ArrayList<SurveyExportDataPoint>();
		
		List<SurveyObjective> surveyObjectives = survey.getObjectives();
		for (SurveyObjective surveyObjective : surveyObjectives) {
			if (objective != null && objective != surveyObjective) continue;
			List<SurveySection> surveySections = surveyObjective.getSections();
			for (SurveySection surveySection : surveySections) {
				if (section != null && section != surveySection) continue;
				List<SurveyQuestion> surveyQuestions = surveySection.getQuestions();
				for (SurveyQuestion surveyQuestion : surveyQuestions) {
					List<SurveyElement> surveyElements = surveyQuestion.getSurveyElements();
					List<SurveyExportDataPoint> surveyExportDataPoints = 
							getSurveyExportDataPoints(facility, survey, surveyObjective, surveySection, surveyQuestion, surveyElements);
					dataPoints.addAll(surveyExportDataPoints);
				}
			}
		}
		
//		int maxHeaders = dataHeaders.size();
//		for(SurveyExportDataPoint dataPoint : dataPoints){
//			 List<SurveyExportDataItem> surveyExportDataItems = dataPoint;
//			 int dataItems = surveyExportDataItems.size();
//			 if(dataItems > maxHeaders) maxHeaders = dataItems;
//		}
//		int additionalHeaders = maxHeaders - dataHeaders.size();
//		while(additionalHeaders > 0){
//			dataHeaders = getAdditionalSurveyExportDataHeaders(dataHeaders);
//			additionalHeaders--;
//		}
		
		return new SurveyExportData(dataHeaders, dataPoints);
	}	

	List<SurveyExportDataPoint> getSurveyExportDataPoints(Organisation facility, Survey survey, SurveyObjective surveyObjective, 
			SurveySection surveySection, SurveyQuestion surveyQuestion, List<SurveyElement> surveyElements){				
		if(facility == null || (surveySection == null && surveyObjective == null && survey == null))
			return new ArrayList<SurveyExportDataPoint>();
		
		List<SurveyExportDataPoint> dataPoints = new ArrayList<SurveyExportDataPoint>();						
		SurveyExportDataPoint dataPoint = null;
		
		if(surveyElements.size() > 1){			
			if(surveyQuestion.getType().equals(QuestionType.TABLE)){
				SurveyTableQuestion surveyTableQuestion = (SurveyTableQuestion) surveyQuestion;
				List<SurveyTableRow> surveyTableRows = surveyTableQuestion.getRows();
				List<SurveyTableColumn> surveyTableColumns = surveyTableQuestion.getColumns();
				
				for(int r = 0; r < surveyTableRows.size(); r++){
					for(int c = 0; c < surveyTableColumns.size(); c++){						
						
						SurveyElement surveyElement = surveyElements.get(r*surveyTableColumns.size() + c);
						
						dataPoint = getBasicInfoSurveyExportDataPoint(facility, survey, surveyObjective, surveySection, surveyQuestion, surveyElement);
						
						List<Object> surveyQuestionDataItems = new ArrayList<Object>();	
						String surveyQuestionRow = languageService.getText(surveyTableRows.get(r).getNames());						
						String surveyQuestionColumn = languageService.getText(surveyTableColumns.get(c).getNames());											
						surveyQuestionDataItems.add(surveyQuestionRow);
						surveyQuestionDataItems.add(surveyQuestionColumn);
						
						dataPoint = getSimpleSurveyExportDataPoint(dataPoint, facility, surveyQuestion, surveyElement, surveyQuestionDataItems);						
						dataPoints.add(dataPoint);
					}
				}
			}	
		}
		else{
			SurveyElement surveyElement = surveyElements.get(0);
			Type type = surveyElement.getDataElement().getType();
			ValueType valueType = type.getType();;
			
			if(valueType.equals(ValueType.MAP)
					|| (valueType.equals(ValueType.LIST) && type.getListType().getType().equals(ValueType.MAP))){
				
				List<SurveyExportDataPoint> complexDataPoints = new ArrayList<SurveyExportDataPoint>();								
				complexDataPoints = getComplexSurveyExportDataPoints(facility, survey, surveyObjective, surveySection, surveyQuestion, surveyElement);				
				dataPoints.addAll(complexDataPoints);				
			}
			else{				
				dataPoint = getBasicInfoSurveyExportDataPoint(facility, survey, surveyObjective, surveySection, surveyQuestion, surveyElement);
				dataPoint = getSimpleSurveyExportDataPoint(dataPoint, facility, surveyQuestion, surveyElement, null);
				dataPoints.add(dataPoint);
			}
		}		
		return dataPoints;
	}
	
	SurveyExportDataPoint getBasicInfoSurveyExportDataPoint(Organisation facility, Survey survey, SurveyObjective surveyObjective, 
			SurveySection surveySection, SurveyQuestion surveyQuestion, SurveyElement surveyElement){
		if(facility == null || (surveySection == null && surveyObjective == null && survey == null))
			return new SurveyExportDataPoint();
		
		SurveyExportDataPoint dataPoint = new SurveyExportDataPoint();			
		dataPoint.add(getSurveyExportDataItem(PERIOD_HEADER, survey.getPeriod().getName(), dataPoint.size()));		
		int facilityLevel = organisationService.loadLevel(facility);
		List<OrganisationUnitLevel> organisationUnitLevels = organisationService.getAllLevels(getSkipLevelArray());					
		for(OrganisationUnitLevel organisationUnitLevel : organisationUnitLevels){			
			if(facilityLevel == organisationUnitLevel.getLevel())
				dataPoint.add(getSurveyExportDataItem(organisationUnitLevel.getName(), facility.getOrganisationUnit().getName(), dataPoint.size()));
			else{
				Organisation parent = organisationService.getParentOfLevel(facility, new Integer(organisationUnitLevel.getLevel()));
				dataPoint.add(getSurveyExportDataItem(organisationUnitLevel.getName(), parent.getOrganisationUnit().getName(), dataPoint.size()));
			}
		}
		organisationService.loadGroup(facility);
		dataPoint.add(getSurveyExportDataItem(ORGANISATION_UNIT_GROUP_HEADER, facility.getOrganisationUnitGroup().getName(), dataPoint.size()));			
		dataPoint.add(getSurveyExportDataItem(SURVEY_HEADER, languageService.getText(survey.getNames()), dataPoint.size()));
		dataPoint.add(getSurveyExportDataItem(OBJECTIVE_HEADER, languageService.getText(surveyObjective.getNames()), dataPoint.size()));
		dataPoint.add(getSurveyExportDataItem(SECTION_HEADER, languageService.getText(surveySection.getNames()), dataPoint.size()));		
		dataPoint.add(getSurveyExportDataItem(QUESTION_TYPE_HEADER, surveyQuestion.getType().toString(), dataPoint.size()));
		
		DataElement dataElement = surveyElement.getDataElement();
		Type type = dataElement.getType();
		ValueType valueType = type.getType();
		
		String dataType = valueType.toString();
		if(valueType.equals(ValueType.LIST)) dataType = dataType + "<" + type.getListType().getType().toString()+ ">";
		dataPoint.add(getSurveyExportDataItem(QUESTION_DATA_TYPE_HEADER, dataType, dataPoint.size()));			
		dataPoint.add(getSurveyExportDataItem(QUESTION_HEADER, languageService.getText(surveyQuestion.getNames()), dataPoint.size()));
		
		return dataPoint;
	}
	
	SurveyExportDataPoint getSimpleSurveyExportDataPoint(SurveyExportDataPoint simpleDataPoint, Organisation facility, SurveyQuestion surveyQuestion, SurveyElement surveyElement, List<Object> surveyQuestionDataItems){
		if(simpleDataPoint == null || facility == null)
			return new SurveyExportDataPoint();
		
		DataElement dataElement = surveyElement.getDataElement();
		Type type = dataElement.getType();
		ValueType valueType = type.getType();
		
		SurveyEnteredValue surveyEnteredValue = surveyValueService.getSurveyEnteredValue(surveyElement, facility.getOrganisationUnit());
		
		if(surveyQuestionDataItems != null){
			for(Object surveyQuestionDataItem : surveyQuestionDataItems)
				simpleDataPoint.add(getSurveyExportDataItem(QUESTION_DATA_HEADER, surveyQuestionDataItem, simpleDataPoint.size()));
		}
		simpleDataPoint.add(getSurveyExportDataItem(DATA_TYPE_HEADER, valueType.toString(), simpleDataPoint.size()));
		simpleDataPoint.add(getSurveyExportDataItem(DATA_NAME_HEADER, languageService.getText(dataElement.getNames()), simpleDataPoint.size()));
		simpleDataPoint.add(getSurveyExportDataItem(DATA_VALUE_HEADER, surveyEnteredValue != null ? surveyEnteredValue.getValue() : null, simpleDataPoint.size()));
		
		return simpleDataPoint;
	}

	private List<SurveyExportDataPoint> getComplexSurveyExportDataPoints(Organisation facility, Survey survey, SurveyObjective surveyObjective, SurveySection surveySection,
			SurveyQuestion surveyQuestion, SurveyElement surveyElement) {

		List<SurveyExportDataPoint> complexDataPoints = new ArrayList<SurveyExportDataPoint>();
		
		DataElement dataElement = surveyElement.getDataElement();
		Type type = dataElement.getType();		
		SurveyEnteredValue surveyEnteredValue = surveyValueService.getSurveyEnteredValue(surveyElement, facility.getOrganisationUnit());		
		
		SurveyExportDataPoint complexDataPoint = getBasicInfoSurveyExportDataPoint(facility, survey, surveyObjective, surveySection, surveyQuestion, surveyElement);
		if(surveyEnteredValue != null){
			ComplexDataPointVisitor complexDataPointVisitor = new ComplexDataPointVisitor(complexDataPoint);
			type.visit(surveyEnteredValue.getValue(), complexDataPointVisitor);
			complexDataPoints = complexDataPointVisitor.getComplexDataPoints();		
		}
		else{
			complexDataPoint = getSimpleSurveyExportDataPoint(complexDataPoint, facility, surveyQuestion, surveyElement, null);			
			complexDataPoints.add(complexDataPoint);
		}
		return complexDataPoints;
	}

	static SurveyExportDataItem getSurveyExportDataItem(String key, Object value, int order){		
		SurveyExportDataItem surveyExportDataItem = new SurveyExportDataItem(order);
		surveyExportDataItem.put(key, value);
		return surveyExportDataItem;
	}
	
	public static class ComplexDataPointVisitor extends Visitor{

		private List<SurveyExportDataPoint> complexDataPoints;		
		private SurveyExportDataPoint baseComplexDataPoint;
		
		public List<SurveyExportDataPoint> getComplexDataPoints(){
			return complexDataPoints;
		}
		
		public ComplexDataPointVisitor(SurveyExportDataPoint baseComplexDataPoint) {
			complexDataPoints = new ArrayList<SurveyExportDataPoint>();
			this.baseComplexDataPoint = baseComplexDataPoint;		
		}
		
		@Override
		public void handle(Type type, Value value, String prefix) {
			if(!type.isComplexType()){
				ArrayList<SurveyExportDataItem> surveyExportDataItems = baseComplexDataPoint;				
				SurveyExportDataPoint complexDataPoint = new SurveyExportDataPoint();
				complexDataPoint.addAll(surveyExportDataItems);

				List<Object> surveyQuestionDataItems = new ArrayList<Object>();
				for(String typeKey : this.getTypes().keySet()){
					surveyQuestionDataItems.add(typeKey);
				}
				
				if(surveyQuestionDataItems != null){
					for(Object surveyQuestionDataItem : surveyExportDataItems)
						complexDataPoint.add(getSurveyExportDataItem(QUESTION_DATA_HEADER, surveyQuestionDataItem, complexDataPoint.size()));
				}
				complexDataPoint.add(getSurveyExportDataItem(DATA_TYPE_HEADER, type, complexDataPoint.size()));
				complexDataPoint.add(getSurveyExportDataItem(DATA_NAME_HEADER, prefix, complexDataPoint.size()));
				complexDataPoint.add(getSurveyExportDataItem(DATA_VALUE_HEADER, value, complexDataPoint.size()));
			}
		}
		
	}
}
