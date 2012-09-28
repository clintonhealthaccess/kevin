package org.chai.task

import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.imports.DataImporter;
import org.chai.kevin.imports.FileImporter;
import org.chai.kevin.imports.GeneralDataImporter;
import org.chai.kevin.imports.ImporterErrorManager;
import org.chai.kevin.imports.NominativeDataImporter;
import org.chai.kevin.Period;

class GeneralImportTask extends ImportTask {

	def locationService;
	def valueService;
	def dataService;
	def periodService;
	def sessionFactory;
	def transactionManager;
	
	String getInformation() {
		return message(code:'import.file.label')+': '+getInputFilename()
	}
	
	FileImporter getImporter(ImporterErrorManager errorManager) {
		return new GeneralDataImporter(
			locationService, valueService, dataService,
			sessionFactory, transactionManager,
			errorManager, periodService
		);
	}
	
	String getFormView() {
		return 'generalImport'	
	}
	
	Map getFormModel() {
		return [ task: this ]
	}
	
}
