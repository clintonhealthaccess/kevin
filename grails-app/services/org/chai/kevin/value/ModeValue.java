package org.chai.kevin.value;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.JaqlService;
import org.chai.kevin.Period;
import org.chai.kevin.data.Mode;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.util.Utils;
import org.chai.location.CalculationLocation;

import com.ibm.jaql.json.type.JsonValue;

public class ModeValue extends CalculationValue<ModePartialValue> {
	private static final Log log = LogFactory.getLog(ModeValue.class);
	
	public ModeValue(Set<ModePartialValue> calculationPartialValues, Mode calculation, Period period, CalculationLocation location) {
		super(new ArrayList<ModePartialValue>(calculationPartialValues), calculation, period, location);
	}
	
	public ModeValue(List<ModePartialValue> calculationPartialValues, Mode calculation, Period period, CalculationLocation location) {
		super(calculationPartialValues, calculation, period, location);
	}

	@Override
	public boolean isNull(){
		return getValue().isNull();
	}
	
	@Override
	public Value getValue() {
		String maxMode = null;
		
		//data Location
		if (getLocation().collectsData()) {
			if (getCalculationPartialValues().get(0).getValue().getMapValue().size() != 1) 
				throw new IllegalStateException("DataLocation must contain only 1 calculation partial value");
			else if (getCalculationPartialValues().get(0).getValue().getMapValue().size() > 0) {
				Map<String, Value> modeMap = getCalculationPartialValues().get(0).getValue().getMapValue();
				if(modeMap != null){
					for(String modeValue : modeMap.keySet()){
						maxMode = modeValue;
						if (log.isDebugEnabled()) log.debug("partialValue.dataLocation(modeValue="+modeValue+")");
					}
				}
			}
			if (log.isDebugEnabled()) log.debug("partialValue.dataLocation(maxMode="+maxMode+")");
		}
		//location
		else{
			Map<String,Double> modeMap = null;
			for (ModePartialValue partialValue : getCalculationPartialValues()) {
				if (log.isDebugEnabled()) log.debug("partialValue.location(partialValue="+partialValue.getValue()+")");
				if (!partialValue.getValue().isNull()) {
					// exclude null values from mode
					if (modeMap == null) 
						modeMap = new HashMap<String,Double>();
					Map<String,Value> partialValueModeMap =  partialValue.getValue().getMapValue();
					for(String partialModeValue : partialValueModeMap.keySet()){
						Double modeCount = null;
						Double partialModeCount = null;
						if(modeMap.containsKey(partialModeValue)){
							modeCount = modeMap.get(partialModeValue);
							partialModeCount = partialValueModeMap.get(partialModeValue).getNumberValue().doubleValue();
							modeCount += partialModeCount;
						}
						else
							modeCount = 1d;
						modeMap.put(partialModeValue, modeCount);
						if (log.isDebugEnabled()) log.debug("partialValue.location(modeJsonValue="+partialModeValue+", modeCount="+modeCount+")");
					}
				}
			}
			
			Integer maxCount = 0;
			if(modeMap != null){
				for(String modeValue : modeMap.keySet()){
					Double modeCount = modeMap.get(modeValue);
					if(modeCount > maxCount)
						maxMode = modeValue;
					if (log.isDebugEnabled()) log.debug("modeValue.location(modeValue="+modeValue+", modeCount="+modeCount+")");
				}
			}
			if (log.isDebugEnabled()) log.debug("modeValue.location(maxMode="+maxMode+", maxCount="+modeMap.get(maxMode)+")");
		}
		
		Value value = getData().getType().getValueFromJaql(maxMode);
		if (log.isDebugEnabled()) log.debug("modeValue.modeMap(value="+value+")");
		return value;
	}
	
	@Override
	public String toString() {
		return "ModeValue [getValue()=" + getValue() + "]";
	}

	@Override
	public Date getTimestamp() {
		return null;
	}

	@Override
	public Value getAverage() {
		// TODO Auto-generated method stub
		return null;
	}
	
}