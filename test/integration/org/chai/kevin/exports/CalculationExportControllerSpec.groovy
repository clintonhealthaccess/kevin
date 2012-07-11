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

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.Period;
import org.chai.kevin.data.Type;
import org.chai.kevin.exports.DataElementExportController;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;

/**
 * @author Jean Kahigiso M.
 *
 */
class CalculationExportControllerSpec extends IntegrationTests {
	
	def calculationExportController;
	
	def "get dataExport"(){
		setup:
		setupLocationTree();
		def periods=new HashSet([newPeriod()]);
		def locationType="Health Center,District Hospital";
		
		def sum = newSum("",CODE(1));
		def aggregation = newAggregation("1",CODE(2));
		
		def locations=new HashSet();
		locations.addAll(getLocations([BURERA]));
		locations.addAll(getDataLocations([KIVUYE]));
		
		def calculations=new HashSet([sum,aggregation]);
		def dataExport = newCalculationExport(j("en":"Testing Seach One"), periods, locationType, locations, calculations);
		calculationExportController = new CalculationExportController();
		
		when:
		calculationExportController.params.('export.id')=dataExport.id
		calculationExportController.export()
		then:
		calculationExportController.response.getContentType() == "application/zip"
		
	}
	
	def "bind params with duplicate location"(){
		setup:
		setupLocationTree();
		def periods=new HashSet([newPeriod()]);
		def locationType="Health Center,District Hospital";
		
		def sum = newSum("",CODE(1));
		def aggregation = newAggregation("1",CODE(2));
		
		def locations = new HashSet();
		locations.addAll(getLocations([BURERA]));
		locations.addAll(getDataLocations([KIVUYE]));
		
		def calculations=new HashSet([sum,aggregation]);
		calculationExportController = new  CalculationExportController();
		
		when:
		calculationExportController.params.('locationIds')=[Location.findByCode(BURERA).id+"",DataLocation.findByCode(KIVUYE).id+"",Location.findByCode(BURERA).id+""]
		calculationExportController.params.('periodIds')=[Period.list()[0].id+""]
		calculationExportController.params.('calculationIds')=[sum.id+"",aggregation.id+""]
		calculationExportController.params.('typeCodes')="Health Center,District Hospital";
		calculationExportController.save()
		def dataExports = CalculationExport.list();
		then:
		dataExports.size()==1;
		dataExports[0].periods==periods;
		dataExports[0].typeCodeString.equals('Health Center,District Hospital');
		dataExports[0].locations==locations;
		dataExports[0].calculations==calculations;
		
	}
	
	
	def "bind params with duplicate data"(){
		setup:
		setupLocationTree();
		def periods=new HashSet([newPeriod()]);
		def locationType="Health Center,District Hospital";
		
		def sum = newSum("1",CODE(1));
		def aggregation = newAggregation("",CODE(2));
		
		def locations = new HashSet();
		locations.addAll(getLocations([BURERA]));
		locations.addAll(getDataLocations([KIVUYE]));
		
		def calculations=new HashSet([sum,aggregation]);
		calculationExportController = new  CalculationExportController();
		
		when:
		calculationExportController.params.('locationIds')=[Location.findByCode(BURERA).id+"",DataLocation.findByCode(KIVUYE).id+""]
		calculationExportController.params.('periodIds')=[Period.list()[0].id+""]
		calculationExportController.params.('calculationIds')=[sum.id+"",aggregation.id+"",aggregation.id+""]
		calculationExportController.params.('typeCodes')="Health Center,District Hospital";
		calculationExportController.save()
		def dataExports = CalculationExport.list();
		then:
		dataExports.size()==1;
		dataExports[0].periods==periods;
		dataExports[0].typeCodeString.equals('Health Center,District Hospital');
		dataExports[0].locations==locations;
		dataExports[0].calculations==calculations;
		
	}
	
	def "bind params with duplicate period"(){
		setup:
		setupLocationTree();
		def periods=new HashSet([newPeriod()]);
		def locationType="Health Center,District Hospital";
		
		def sum = newSum("1",CODE(1));
		def aggregation = newAggregation("",CODE(2));
		
		def locations = new HashSet();
		locations.addAll(getLocations([BURERA]));
		locations.addAll(getDataLocations([KIVUYE]));
		
		def calculations=new HashSet([sum,aggregation]);
		calculationExportController = new  CalculationExportController();
		
		when:
		calculationExportController.params.('locationIds')=[Location.findByCode(BURERA).id+"",DataLocation.findByCode(KIVUYE).id+""]
		calculationExportController.params.('periodIds')=[Period.list()[0].id+"",Period.list()[0].id+""]
		calculationExportController.params.('calculationIds')=[sum.id+"",aggregation.id+""]
		calculationExportController.params.('typeCodes')="Health Center,District Hospital";
		calculationExportController.save()
		def dataExports = CalculationExport.list();
		then:
		dataExports.size()==1;
		dataExports[0].periods==periods;
		dataExports[0].typeCodeString.equals('Health Center,District Hospital');
		dataExports[0].locations==locations;
		dataExports[0].calculations==calculations;
	}
	def "dataExport clone successfull"(){
		setup:
		setupLocationTree();
		def periods=new HashSet([newPeriod()]);
		def locationType="Health Center,District Hospital";
		
		def sum = newSum("1",CODE(1));
		def aggregation = newAggregation("",CODE(2));
		
		def locations=new HashSet();
		locations.addAll(getLocations([BURERA]));
		locations.addAll(getDataLocations([KIVUYE]));
		
		def calculations=new HashSet([sum,aggregation]);
		def dataExport = newCalculationExport(j("en":"Testing Seach One"),periods, locationType, locations, calculations);
		
		calculationExportController = new  CalculationExportController();
		
		when:
		calculationExportController.params.('export.id')=dataExport.id
		calculationExportController.clone()
		then:
		CalculationExport.list().size()==2;
		CalculationExport.list()[0].periods.equals(CalculationExport.list()[1].periods)
		CalculationExport.list()[0].typeCodeString.equals(CalculationExport.list()[1].typeCodeString)
		CalculationExport.list()[0].locations.equals(CalculationExport.list()[1].locations)
		CalculationExport.list()[0].calculations.equals(CalculationExport.list()[1].calculations)
		
	}

}
