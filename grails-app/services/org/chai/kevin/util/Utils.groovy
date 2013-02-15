/**
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
 */
package org.chai.kevin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.chai.kevin.Exportable;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.ValueType;
import org.chai.location.DataLocationType;
import org.chai.kevin.security.User;
import org.chai.kevin.value.Value;
import org.chai.kevin.data.Enum;
import org.chai.kevin.util.DataUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.chai.kevin.data.Type;
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

/**
 * @author Jean Kahigiso M.
 * 
 */
public class Utils {
	
	private static final Log log = LogFactory.getLog(Utils.class);

	private final static String ZIP_FILE_EXTENSION = ".zip";	
	private final static String DATE_FORMAT_TIME = "dd-MM-yyyy hh:mm:ss";

	public static boolean matches(String text, String value) {
		if (value == null) return false;
		return value.matches("(?i).*"+text+".*");
	}
	
	public static String formatNumber(String format, Number value) {
		if (format == null) format = "#";
		
		DecimalFormat frmt = new DecimalFormat(format);
		return frmt.format(value.doubleValue());
	}
	
	public static String parseNumber(String string){
		return string.replaceAll("[^0-9]", "");
	}

	public static String stripHtml(String htmlString) {
		String noHtmlString;
	
		if (htmlString != null) {
			noHtmlString = htmlString.replace("&nbsp;", " ");
			noHtmlString = noHtmlString.replaceAll("<.*?>", " ");
			noHtmlString = StringEscapeUtils.unescapeHtml(noHtmlString);
			noHtmlString = noHtmlString.trim();
		}
		else noHtmlString = htmlString;
	
		return noHtmlString;
	}
	
	public static File getZipFile(File file, String filename) throws IOException {		
		List<File> files = new ArrayList<File>();
		files.add(file);
		return getZipFile(files, filename);
	}
	
	public static File getZipFile(List<File> files, String filename) throws IOException {		
		File zipFile = File.createTempFile(filename, ZIP_FILE_EXTENSION);

		ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
				
		try {
			for(File file: files){
				FileInputStream fileInputStream = new FileInputStream(file);
			    ZipEntry zipEntry = new ZipEntry(file.getName());
			    zipOutputStream.putNextEntry(zipEntry);
			    
			    IOUtils.copy(fileInputStream, zipOutputStream);
			    zipOutputStream.closeEntry();	
			}			
		} catch (IOException e) {
			throw e;
		} finally {
		    IOUtils.closeQuietly(zipOutputStream);
		    IOUtils.closeQuietly(zipOutputStream);
		}
			
		return zipFile;
	}
	
	public static String formatExportCode(String code){
		// TODO relocate outer brackets to this method
//		if(code == null || code.isEmpty()){
//			code = CODE_MISSING;
//		}
		return ImportExportConstant.CODE_DELIMITER + code + ImportExportConstant.CODE_DELIMITER;
	}
	
	public static def copyI18nField(def source, def target, def fieldName) {
		def getterName = 'get'+fieldName
		def setterName = 'set'+fieldName
		
		def grailsApplication = new User().domainClass.grailsApplication
		grailsApplication.config.i18nFields.locales.each{ language ->
			def text = source."$getterName"(new Locale(language))
			if (text != null) target."$setterName"(text, new Locale(language))
		}
	}
	
	public static String getStringValue(Value value, Type type, def enums = null, def format = null, def zero = null, def rounded = null) {
		if (value == null || value.isNull()) return null
		def result;
		switch (type.type) {
			case ValueType.BOOL:
				if (value.booleanValue) result = 'true'
				else result = 'false'
				break;
			case (ValueType.ENUM):
				def enume = null
				 
				if (enums == null) enume = Enum.findByCode(type.enumCode, [cache: true])
				else enume = enums?.get(type.enumCode)
				
				if (enume == null) result = value.enumValue
				else {
					def option = enume?.getOptionForValue(value.enumValue)
					if (option == null) result = value.enumValue
					else result = DataUtils.noNull(option.names)
				}
				break;
			case (ValueType.NUMBER):
				if (zero != null && value.numberValue == 0){
					result = zero
					if (log.isDebugEnabled()) log.debug("utils.getStringValue(), value:"+value+", numberValue:"+value.numberValue+", zero:"+zero+", result:"+result)
				}
				else {
					result = Utils.formatNumber(format, (rounded!=null ? value.numberValue.round(rounded) : value.numberValue))
					if (log.isDebugEnabled()) log.debug("utils.getStringValue(), value:"+value+", numberValue:"+value.numberValue+", rounded:"+rounded+", result:"+result)
				}
				break;
			case (ValueType.MAP):
				// TODO
			case (ValueType.LIST):
				// TODO
				break;
			default:
				result = value.stringValue
		}
		return result;
	}
	
	public static Type buildType(String typeString) {
		if (log.isDebugEnabled()) log.debug("buildType(typeString="+typeString+")");
		if (typeString == null || typeString.trim() == "") return null;
		
		def list = []
		
		def importCustomizer = new ImportCustomizer()
		importCustomizer.addImport('TypeBuilder', 'org.chai.TypeBuilder')
		
		def config = new CompilerConfiguration()
		config.addCompilationCustomizers(importCustomizer)
		
		def binding = new Binding(list: list)
		def shell = new GroovyShell(Utils.class.classLoader, binding, config) 
		shell.evaluate('''
			static def type(def closure) {
				def typeBuilder = new TypeBuilder()
				def type = typeBuilder.build(closure);				
				return type;
			}
			
			def box = true
			
			list << 
			''' + typeString.replaceAll("\n", " ")
		);
		
		if (log.isDebugEnabled()) log.debug("buildType(...)="+list[0]);
		return list[0];
	}
	
}
