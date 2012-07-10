package org.chai.kevin

import org.springframework.web.multipart.MultipartFile;

enum FileType{
	NONE,ZIP,CSV
}

public abstract class AbstractFileUploadController extends AbstractController {

	public FileType getFileType(MultipartFile file){
		String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);// Add a one to get where to cut from

		if(fileExtension.equalsIgnoreCase("csv")) return FileType.CSV;
		else if(fileExtension.equalsIgnoreCase("zip")) return FileType.ZIP;
		
		return FileType.NONE;
	}
}
