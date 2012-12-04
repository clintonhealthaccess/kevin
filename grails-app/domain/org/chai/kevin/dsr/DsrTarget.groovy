package org.chai.kevin.dsr;


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

/**
 * @author Jean Kahigiso M.
 *
 */

import org.chai.kevin.Exportable
import org.chai.kevin.Importable
import org.chai.kevin.data.Data
import org.chai.kevin.data.Type
import org.chai.kevin.reports.AbstractReportTarget
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.reports.ReportTarget
import org.chai.kevin.util.Utils
import org.hibernate.proxy.HibernateProxy

import org.chai.kevin.Exportable
import org.chai.kevin.Importable
import org.chai.kevin.data.Data
import org.chai.kevin.data.Type
import org.chai.kevin.reports.AbstractReportTarget
import org.chai.kevin.util.Utils
import org.hibernate.proxy.HibernateProxy

class DsrTarget extends AbstractReportTarget implements Exportable, Importable {
	
	String format;
	Boolean average; //this can either be an average (true) or sum (null or false)

	static belongsTo = [category: DsrTargetCategory]
	
	static mapping = {
		table 'dhsst_dsr_target'
		category column: 'category'
	}
	
	static constraints =  {
		format (nullable: true)
		average (nullable: true)
	}
	
	@Override
	public Data getData() {
		if (super.getData() instanceof HibernateProxy) {
			return Data.class.cast(((HibernateProxy) super.getData()).getHibernateLazyInitializer().getImplementation());  
		}
		else {
			return Data.class.cast(super.getData());
		}
	}
	
	@Override
	public Type getType() {
		return getData().getType();
	}
		
	@Override
	public DsrTarget fromExportString(Object value) {
		return (DsrTarget) value;
	}
	
}