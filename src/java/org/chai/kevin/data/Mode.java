package org.chai.kevin.data;

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
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.ModePartialValue;
import org.chai.kevin.value.ModeValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ExpressionService.StatusValuePair;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="Mode")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="dhsst_data_calculation_mode")
public class Mode extends Calculation<ModePartialValue> {
	private static final Log log = LogFactory.getLog(Mode.class);
	
	@Override
	public ModeValue getCalculationValue(List<ModePartialValue> partialValues, Period period, CalculationLocation location) {
		return new ModeValue(partialValues, this, period, location);
	}

	@Override
	@Transient
	public Class<ModePartialValue> getValueClass() {
		return ModePartialValue.class;
	}

	@Override
	public ModePartialValue getCalculationPartialValue(String expression, Map<DataLocation, StatusValuePair> values, CalculationLocation location, Period period, DataLocationType type) {
		ModeMap modeMap = getModeMap(values, location);
		Value value = getValue(modeMap, location);
		return new ModePartialValue(this, location, period, type, modeMap, value);
	}

	private ModeMap getModeMap(Map<DataLocation, StatusValuePair> values, CalculationLocation location) {
		ModeMap modeMap = new ModeMap();
		for (Entry<DataLocation, StatusValuePair> entry : values.entrySet()) {
			if (!entry.getValue().value.isNull()) {
				//convert Value -> String
				String modeJsonValue = entry.getValue().value.getJsonValue();
				Integer modeCount = null;
				if(modeMap.containsKey(modeJsonValue)){
					modeCount = modeMap.get(modeJsonValue);
					if (log.isDebugEnabled()) {
						log.debug("getModeMap(Location="+location+", DataLocation="+entry.getKey().getNames()+
								", Value="+modeJsonValue+", Count="+modeCount+")");
					}
				}
				else  modeCount = 0;
				modeCount++;
				modeMap.put(modeJsonValue, modeCount);
				if (log.isDebugEnabled()) {
					log.debug("getModeMap(DataLocation="+entry.getKey().getNames()+", Value="+entry.getValue().value+
								", modeValue="+modeJsonValue+", modeCount="+modeCount+")");
				}
			}
		}
		return modeMap;
	}
	
	protected Value getValue(ModeMap modeMap, CalculationLocation location) {
		String mode = null;
		Integer maxCount = 0;
		if(modeMap != null){
			for(String modeJsonValue : modeMap.keySet()){
				Integer modeCount = modeMap.get(modeJsonValue);
				if(modeCount > maxCount)
					mode = modeJsonValue;
				if (log.isDebugEnabled()) {
					log.debug("getValue(modeJsonValue="+modeJsonValue+", modeCount="+modeCount+")");
				}
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("getValue(Location="+location+", mode="+mode+")");
		}
		Value value = new Value(mode);
		return value;
	}
	
	@Override
	@Transient
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