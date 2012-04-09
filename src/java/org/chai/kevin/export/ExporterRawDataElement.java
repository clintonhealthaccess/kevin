/**
 * 
 */
package org.chai.kevin.export;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.chai.kevin.data.RawDataElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author JeanKahigiso
 *
 */
@Entity(name="ExportRawDataElement")
@Table(name="dhsst_exporter_data_raw_element")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class ExporterRawDataElement extends Exporter {
	
	private List<RawDataElement> dataElements;
	
	@ManyToOne(targetEntity=RawDataElement.class)
	@JoinColumn(nullable= false)
	public List<RawDataElement> getDataElements() {
		return dataElements;
	}

	public void setDataElements(List<RawDataElement> dataElements) {
		this.dataElements = dataElements;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((dataElements == null) ? 0 : dataElements.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExporterRawDataElement other = (ExporterRawDataElement) obj;
		if (dataElements == null) {
			if (other.dataElements != null)
				return false;
		} else if (!dataElements.equals(other.dataElements))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExporterRawDataElement [dataElements=" + dataElements + "]";
	}

	
	
}
