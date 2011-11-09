package org.chai.kevin.data;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="Aggregation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="dhsst_calculation_aggregation")
public class Aggregation extends Calculation {

	private static final long serialVersionUID = -633638638981261851L;
	
}
