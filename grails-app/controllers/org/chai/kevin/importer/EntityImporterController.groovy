package org.chai.kevin.importer

import org.chai.kevin.AbstractController
import org.chai.kevin.entity.EntityImportService
import org.springframework.web.multipart.commons.CommonsMultipartFile

class EntityImporterController extends AbstractController {
	
	EntityImportService entityImportService;
	ImporterService importerService;
	
	final String ENTITY_IMPORT = "entityImport";
	final String IMPORT_OUTPUT = "importOutput";
	
	def importer = {
		def clazz = (Class) params.get("entityClass");
		this.getModel(null, null, ENTITY_IMPORT);
	}
	
	def uploader = { EntityImporterCommand cmd ->
		ImporterErrorManager errorManager = new ImporterErrorManager();
		if (!cmd.hasErrors()) {
			if(log.isDebugEnabled()) log.debug("uploader(file="+cmd.file.getInputStream()+",class="+cmd.clazz+")")
			InputStreamReader csvInputStreamReader = new InputStreamReader(cmd.file.getInputStream());			
			entityImportService.importEntityData(csvInputStreamReader, cmd.clazz,errorManager);			
			this.getModel(cmd, errorManager, IMPORT_OUTPUT);
		}else{
			this.getModel(cmd, errorManager, ENTITY_IMPORT);
		}
	}
	
	def getModel(EntityImporterCommand cmd, ImporterErrorManager errorManager, String view) {
		if(log.isDebugEnabled()) log.debug("getModel(cmd="+cmd+",errorManager="+errorManager+",view="+view+")")
		render (view: '/import/'+view, model:[
					entityClass: clazz,
					entityImporter: cmd,
					errorManager: errorManager
				])
	}	
}

class EntityImporterCommand {

	Class clazz;
	CommonsMultipartFile file;
	
	static constraints = {
		clazz(blank:false,nullable:false)
		file(blank:false,nullable:false, validator: { val, obj ->
			final String FILE_TYPE = "text/csv";
			boolean valid = true;
			if(val != null)
				if(!val.contentType.equals(FILE_TYPE))
					return valid=false;
			return valid;
		})
	}
}