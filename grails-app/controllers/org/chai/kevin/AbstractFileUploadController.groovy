/*
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
  * 
  * @author Eugene Munyaneza.
  * 
  */
package org.chai.kevin

import org.chai.kevin.imports.FileImporter
import org.chai.kevin.imports.GeneralDataImporter
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
	
	public boolean importFile(FileImporter importer, MultipartFile file, String encoding, Character delimiter){
		
		if (getFileType(file) == FileType.ZIP){
			importer.importZipFiles(file.getInputStream(), encoding, delimiter);
			return true;
		}
		else if (getFileType(file) == FileType.CSV){
			importer.importCsvFile(file.getName(), file.getInputStream(), encoding, delimiter);
			return true;
		}
		return false;
	}
}
