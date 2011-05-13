package org.chai.kevin.maps;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.chai.kevin.Objective;
import org.hisp.dhis.indicator.Indicator;

@Entity(name="MapsTarget")
@Table(name="dhsst_maps_target")
public class MapsTarget extends Objective {

	private Indicator indicator;
	
	@ManyToOne(targetEntity=Indicator.class, optional=false)
	public Indicator getIndicator() {
		return indicator;
	}
	
	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}
	
}
