package org.chai.kevin.imports;

import org.chai.kevin.AbstractFileUploadController;
import org.chai.kevin.imports.EntityImporter;
import org.chai.kevin.imports.ImporterErrorManager;
import org.springframework.web.multipart.commons.CommonsMultipartFile

class EntityImporterController extends AbstractFileUploadController {
	
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

			EntityImporter importer = new EntityImporter(sessionFactory, errorManager, clazz);
			
			importFile(importer, cmd.file, cmd.encoding, cmd.delimiter, errorManager)
			
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
	String encoding;
	Character delimiter;
	CommonsMultipartFile file;
	
	static constraints = {
		file(blank:false, nullable:false, validator: {val, obj ->
			return !val.empty
		})
		delimiter(blank:false,nullable:false)
		encoding(blank:false,nullable:false)
		entityClass(blank:false,nullable:false)
	}
}