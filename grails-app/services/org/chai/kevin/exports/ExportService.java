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
package org.chai.kevin.exports;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.ValueVisitor;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.chai.location.LocationService;
import org.hibernate.SessionFactory;

/**
 * @author Jean Kahigiso M.
 *
 */
public abstract class ExportService {

	public LocationService locationService;
	public ValueService valueService;
	public SessionFactory sessionFactory;
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public abstract List<String> getExportDataHeaders();
	public abstract File exportData(DataExport export) throws IOException;
	
	public final class DataPointVisitor extends ValueVisitor{
		private List<String> basicInfo = new ArrayList<String>();
		private List<List<String>> lines= new ArrayList<List<String>>();

		@Override
		public void handle(Type type, Value value, String prefix, String genericPrefix) {
			if(!type.isComplexType()){
				List<String> dataList = new ArrayList<String>(basicInfo);
				dataList.add(Utils.getValueString(type, value));
				dataList.add(prefix);
				lines.add(dataList);
			}
		}
		public void setBasicInfo(List<String> basicInfo){
			this.basicInfo=basicInfo;
		}
		public List<List<String>> getLines(){
			return lines;
		}
	}

}
