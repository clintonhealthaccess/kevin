package org.chai.kevin.data;

import groovy.transform.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Period;
import org.chai.kevin.dsr.DsrService;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.ModePartialValue;
import org.chai.kevin.value.ModeValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ExpressionService.StatusValuePair;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

public class Mode extends Calculation<ModePartialValue> {
	
	static mapping = {
		table 'dhsst_data_calculation_mode'
	}
	
	/*
	 * Retaining backward compatibility with old getters and setters
	 */
	public Type getType(){
		if (typeString != null && cachedType == null) this.cachedType = new Type(typeString)
		return cachedType
	}
	
	@Override
	public Type getType(CalculationLocation location){
		if(location.collectsData()) return getType();
		else return Type.TYPE_LIST(getType());
	}
	
	@Override
	public ModeValue getCalculationValue(List<ModePartialValue> partialValues, Period period, CalculationLocation location) {
		return new ModeValue(partialValues, this, period, location);
	}

	@Override
	public Class<ModePartialValue> getValueClass() {
		return ModePartialValue.class;
	}

	@Override
	public ModePartialValue getCalculationPartialValue(String expression, Map<DataLocation, StatusValuePair> values, CalculationLocation location, Period period, DataLocationType type) {
		Map modeMap = getModeMap(values, location);
		Value value = getValue(modeMap, location);
		return new ModePartialValue(this, location, period, type, value);
	}

	//used to create the partial value
	private Map getModeMap(Map<DataLocation, StatusValuePair> values, CalculationLocation location) {
		String modeMapString = null;
		Map<String, Value> modeMap = new HashMap<String,Integer>();
		for (Entry<DataLocation, StatusValuePair> entry : values.entrySet()) {
			if (!entry.getValue().value.isNull()) {
				String modeValue = new Value(entry.getValue().value.getJsonValue()).getStringValue();
				Number modeCount = null;
				if(modeMap.containsKey(modeValue)){
					if (log.isDebugEnabled()) {
						log.debug("getModeMap(Location="+location+", DataLocation="+entry.getKey().getNames()+
								", Value="+modeValue+", Count="+modeMap.get(modeValue)+")");
					}
					modeCount = modeMap.get(modeValue).getNumberValue();
					if (log.isDebugEnabled()) {
						log.debug("getModeMap(Location="+location+", DataLocation="+entry.getKey().getNames()+
								", Value="+modeValue+", Count="+modeCount+")");
					}
				}
				else  modeCount = 0;
				modeCount++;
				modeMap.put(modeValue, Value.VALUE_NUMBER(modeCount));
				if (log.isDebugEnabled()) {
					log.debug("getModeMap(DataLocation="+entry.getKey().getNames()+", Value="+entry.getValue().value+
								", modeValue="+modeValue+", modeCount="+modeCount+")");
				}
			}
		}
		if (log.isDebugEnabled()) log.debug("getModeMap(modeMap="+JSONUtils.getJSONFromMap(modeMap)+")");
		return modeMap;
	}
	
	//used to create the partial value
	protected Value getValue(Map modeMap, CalculationLocation location) {
		Value value = Value.VALUE_MAP(modeMap)
		return value;
	}
	
	@Override
	public List<String> getPartialExpressions() {
		List<String> result = new ArrayList<String>();
		result.add(getExpression());
		return result;
	}

	@Override
	public String toString() {
		return "Mode[getId()=" + getId() + ", getCode()="
				+ getCode() + ", getExpression()='" + getExpression() + "']";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}

}