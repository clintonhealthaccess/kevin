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

import java.util.List;
import java.util.Set;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.Period;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.value.DataValue;

/**
 * @author Jean Kahigiso M.
 *
 */
class ExporterServiceSpec  extends IntegrationTests {
	
	def exporterService;
	
	def "test seacrh exporter"(){
		setup:
		setupLocationTree();
		def periods=new HashSet([newPeriod()]);
		def locationType="Health Center,District Hospital";
		def typeOne = Type.TYPE_NUMBER();
		def typeTwo = Type.TYPE_BOOL();
		def dataElementOne = newRawDataElement(CODE(1), typeOne);
		def dataElementTwo = newRawDataElement(CODE(2), typeTwo);
		def locations=new HashSet();
		locations.addAll(getLocations([BURERA]));
		locations.addAll(getDataLocations([KIVUYE]));
		
		def data=new HashSet([dataElementOne,dataElementTwo]);
		
		def exporterOne = newExporter(j("en":"Testing Seach One"),periods, locationType, locations, data);
		def exporterTwo = newExporter(j("en":"Testing Seach Two"),periods, locationType, locations, data);
		
		when:
		def seachOne = exporterService.searchExporter(Exporter.class,'One',[:]);
		def searchTwo = exporterService.searchExporter(Exporter.class,'Seach Two',[:]);
		def searchThree = exporterService.searchExporter(Exporter.class,'calculation',[:]);
		then:
		seachOne.equals([exporterOne]);
		searchTwo.equals([exporterTwo]);
		searchThree.equals([]);
	}
	
	def "test exportDataElement(exporter)"(){
		setup:
		setupLocationTree();
		def periods=new HashSet([newPeriod()]);
		def locationType="Health Center,District Hospital";
		def typeOne = Type.TYPE_NUMBER();
		def typeTwo = Type.TYPE_BOOL();
		def dataElementOne = newRawDataElement(CODE(1), typeOne);
		def dataElementTwo = newRawDataElement(CODE(2), typeTwo);
		def locations=new HashSet();
		locations.addAll(getLocations([BURERA]));
		locations.addAll(getDataLocations([KIVUYE]));
		def data=new HashSet([dataElementOne,dataElementTwo]);
		def exporter = newExporter(j("en":"Testing Seach One"),periods, locationType, locations, data);
		
		when:
		def exportedFile = exporterService.exportDataElement(exporter);
		then:
		//TODO Best way to check
		exportedFile!=null
	}
	
	def "test exportDataElement(String fileName,List<DataLocation> dataLocations,Set<Period> periods,Set<Data<DataValue>> data)"(){
		setup:
		setupLocationTree();
		def periods=new HashSet([newPeriod()]);
		def locationType="Health Center,District Hospital";
		def typeOne = Type.TYPE_NUMBER();
		def typeTwo = Type.TYPE_BOOL();
		def dataElementOne = newRawDataElement(CODE(1), typeOne);
		def dataElementTwo = null;
		def locations=new HashSet();
		locations.addAll(getLocations([BURERA]));
		locations.addAll(getDataLocations([KIVUYE]));
		def dataOne=new HashSet([dataElementOne]);
		def dataTwo=new HashSet([dataElementTwo]);
		def exporterOne = newExporter(j("en":"Testing Seach One"),periods, locationType, locations, dataOne);
		def exporterTwo = newExporter(j("en":"Testing Seach One"),periods, locationType, locations, dataTwo);

		when:
		def exportedFileOne = exporterService.exportDataElement("Testing",exporterOne.locations,exporterOne.periods,exporterOne.data);
		def exportedFileTwo = exporterService.exportDataElement("Testing",exporterTwo.locations,exporterTwo.periods,exporterTwo.data);
		then:
		//TODO  any insight please
		exportedFileOne!=null
		exportedFileTwo==null
	}
}
