package org.chai.kevin.value;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Period;
import org.chai.kevin.data.Mode;
import org.chai.kevin.data.ModeMap;
import org.chai.kevin.location.CalculationLocation;

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
		//data Location
		if (getLocation().collectsData()) {
			return getDataLocationValue();
		}
		//location
		ModeMap modeMap = null;
		for (ModePartialValue partialValue : getCalculationPartialValues()) {
			if (log.isDebugEnabled()) log.debug("partialValue.value(partialValue="+partialValue.getValue()+")");
			if (!partialValue.getValue().isNull()) {
				// exclude null values from mode
				if (modeMap == null) 
					modeMap = new ModeMap();
				ModeMap partialValueModeMap = partialValue.getModeMap();
				for(String partialValueModeJsonValue : partialValueModeMap.keySet()){
					Integer modeCount = null;
					if(modeMap.containsKey(partialValueModeJsonValue))
						modeCount = modeMap.get(partialValueModeJsonValue);
					else
						modeCount = 0;
					modeCount++;
					modeMap.put(partialValueModeJsonValue, modeCount);
					if (log.isDebugEnabled()) log.debug("partialValue.modeMap(modeJsonValue="+partialValueModeJsonValue+", modeCount="+modeCount+")");
				}
			}
		}
		String mode = null;
		Integer maxCount = 0;
		if(modeMap != null){
			for(String modeValue : modeMap.keySet()){
				Integer modeCount = modeMap.get(modeValue);
				if(modeCount > maxCount)
					mode = modeValue;
				if (log.isDebugEnabled()) log.debug("modeValue.modeMap(modeValue="+modeValue+", modeCount="+modeCount+")");
			}
		}
		Value value = new Value(mode);
		return value;
	}
	
	private Value getDataLocationValue(){
		if (getCalculationPartialValues().size() > 1) throw new IllegalStateException("Calculation for DataLocation does not contain only 1 partial value");
		if (getCalculationPartialValues().size() == 0) return Value.NULL_INSTANCE();
		return getCalculationPartialValues().get(0).getValue();
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