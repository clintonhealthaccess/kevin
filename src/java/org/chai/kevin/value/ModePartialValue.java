package org.chai.kevin.value;

import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.Period;
import org.chai.kevin.data.Mode;
import org.chai.kevin.data.ModeMap;
import org.chai.kevin.data.Sum;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocationType;
import org.hibernate.annotations.NaturalId;

@Entity(name="ModePartialValue")
@Table(name="dhsst_value_partial_mode",
	uniqueConstraints = {
		@UniqueConstraint(columnNames={"data", "location", "period", "type"})
	}
)
public class ModePartialValue extends CalculationPartialValue {

	private Mode data;
	private ModeMap modeMap;
	
	public ModePartialValue() {
		super();
	}

	public ModePartialValue(Mode data, CalculationLocation location, Period period, DataLocationType type, ModeMap modeMap, Value value) {
		super(location, period, type, value);
		this.data = data;
		this.modeMap = modeMap;
	}

	public ModePartialValue(Mode data, CalculationLocation location, Period period, DataLocationType type, ModeMap modeMap) {
		super(location, period, type);		
		this.data = data;
		this.modeMap = modeMap;
	}

	@AttributeOverrides({
		@AttributeOverride(name="jsonText", column=@Column(name="modeMap", nullable=false))
	})
	public ModeMap getModeMap() {
		return modeMap;
	}

	public void setModeMap(ModeMap modeMap) {
		this.modeMap = modeMap;
	}
	
	@NaturalId
	@ManyToOne(targetEntity=Mode.class, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public Mode getData() {
		return data;
	}
	
	public void setData(Mode data) {
		this.data = data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ModePartialValue))
			return false;
		ModePartialValue other = (ModePartialValue) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
	
}