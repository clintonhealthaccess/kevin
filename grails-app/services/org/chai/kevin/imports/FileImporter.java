package org.chai.kevin.imports;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

public abstract class FileImporter{

	protected static final Log log = LogFactory.getLog(FileImporter.class);

	public FileImporter() {
		super();
	}
	
	public void importCsvFile(String fileName, InputStream inputStream, String encoding, Character delimiter) throws IOException {
		importData(fileName, getReaderInCorrectEncodingWithDelimiter(inputStream, encoding, delimiter));	
	}

	public void importZipFiles(InputStream inputStream, String encoding, Character delimiter) throws IOException {
		ZipInputStream zipInputStream = null;
		zipInputStream = new ZipInputStream(inputStream);
		ZipEntry zipEntry;
		while((zipEntry = zipInputStream.getNextEntry()) != null) {
			if (!zipEntry.isDirectory()) importData(zipEntry.getName(), getReaderInCorrectEncodingWithDelimiter(zipInputStream, encoding, delimiter));
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
	
	private ICsvMapReader getReaderInCorrectEncodingWithDelimiter(InputStream inputStream, String encoding, Character delimiter) {
		Charset charset = null;
		try {
			if (encoding != null && !encoding.isEmpty()) charset = Charset.forName(encoding);
		} catch (Exception e) {
			if (log.isWarnEnabled()) log.warn("encoding not recognized: "+encoding);
		}
		
		if (charset == null) {
			charset = Charset.defaultCharset();
		}
		int delimiterChar = CsvPreference.EXCEL_PREFERENCE.getDelimiterChar();
		if (delimiter != null) delimiterChar = delimiter;
		CsvPreference preference = new CsvPreference('"', delimiterChar, CsvPreference.EXCEL_PREFERENCE.getEndOfLineSymbols());
		return new CsvMapReader(new InputStreamReader(inputStream, charset), preference);
	}
	
	/**
	 * Imports one file.
	 * 
	 * @param fileName
	 * @param reader
	 * @throws IOException
	 */
	public abstract void importData(String fileName, ICsvMapReader reader) throws IOException;

}