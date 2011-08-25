package org.chai.kevin.data;

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
  */
  
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.ValueCalculator;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

@Entity(name="Calculation")
@Table(name="dhsst_calculation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Calculation extends Data<CalculationValue> {

	private static final long serialVersionUID = -633638638981261851L;
	
	private Map<String, Expression> expressions = new HashMap<String, Expression>();
	
	@ManyToMany(targetEntity=Expression.class)
	@JoinTable(name="dhsst_calculation_expression", 
		inverseJoinColumns=@JoinColumn(name="expression", nullable=true)
	)
	@MapKeyColumn(name="groupUuid")
	public Map<String, Expression> getExpressions() {
		return expressions;
	}
	
	public void setExpressions(Map<String, Expression> expressions) {
		this.expressions = expressions;
	}

	@Override
	public String toString() {
		return "Calculation [expressions=" + expressions + "]";
	}

	@Override
	public CalculationValue getValue(ValueCalculator calculator, OrganisationUnit organisationUnit, Period period) {
		return calculator.getValue(this, organisationUnit, period);
	}
	
//	@Transient
//	public ValueType getType() throws IllegalStateException {
//		ValueType result = null;
//		for (Expression expression : expressions.values()) {
//			if (result == null) result = expression.getType();
//			else {
//				if (result != expression.getType()) throw new IllegalStateException("calculation contains expressions of different formats");
//			}
//		}
//		return result;
//	}
	
	
}
