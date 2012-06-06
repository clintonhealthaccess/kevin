package org.chai.kevin.imports;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.ICsvMapReader;

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

	protected Map<String, String> readRow(String filename, ICsvMapReader csvMapReader, String[] headers, ImporterErrorManager manager) throws IOException {
		try {
			return csvMapReader.read(headers);
		} catch (SuperCSVException e) {
			manager.incrementNumberOfUnsavedRows();
			manager.getErrors().add(new ImporterError(filename, csvMapReader.getLineNumber(), "", "import.error.message.headers.mismatch"));
			return new HashMap<String, String>();
		}
	}
	
	/**
	 * Imports one file.
	 * 
	 * @param fileName
	 * @param reader
	 * @throws IOException
	 */
	public abstract void importData(String fileName, Reader reader) throws IOException;

}