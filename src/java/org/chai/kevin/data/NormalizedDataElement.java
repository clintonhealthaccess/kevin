package org.chai.kevin.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

import org.chai.kevin.Period;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.value.ExpressionService;
import org.chai.kevin.value.NormalizedDataElementValue;

@Entity(name="NormalizedDataElement")
@Table(name="dhsst_data_normalized_element")
public class NormalizedDataElement extends DataElement<NormalizedDataElementValue> {

	// json text example : {"1":{"DH":"$1 + $2"}, "2":{"HC":"$1 + $2 + $3"}}
	private ExpressionMap expressionMap = new ExpressionMap();
	private Date refreshed;
	
	@AttributeOverrides({
		@AttributeOverride(name="jsonText", column=@Column(name="expressionMap", nullable=false))
	})
	public ExpressionMap getExpressionMap() {
		return expressionMap;
	}
	
	public void setExpressionMap(ExpressionMap expressionMap) {
		this.expressionMap = expressionMap;
	}
	
	@Column(nullable=true, columnDefinition="datetime")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	public Date getRefreshed() {
		return refreshed;
	}
	
	public void setRefreshed(Date refreshed) {
		this.refreshed = refreshed;
	}
	
	@Transient
	public String getExpression(Period period, String typeCode) {
		if (!expressionMap.containsKey(Long.toString(period.getId()))) return null;
		return expressionMap.get(Long.toString(period.getId())).get(typeCode);
	}
	
	@Transient
	public Set<String> getExpressions() {
		Set<String> expressions = new HashSet<String>();
		for (Map<String, String> groupMap : expressionMap.values()) {
			for (String expression : groupMap.values()) {
				expressions.add(expression);
			}
		}
		return expressions;
	}

	@Override
	@Transient
	public Class<NormalizedDataElementValue> getValueClass() {
		return NormalizedDataElementValue.class;
	}
	
	@Override
	public String toString() {
		return "NormalizedDataElement[getId()=" + getId() + ", getCode()=" + getCode() + "]";
	}

}
