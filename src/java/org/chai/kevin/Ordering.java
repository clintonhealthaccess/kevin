package org.chai.kevin;

import java.util.Comparator;

import javax.persistence.Embeddable;

import org.chai.kevin.json.JSONMap;


@Embeddable
public class Ordering extends JSONMap<Integer> implements Comparable<Ordering>, Exportable, Importable {

	private static final long serialVersionUID = 1179476928310670136L;

	public Ordering() {
		super();
	}
	
	public Ordering(JSONMap<Integer> jsonMap) {
		super(jsonMap);
	}
	
	private Integer getOrder(String currentLanguage, String fallbackLanguage) {
		if (containsKey(currentLanguage)) return get(currentLanguage);
		else return get(fallbackLanguage);
	}

	public static class OrderingComparator implements Comparator<Ordering> {

		private String currentLanguage;
		private String fallbackLanguage;
		
		public OrderingComparator(String currentLanguage, String fallbackLanguage) {
			this.currentLanguage = currentLanguage;
			this.fallbackLanguage = fallbackLanguage;
		}
		
		@Override
		public int compare(Ordering ordering0, Ordering ordering1) {
			if (ordering0.getOrder(currentLanguage, fallbackLanguage) == null 
				&& ordering1.getOrder(currentLanguage, fallbackLanguage) == null) {
				return 0;
			}
			else if (ordering0.getOrder(currentLanguage, fallbackLanguage) == null) {
				return -1;
			}
			else if (ordering1.getOrder(currentLanguage, fallbackLanguage) == null) {
				return 1;
			}
			else return ordering0.getOrder(currentLanguage, fallbackLanguage).compareTo(ordering1.getOrder(currentLanguage, fallbackLanguage));
		}
		
	}
	
	public static Comparator<Orderable<Ordering>> getOrderableComparator(final String currentLanguage, final String fallbackLanguage) {
		return new Comparator<Orderable<Ordering>>(){
			private OrderingComparator orderingComparator = new OrderingComparator(currentLanguage, fallbackLanguage);
			@Override
			public int compare(Orderable<Ordering> arg0, Orderable<Ordering> arg1) {
				return orderingComparator.compare(arg0.getOrder(), arg1.getOrder());
			}
		};
	}

	@Override
	public int compareTo(Ordering o) {
		return 0;
	}
	
	@Override
	public String toExportString() {
		return super.toExportString();
	}

	@Override
	public Ordering fromExportString(Object value) {
		JSONMap jsonMap = super.fromExportString(value);
		Ordering ordering = new Ordering(jsonMap);
		return ordering;
	}
}