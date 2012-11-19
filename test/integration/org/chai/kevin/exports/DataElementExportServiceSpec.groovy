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
package org.chai.kevin.exports

import java.text.SimpleDateFormat
import java.util.List;
import java.util.Set;

import org.apache.commons.io.output.StringBuilderWriter;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.Period;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.exports.DataElementExport;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.kevin.util.ImportExportConstant;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Status;
import org.chai.kevin.value.Value;
import org.supercsv.io.CsvListWriter
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

/**
 * @author Jean Kahigiso M.
 *
 */
class DataElementExportServiceSpec extends IntegrationTests {
	
	def dataElementExportService;
	def locationService;
	
	def "test exportData(exporter) return file"(){
		setup:
		setupLocationTree();
		def period = newPeriod()
		
		def dataElementOne = newRawDataElement(CODE(1), Type.TYPE_NUMBER());
		def dataElementTwo = newRawDataElement(CODE(2), Type.TYPE_BOOL());
		newRawDataElementValue(dataElementOne, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(1))
		
		def locations = s([Location.findByCode(BURERA), DataLocation.findByCode(KIVUYE)]);
		def dataElements = new HashSet([dataElementOne,dataElementTwo]);
		def exporter = newDataElementExport(CODE(1), ["en":"Testing Seach One"], s([period]), [HEALTH_CENTER_GROUP,DISTRICT_HOSPITAL_GROUP], locations, dataElements);
		
		when:
		def exportedFile = dataElementExportService.exportData(exporter);
		def lines = IntegrationTests.readLines(exportedFile)
		
		then:
		lines[0] == [NATIONAL, PROVINCE, DISTRICT, SECTOR, ImportExportConstant.DATA_LOCATION_CODE, ImportExportConstant.DATA_LOCATION_NAME,
			ImportExportConstant.LOCATION_TYPE, ImportExportConstant.PERIOD_CODE, ImportExportConstant.PERIOD, ImportExportConstant.DATA_CLASS,
			ImportExportConstant.DATA_CODE, ImportExportConstant.DATA_NAME, ImportExportConstant.DATA_VALUE, ImportExportConstant.DATA_VALUE_ADDRESS].join(',')
		lines[1] == ["Rwanda", "North", "Burera", "", BUTARO, "Butaro DH", DISTRICT_HOSPITAL_GROUP, period.code, 
			"[ "+(period.startDate).toString()+" - "+(period.endDate).toString()+" ]", RawDataElement.class.simpleName, dataElementOne.code, 
			'', Value.VALUE_NUMBER(1).numberValue, ''].join(',')
	}
	
	def "test exportDataElements() return file"(){
		setup:
		setupLocationTree();
		def periods=new HashSet([newPeriod()]);
		def locationTypes = [HEALTH_CENTER_GROUP,DISTRICT_HOSPITAL_GROUP];
		
		def dataElementOne = newRawDataElement(CODE(1), Type.TYPE_NUMBER());
		
		def locations = s([Location.findByCode(BURERA), DataLocation.findByCode(KIVUYE)]);
		def dataElements=new HashSet([dataElementOne]);
		def exporterOne = newDataElementExport(CODE(1), ["en":"Testing Seach One"],periods, locationTypes, locations, dataElements);
		
		when:
		def exportedFileOne = dataElementExportService.exportDataElements("Testing", [DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)], exporterOne.periods as List, exporterOne.dataElements as List);
		
		then:
		exportedFileOne!=null
	}
	
	def "test exportDataElements() with valid Map Type DataElement"(){
		setup:
		setupLocationTree();
		def period= newPeriod();
		def locations = [DataLocation.findByCode(KIVUYE)]
		
		def typeListMap = Type.TYPE_LIST(Type.TYPE_MAP(["key1": Type.TYPE_STRING()]));
		def dataElementMap = newRawDataElement(CODE(1), typeListMap);
		RawDataElementValue valueMap = newRawDataElementValue(dataElementMap,period,locations[0],Value.VALUE_LIST([Value.VALUE_MAP(["key1":Value.VALUE_STRING("value")])]));
				
		when:
		def lines = dataElementExportService.getExportLineForValue(locations[0],period,dataElementMap)
		def periodString = "[ "+(period.startDate).toString()+" - "+(period.endDate).toString()+" ]";
		def listDataList=["Rwanda","North","Burera",""]
		listDataList.add(locations[0].code);
		listDataList.add(locations[0].names_en);
		listDataList.add(locations[0].type.names_en);
		listDataList.add(period.code);
		listDataList.add(periodString);
		listDataList.add(dataElementMap.class.simpleName);
		listDataList.add(dataElementMap.code);
		listDataList.add("");
		listDataList.add("value");
		listDataList.add("[0].key1");
		
		def listOfList=[]
		listOfList.add(listDataList)
		
		then:
		lines.equals(listOfList);
	}
	
	def "test exportDataElements() with valid Simple RawDataElement Type"(){
		setup:
		setupLocationTree();
		def date= new Date();
		def period= newPeriod();
		def locations = [DataLocation.findByCode(KIVUYE)]
		
		def typeDate = Type.TYPE_DATE();
		def dataElementDate = newRawDataElement(CODE(1), typeDate);
		
		RawDataElementValue valueDate = newRawDataElementValue(dataElementDate,period,locations[0],Value.VALUE_DATE(date));
		
		when:
		def lines = dataElementExportService.getExportLineForValue(locations[0],period,dataElementDate)
		def periodString = "[ "+(period.startDate).toString()+" - "+(period.endDate).toString()+" ]";
		def listDataList=["Rwanda","North","Burera",""]
		listDataList.add(locations[0].code);
		listDataList.add(locations[0].names_en);
		listDataList.add(locations[0].type.names_en);
		listDataList.add(period.code);
		listDataList.add(periodString);
		listDataList.add(dataElementDate.class.simpleName);
		listDataList.add(dataElementDate.code);
		listDataList.add("");
		listDataList.add(Utils.formatDate(date));
		listDataList.add("");
		def listOfList=[]
		listOfList.add(listDataList)
		
		then:
		lines.equals(listOfList);
	}
	
	def "test exportDataElements() with valid Simple NormalizedDataElement Type"(){
		setup:
		setupLocationTree();
		def date= new Date();
		def period= newPeriod();
		def locations = [DataLocation.findByCode(KIVUYE)]

		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+''):[(HEALTH_CENTER_GROUP):"1"]])
		
		def value1 = newNormalizedDataElementValue(normalizedDataElement, locations[0], period, Status.VALID, v("1"))
		
		when:
		def lines = dataElementExportService.getExportLineForValue(locations[0],period,normalizedDataElement)
		def periodString = "[ "+(period.startDate).toString()+" - "+(period.endDate).toString()+" ]";
		def listDataList=["Rwanda","North","Burera",""]
		listDataList.add(locations[0].code);
		listDataList.add(locations[0].names_en);
		listDataList.add(locations[0].type.names_en);
		listDataList.add(period.code);
		listDataList.add(periodString);
		listDataList.add(normalizedDataElement.class.simpleName);
		listDataList.add(normalizedDataElement.code);
		listDataList.add("");
		listDataList.add("1.0");
		listDataList.add("");
		def listOfList=[]
		listOfList.add(listDataList)
		
		then:
		lines.equals(listOfList);
	}
	
	def "test exportDataElements() DataElement is null"(){
		setup:
		setupLocationTree();
		def date= new Date();
		def period= newPeriod();
		def locations = [DataLocation.findByCode(KIVUYE)]
		
		def typeString = Type.TYPE_STRING();
		def dataElementString = null;
		
		when:
		def lines = dataElementExportService.getExportLineForValue(locations[0],period,dataElementString)	
		then:
		lines==[];
	}
	
	def "test exportDataElements() RawDataElementValue is null"(){
		setup:
		setupLocationTree();
		def date= new Date();
		def period= newPeriod();
		def locations = [DataLocation.findByCode(KIVUYE)]

		def typeNumber = Type.TYPE_NUMBER();
		def dataElementNumber = newRawDataElement(CODE(1), typeNumber);
			
		when:
		def lines = dataElementExportService.getExportLineForValue(locations[0],period,dataElementNumber)
		then:
		lines==[]
	}
	
}

