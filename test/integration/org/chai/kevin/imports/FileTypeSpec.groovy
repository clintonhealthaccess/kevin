package org.chai.kevin.imports

import org.chai.kevin.AbstractFileUploadController
import org.chai.kevin.IntegrationTests
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile
import org.chai.kevin.FileType
import org.chai.kevin.imports.GeneralImporterController

class FileTypeSpec extends IntegrationTests{

	class UploaderControllerTest extends AbstractFileUploadController{
		
	}
    def "check the type of files uploaded"() {
		when:
			File tempFileZip = new File("test/integration/org/chai/kevin/imports/nominativeTestFile.csv.zip");
			File tempFileCsv = new File("test/integration/org/chai/kevin/imports/nominativeDataUgin.csv");
			File tempFilePdf = new File("test/integration/org/chai/kevin/imports/Git1.pdf");
			
			GrailsMockMultipartFile grailsMockMultipartFileZip = new GrailsMockMultipartFile(
				"nominativeTestFile",
				"nominativeTestFile.csv.zip",
				"",
				 tempFileZip.getBytes())
			
			GrailsMockMultipartFile grailsMockMultipartFileCsv = new GrailsMockMultipartFile(
				"nominativeDataUgin",
				"nominativeDataUgin.csv",
				"",
				 tempFileCsv.getBytes())
			
			GrailsMockMultipartFile grailsMockMultipartFilePdf = new GrailsMockMultipartFile(
				"Git1",
				"Git1.pdf",
				"",
				 tempFilePdf.getBytes())
			
			UploaderControllerTest uploaderControllerTest = new UploaderControllerTest();
		then:
			
			uploaderControllerTest.getFileType(grailsMockMultipartFileZip) == FileType.ZIP
			uploaderControllerTest.getFileType(grailsMockMultipartFileCsv) == FileType.CSV
			uploaderControllerTest.getFileType(grailsMockMultipartFilePdf) == FileType.NONE
	}
}
