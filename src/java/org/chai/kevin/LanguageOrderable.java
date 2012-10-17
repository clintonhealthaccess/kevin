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
package org.chai.kevin;

import java.util.Comparator;
import java.util.Map;


/**
 * @author Jean Kahigiso M.
 *
 */
public abstract class LanguageOrderable  {
	
	public abstract Long getId();
	public abstract Map<String, Integer> getOrder();
	
	public Integer getOrder(String currentLanguage, String fallbackLanguage) {
		if (getOrder() == null) return null;
		
		if (getOrder().containsKey(currentLanguage)) return getOrder().get(currentLanguage);
		else return getOrder().get(fallbackLanguage);
	}
	
	public static Comparator<LanguageOrderable> getOrderableComparator(final String currentLanguage, final String fallbackLanguage) {
		
		return new Comparator<LanguageOrderable>(){
			@Override
			public int compare(LanguageOrderable arg0, LanguageOrderable arg1) {
				if (arg0.getOrder(currentLanguage, fallbackLanguage) == null 
					&& arg1.getOrder(currentLanguage, fallbackLanguage) == null) {
					return arg0.getId().compareTo(arg1.getId());
				}
				else if (arg0.getOrder(currentLanguage, fallbackLanguage) == null) {
					return -1;
				}
				else if (arg1.getOrder(currentLanguage, fallbackLanguage) == null) {
					return 1;
				}
				else return arg0.getOrder(currentLanguage, fallbackLanguage).compareTo(arg1.getOrder(currentLanguage, fallbackLanguage));
			}
		};
		
	}

}
