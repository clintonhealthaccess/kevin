package org.chai.kevin.data;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.chai.kevin.value.StoredValue;

@Entity(name="DataElement")
@Table(name="dhsst_data_element")
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class DataElement<T extends StoredValue> extends Data<T> {

	private Type type;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="jsonValue", column=@Column(name="type", nullable=false))
	})
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
}
