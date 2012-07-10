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
import org.chai.kevin.data.Type;
import org.chai.kevin.exports.DataElementExport;
import org.chai.kevin.location.DataLocation;
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
class CalculationExportServiceSpec extends IntegrationTests {
	
	def calculationExportService;
	def locationService;
	
	def "test exportData(exporter) return file"(){
		setup:
		setupLocationTree();
		def periods=new HashSet([newPeriod()]);
		def locationType="Health Center,District Hospital";
		
		def sum = newSum("",CODE(1));
		def aggregation = newAggregation("1",CODE(2));
		
		def locations=new HashSet();
		locations.addAll(getLocations([BURERA]));
		locations.addAll(getDataLocations([KIVUYE]));
		
		def calculations = new HashSet([sum,aggregation]);
		
		def exporter = newCalculationExport(j("en":"Testing Seach One"),periods, locationType, locations, calculations);
		
		when:
		def exportedFile = calculationExportService.exportData(exporter);
		then:
		//TODO Best way to check
		exportedFile!=null
	}
	
	def "test exportCalculations() return file"(){
		setup:
		setupLocationTree();
		def periods=new HashSet([newPeriod()]);
		def locationType="Health Center,District Hospital";
		def locationTypes = new HashSet(getDataLocationTypes([HEALTH_CENTER_GROUP,DISTRICT_HOSPITAL_GROUP]));
		
		def sum = newSum("1",CODE(1));
		
		def locations=new HashSet();
		locations.addAll(getLocations([BURERA]));
		locations.addAll(getDataLocations([KIVUYE]));
		
		def calculations=new HashSet([sum]);
		
		def exporterOne = newCalculationExport(j("en":"Testing Seach One"),periods, locationType, locations, calculations);
		when:
		def selectedLocations = locationService.getDataLocationsOfType(locations,locationTypes)
		def exportedFileOne = calculationExportService.exportCalculations("Testing",selectedLocations,exporterOne.periods,exporterOne.calculations,locationTypes);
		then:
		exportedFileOne!=null
	}
		
	def "test getExportLineForValue() Calculation value is not null"(){
		setup:
		setupLocationTree();
		def date= new Date();
		def period= newPeriod();
		def locationTypes = new HashSet(getDataLocationTypes([HEALTH_CENTER_GROUP,DISTRICT_HOSPITAL_GROUP]));
		def locations= getDataLocations([KIVUYE]);
		def calculation = newAggregation("1",CODE(1));
			
		when:
		def lines = calculationExportService.getExportLineForValue(locations[0],period,calculation,locationTypes)	
		def periodString = "[ "+(period.startDate).toString()+" - "+(period.endDate).toString()+" ]";
		
		def listDataList=[];
		listDataList.add(locations[0].code);
		listDataList.add(locations[0].names.en);
		listDataList.add("");
		listDataList.add(locations[0].type.names.en);
		listDataList.add(period.code);
		listDataList.add(periodString);
		listDataList.add(calculation.class.simpleName);
		listDataList.add(calculation.code);
		listDataList.add("1.0");
		listDataList.add("");
		def listOfList=[]
		listOfList.add(listDataList)
		then:
		lines.equals(listOfList);
	}
	
	def "test getExportLineForValue() Calculation is null"(){
		setup:
		setupLocationTree();
		def date= new Date();
		def period= newPeriod();
		def locationTypes = new HashSet(getDataLocationTypes([HEALTH_CENTER_GROUP,DISTRICT_HOSPITAL_GROUP]));
		def locations= getDataLocations([KIVUYE]);
		
		def calculation = null;
		
		when:
		def lines = calculationExportService.getExportLineForValue(locations[0],period,calculation,locationTypes)	
		then:
		lines==[];
	}
	
	
	
}

