package org.chai.kevin.task

import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.imports.DataImporter;
import org.chai.kevin.imports.EntityImporter;
import org.chai.kevin.imports.FileImporter;
import org.chai.kevin.imports.GeneralDataImporter;
import org.chai.kevin.imports.ImporterErrorManager;
import org.chai.kevin.imports.NominativeDataImporter;
import org.chai.kevin.Period;

class EntityImportTask extends ImportTask {

	String entityClass;
	def sessionFactory;
	
	FileImporter getImporter(ImporterErrorManager errorManager) {
		def clazz = Class.forName(entityClass, true, this.getClass().getClassLoader());
		
		if (clazz != null)
			return new EntityImporter(sessionFactory, errorManager, clazz);
		else {
			return null;
		}
	}
	
	String getFormView() {
		return 'entityImport'	
	}
	
	Map getFormModel() {
		return [ 
			task: this
		]
	}
	
	static constraints = {
		entityClass (nullable: false, blank: false)	
	}
}
