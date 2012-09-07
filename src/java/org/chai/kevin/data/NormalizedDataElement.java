package org.chai.kevin.data;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.NormalizedDataElementValue;

@Entity(name="NormalizedDataElement")
@Table(name="dhsst_data_normalized_element")
public class NormalizedDataElement extends DataElement<NormalizedDataElementValue> {

	// json text example : {"1":{"DH":"$1 + $2"}, "2":{"HC":"$1 + $2 + $3"}}
	private ExpressionMap expressionMap = new ExpressionMap();
	private SourceMap sourceMap = new SourceMap();
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
	
	@AttributeOverrides({
		@AttributeOverride(name="jsonText", column=@Column(name="sourceMap", nullable=false))
	})
	public SourceMap getSourceMap() {
		return sourceMap;
	}
	
	public void setSourceMap(SourceMap sourceMap) {
		this.sourceMap = sourceMap;
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
	
	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}

	@Transient
	@Override
	public Set<String> getSources(Period period, DataLocationType type) {
		Set<String> result = new HashSet<String>();
		if (sourceMap.containsKey(period.getId()+"") && sourceMap.get(period.getId()+"").containsKey(type.getCode())) {
			result.addAll(sourceMap.get(period.getId()+"").get(type.getCode()));
		}
		return result;
	}
	
	@Transient
	@Override
	public Set<String> getSources() {
		Set<String> result = new HashSet<String>();
		for (Map<String, List<String>> map : sourceMap.values()) {
			for (List<String> sources : map.values()) {
				result.addAll(sources);
			}
		}
		return result;
	}
	
}
