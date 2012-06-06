package org.chai.kevin.imports;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class FileImporter {

	protected static final Log log = LogFactory.getLog(DataImporter.class);

	public FileImporter() {
		super();
	}

	public void importCsvFile(String fileName, InputStream inputStream) throws IOException {
		importData(fileName,new InputStreamReader(inputStream));
	}

	public void importZipFiles(InputStream inputStream) throws IOException {
		ZipInputStream zipInputStream = null;
		zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
		ZipEntry zipEntry;
		while((zipEntry=zipInputStream.getNextEntry())!=null){
			if(!zipEntry.isDirectory())
				importData(zipEntry.getName(),new InputStreamReader(zipInputStream));
			if (log.isDebugEnabled()) log.debug("zipEntryName " +zipEntry.getName());
		}
	}

	/**
	 * Imports one file.
	 * 
	 * @param fileName
	 * @param reader
	 * @throws IOException
	 */
	public abstract void importData(String fileName, Reader reader)
			throws IOException;

}