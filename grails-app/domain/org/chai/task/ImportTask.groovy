package org.chai.task

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.chai.kevin.imports.DataImporter
import org.chai.kevin.imports.FileImporter;
import org.chai.kevin.imports.ImporterError;
import org.chai.kevin.imports.ImporterErrorManager;
import org.springframework.web.multipart.MultipartFile;

abstract class ImportTask extends Task {

	def groovyPageRenderer
	
	String inputFilename
	String encoding
	Character delimiter
	
	static FileType getFileType(String filename){
		String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);// Add a one to get where to cut from

		if (fileExtension.equalsIgnoreCase("csv")) return FileType.CSV;
		else if(fileExtension.equalsIgnoreCase("zip")) return FileType.ZIP;
			
		return FileType.NONE;
	}
	
	// TODO take progress param
	private void importFile(def importer, File file, String encoding, Character delimiter, ImporterErrorManager errorManager){
		switch (getFileType(inputFilename)) {
		case FileType.ZIP:
			importer.importZipFiles(new FileInputStream(file), encoding, delimiter);
			break;
		case FileType.CSV:
			importer.importCsvFile(inputFilename, new FileInputStream(file), encoding, delimiter);
			break;
		default:
			throw new IllegalStateException('file type not accepted')
			break;
		}
	}
	
	def executeTask() {
		ImporterErrorManager errorManager = new ImporterErrorManager()
		FileImporter importer = getImporter(errorManager)
		File inputFile = new File(getFolder(), inputFilename)
		
		if (inputFile.exists() && importer != null) {
			withTransaction {
				importFile(importer, inputFile, encoding, delimiter, errorManager)
			}
			
			// put errorManager output in a file and save it
			String errorOutput = groovyPageRenderer.render template: '/task/importOutput', model: [errorManager: errorManager]
			File outputFile = new File(getFolder(), getOutputFilename())
			outputFile.createNewFile()
			
			def fileWriter = new FileWriter(outputFile)
			IOUtils.write(errorOutput, fileWriter)
			fileWriter.flush()
			IOUtils.closeQuietly(fileWriter)
		}
		else {
			throw new IllegalStateException("task is invalid, data, period or file not found")
		}
	}
	
	String getOutputFilename() {
		return 'importOutput.txt'
	}
	
	boolean isUnique() {
		return true;
	}
	
	abstract FileImporter getImporter(ImporterErrorManager errorManager);
	
	static constraints = {
		delimiter(blank:false, nullable:false)
		encoding(blank:false, nullable:false)
		inputFilename(blank:false, nullable: false, validator: {val, obj ->
			return getFileType(val) != FileType.NONE
		})
	}
}

enum FileType {
	NONE,ZIP,CSV
}