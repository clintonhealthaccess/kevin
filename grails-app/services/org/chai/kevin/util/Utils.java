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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * @author Jean Kahigiso M.
 * 
 */
public class Utils {

	public static Set<String> split(String string) {
		Set<String> result = new HashSet<String>();
		if (string != null) result.addAll(Arrays.asList(StringUtils.split(string.trim(), ',')));
		return result;
	}

	public static String unsplit(Object list) {
		if (list == null) return "";
		if (list instanceof String) return (String) list;
		if (list instanceof Collection) return StringUtils.join(((Collection<String>)list).toArray(), ',');
		else return StringUtils.join((Object[]) list, ',');
	}
	
	@SuppressWarnings("unused")
	private static boolean matches(String text, String value) {
		if (value == null) return false;
		return value.matches("(?i).*"+text+".*");
	}

	public static String formatDate(Date date) {
		if (date == null) return null;
		return DATE_FORMAT.format(date);
	}
	
	public static Date parseDate(String string) throws ParseException {
		return DATE_FORMAT.parse(string);
	}
	
	private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

	public static String stripHtml(String htmlString, Integer num) {
		String noHtmlString;
		Integer length = num;
	
		if (htmlString != null) noHtmlString = htmlString.replaceAll("\\<.*?\\>", "");
		else noHtmlString = htmlString;
	
		if (num == null || noHtmlString.length() <= num) return noHtmlString;
		return noHtmlString.substring(0, length);
	}
	
}
