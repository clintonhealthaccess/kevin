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
package org.chai.kevin.export

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LocationService;
import org.chai.kevin.Period;
import org.chai.kevin.Translation;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.data.Type.ValueVisitor;
import org.chai.kevin.importer.ImporterError;
import org.chai.kevin.location.CalculationLocation
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.RawDataElementValue
import org.chai.kevin.value.Value
import org.chai.kevin.value.ValueService;

import org.apache.commons.lang.StringUtils
import org.hibernate.Criteria;
import org.chai.kevin.survey.export.SurveyExportDataPoint;
import org.chai.kevin.util.Utils
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Order
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

/**
 * @author Jean Kahigiso M.
 *
 */
class ExporterService {
	
	static transactional = true
	def languageService;
	def locationService;
	def valueService;
	def dataService;
	def sessionFactory;
	def surveyExportService;
	
	private final static String CSV_FILE_EXTENSION = ".csv";
	private final static String LOCATION_TYPE = "Type";
	private final static String HEALTH_FACILITY_CODE = "Health Facility Code";
	private final static String HEALTH_FACILITY = "Health Facility";
	private final static String DATA_ELEMENT_NAME = "Data Element Name";
	private final static String DATA_ELEMENT_CODE = "Data Element Code";
	private final static String PERIOD = "Period";
	private final static String DATA_VALUE = "Data Value";
	private final static String DATA_VALUE_ADDRESS = "Data Value Address";
	
	public File exportDataElement(Exporter export){
		if (log.isDebugEnabled()) log.debug("exportData("+export+")");
		Set<DataLocationType> types = new HashSet();
		
		for(String code: export.getTypeCodes()){
			def type = locationService.findDataLocationTypeByCode(code)
			if(type!=null) types.add(type)
		}
		
		List<DataLocation> dataLocations = locationService.getDataLocations(export.locations,types);
		
		if (log.isDebugEnabled()) log.debug(" Exporter dataLocations "+dataLocations+")");
		return this.exportDataElement(export.descriptions[languageService.getCurrentLanguage()],dataLocations,export.periods,export.data);
	}
		
	public File exportDataElement(String fileName,List<DataLocation> dataLocations,Set<Period> periods,Set<Data<DataValue>> data){
		if (log.isDebugEnabled()) log.debug(" exportDataElement(String "+fileName+" List<DataLocation>: " + dataLocations + " List<Period>: "+ periods + " Set<Data<DataValue>>: " + data + ")");
		File csvFile = File.createTempFile(fileName, CSV_FILE_EXTENSION);
		FileWriter csvFileWriter = new FileWriter(csvFile);
		ICsvListWriter writer = new CsvListWriter(csvFileWriter, CsvPreference.EXCEL_PREFERENCE);
		DataPointVisitor dataPointVisitor = new DataPointVisitor(writer);

		try{
			String[] csvHeaders = null;
			// headers
			if(csvHeaders == null){
				csvHeaders = getExportDataHeaders();
				writer.writeHeader(csvHeaders);
			}
			for(DataLocation location: dataLocations)
				for(Period period: periods)
					for(Data dataElement: data){
						List<String> basicInfo = this.getBasicInfo(location,period,dataElement);
						RawDataElementValue rawDataElementValue = valueService.getDataElementValue(dataElement, location, period);
						dataPointVisitor.setBasicInfo(basicInfo);
						if(rawDataElementValue!=null)
							dataElement.getType().visit(rawDataElementValue.getValue(), dataPointVisitor);
					}
		} catch (IOException ioe){
			// TODO throw something that make sense
			throw ioe;
		} finally {
			writer.close();
		}
		return csvFile;

	}
	
	private List<String> getBasicInfo(DataLocation location,Period period, Data data){
		def basicInfo=[]
		for (LocationLevel level : locationService.listLevels()){
			Location parent = locationService.getParentOfLevel(location, level);
			if (parent != null) basicInfo.add(languageService.getText(parent.getNames()));
			else basicInfo.add("");
		}
		basicInfo.add(languageService.getText(location.getNames()))
		basicInfo.add(languageService.getText(location.type.getNames()))

		basicInfo.add(location.code)
		basicInfo.add("[ "+period.startDate.toString()+" - "+period.endDate.toString()+" ]")
		basicInfo.add(languageService.getText(data.getNames()))
		basicInfo.add(data.code)
		return basicInfo;
	}
	
	private List getExportDataHeaders() {
		List<String> headers = new ArrayList<String>();
		for (LocationLevel level : locationService.listLevels())
			headers.add(languageService.getText(level.getNames()));
		headers.add(HEALTH_FACILITY);
		headers.add(LOCATION_TYPE);
		headers.add(HEALTH_FACILITY_CODE);
		headers.add(PERIOD);
		headers.add(DATA_ELEMENT_NAME);
		headers.add(DATA_ELEMENT_CODE);
		headers.add(DATA_VALUE);
		headers.add(DATA_VALUE_ADDRESS);
		return headers;
	}
	
	public List<Exporter> getExporters(def sorter, def order){
		return Exporter.list(sort:sorter,order:order);
	}
	public List<Exporter> getExporters(){
		return Exporter.list();
	}
	public Integer countExporter(Class<Exporter> clazz, String text) {
		return getSearchCriteria(clazz,text).setProjection(Projections.count("id")).uniqueResult()
	}

	public <T extends Exporter> List<T>  searchExporter(Class<T> clazz, String text, Map<String, String> params) {
		def exporters=[]
		def criteria = getSearchCriteria(clazz,text)

		if (params['offset'] != null) criteria.setFirstResult(params['offset'])
		if (params['max'] != null) criteria.setMaxResults(params['max'])

		if(params['sort']!=null)
			exporters= criteria.addOrder(Order.asc(params['sort'])).list()
		else
			exporters= criteria.addOrder(Order.desc("date")).list()

		StringUtils.split(text).each { chunk ->
			exporters.retainAll { exporter ->
				Utils.matches(chunk, exporter.descriptions[languageService.getCurrentLanguage()]);
			}
		}

		return exporters;
	}
	private Criteria getSearchCriteria(Class<Exporter> clazz, String text) {
		def criteria = sessionFactory.getCurrentSession().createCriteria(clazz);
		def textRestrictions = Restrictions.conjunction()
		StringUtils.split(text).each { chunk ->
			def disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.ilike("descriptions.jsonText", chunk, MatchMode.ANYWHERE))
			textRestrictions.add(disjunction)
		}
		criteria.add(textRestrictions)
		return criteria
	}
	
	
}
private class DataPointVisitor extends ValueVisitor{
	private List<String> basicInfo = new ArrayList<String>();
	private ICsvListWriter writer;

	public DataPointVisitor(ICsvListWriter writer){
		this.writer=writer;
	}

	@Override
	public void handle(Type type, Value value, String prefix, String genericPrefix) {
		if(!type.isComplexType()){
			List<String> dataList = new ArrayList<String>(basicInfo);
			dataList.add(Utils.getValueString(type, value));
			dataList.add(prefix)
			writer.write(dataList)
		}
	}
	public void setBasicInfo(List<String> basicInfo){
		this.basicInfo=basicInfo
	}
}

