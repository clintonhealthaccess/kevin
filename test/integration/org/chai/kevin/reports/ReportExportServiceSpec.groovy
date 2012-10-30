package org.chai.kevin.reports

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.dsr.DsrIntegrationTests;
import org.chai.kevin.fct.FctIntegrationTests;
import org.chai.kevin.dsr.DsrTarget;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.RawDataElementValue
import org.chai.kevin.value.Value;

class ReportExportServiceSpec extends ReportIntegrationTests {

	def reportExportService
	def dsrService
	def fctService
	
	def "test for get dsr zip file"(){
		setup:
		setupLocationTree()
		setupProgramTree()
		def period = newPeriod()
		def program = ReportProgram.findByCode(PROGRAM1)
		def location = Location.findByCode(BURERA)
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def dataElementValue1 = newRawDataElementValue(dataElement, period, DataLocation.findByCode(KIVUYE), v("30"))
		def dataElementValue2 = newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), v("50"))
		def category = DsrIntegrationTests.newDsrTargetCategory(CODE(1), 1)
		def target = DsrIntegrationTests.newDsrTarget(CODE(3), 1, dataElement, program, category)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def reportType = Utils.ReportType.TABLE
		refresh()
		
		when:
		def dsrTable = dsrService.getDsrTable(location, program, period, types, category, reportType)
		
		then:
		dsrTable != null
		dsrTable.hasData() == true
		
		when:
		def csvFile = reportExportService.getReportExportFile("file", dsrTable)
		def zipFile = Utils.getZipFile(csvFile, "file")
		
		then:
		zipFile.exists() == true
		zipFile.length() > 0
		
		when:
		String headers = null
		String exportData1 = null
		String exportData2 = null
		try {
			FileInputStream fis = new FileInputStream(csvFile);
			BufferedReader d = new BufferedReader(new InputStreamReader(fis));
			headers = d.readLine();
			exportData1 = d.readLine();
			exportData2 = d.readLine();
			fis.close();
			d.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
		then:
		headers == "Location,CODE3"
		exportData1 == "Rwanda-North-Burera-Kivuye HC,30"
		exportData2 == "Rwanda-North-Burera-Butaro DH,50"
		
	}
	
	def "test for get fct zip file"(){
		setup:
		setupLocationTree()
		setupProgramTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))
		def program = ReportProgram.findByCode(PROGRAM1)
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def target = FctIntegrationTests.newFctTarget(CODE(3), 1, program)
		def targetOption = FctIntegrationTests.newFctTargetOption(CODE(4), 1, target, sum)
		def location = Location.findByCode(NORTH)
		def dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def reportType = Utils.ReportType.TABLE
		def fctTable = null
		refresh()
		
		when:
		fctTable = fctService.getFctTable(location, program, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable != null
		fctTable.hasData() == true
		
		when:
		def csvFile = reportExportService.getReportExportFile("file", fctTable)
		def zipFile = Utils.getZipFile(csvFile, "file")
		
		then:
		zipFile.exists() == true
		zipFile.length() > 0
		
		when:
		String headers = null
		String exportData1 = null
		String exportData2 = null
		String exportData3 = null
		String exportData4 = null
		try {
			FileInputStream fis = new FileInputStream(csvFile);
			BufferedReader d = new BufferedReader(new InputStreamReader(fis));
			headers = d.readLine();
			exportData1 = d.readLine();
			exportData2 = d.readLine();
			exportData3 = d.readLine();
			exportData4 = d.readLine();
			fis.close();
			d.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
		then:
		headers == "Location,CODE4"
		exportData1 == "Rwanda-North,2"
		exportData2 == "Rwanda-North-Burera,2"
		exportData3 == "Rwanda-North-Burera-Kivuye HC,1"
		exportData4 == "Rwanda-North-Burera-Butaro DH,1"
	}
	
	def "test for valid export filename"() {
		setup:
		setupLocationTree()
		setupProgramTree()
		def period = newPeriod()
		def program = ReportProgram.findByCode(PROGRAM1)
		def location = Location.findByCode(BURERA)
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def category = DsrIntegrationTests.newDsrTargetCategory(CODE(1), 1)
		def target = DsrIntegrationTests.newDsrTarget(CODE(3), 1, dataElement, program, category)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def reportType = Utils.ReportType.TABLE
		
		when:
		def file = reportExportService.getExportFilename("Reports - DSR", location, program, period)
		
		then:
		file.startsWith("ReportsDSR_2005_Program1_Burera")
	}

}