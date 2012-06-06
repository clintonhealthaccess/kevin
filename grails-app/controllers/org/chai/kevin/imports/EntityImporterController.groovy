package org.chai.kevin.imports

import org.chai.kevin.AbstractController
import org.chai.kevin.imports.EntityImporter;
import org.chai.kevin.imports.ImporterErrorManager;
import org.chai.kevin.imports.NormalizedDataImporter;
import org.springframework.web.multipart.commons.CommonsMultipartFile

class EntityImporterController extends AbstractController {
	
	def sessionFactory
	
	final String ENTITY_IMPORT = "entityImport";
	final String IMPORT_OUTPUT = "importOutput";
	
	def importer = {
		def entityClass = (String) params.get("entityClass");
		def clazz = Class.forName(entityClass, true, this.getClass().getClassLoader());
		this.getModel(clazz, null, null, ENTITY_IMPORT);
	}
	
	def uploader = { EntityImporterCommand cmd ->
		ImporterErrorManager errorManager = new ImporterErrorManager();
		def clazz = Class.forName(cmd.entityClass, true, this.getClass().getClassLoader());
		if (!cmd.hasErrors()) {
			if(log.isDebugEnabled()) log.debug("uploader(file="+cmd.file+",clazz="+cmd.entityClass+")")

			EntityImporter importer = new EntityImporter(
				sessionFactory, errorManager, clazz
			);
			if(cmd.file.getContentType().equals(FILE_TYPE_ZIP))
				importer.importZipFiles(cmd.file.getInputStream())
			if(cmd.file.getContentType().equals(FILE_TYPE_CSV))
				importer.importCsvFile(cmd.file.getName(),cmd.file.getInputStream())
			
			cmd.file.getInputStream().close();
			
			this.getModel(clazz,cmd,errorManager,IMPORT_OUTPUT);
		} else {
			this.getModel(clazz,cmd,errorManager,ENTITY_IMPORT);
		}
	}
	
	def getModel(Class clazz, EntityImporterCommand cmd, ImporterErrorManager errorManager, String view) {
		if(log.isDebugEnabled()) log.debug("getModel(cmd="+cmd+",entityClass="+clazz.name+",errorManager="+errorManager+",view="+view+")")
		render (view: '/import/'+view, model:[
			entityClass: clazz.name,
			entityImporter: cmd,
			errorManager: errorManager
		])
	}
}

class EntityImporterCommand {

	String entityClass;
	CommonsMultipartFile file;
	
	static constraints = {
		entityClass(blank:false,nullable:false)
//		file(blank:false,nullable:false, validator: { val, obj ->
//			final String FILE_TYPE = "text/csv";
//			boolean valid = true;
//			if(val != null)
//				if(!val.contentType.equals(FILE_TYPE))
//					return valid=false;
//			return valid;
//		})
	}
}