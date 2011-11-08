package org.chai.kevin

import java.util.Comparator;
import org.chai.kevin.Ordering.OrderingComparator;
import grails.plugin.spock.UnitSpec;

class OrderableSpec extends UnitSpec {

	static class TestOrderable extends Orderable<Ordering> {
		Long id;
		Ordering order;
		
		Ordering getOrder() {
			return order;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof TestOrderable))
				return false;
			TestOrderable other = (TestOrderable) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
	}
	
	Comparator<TestOrderable> getComparator(final String currentLanguage, final String fallbackLanguage) {
		return new Comparator<TestOrderable>() {
			private OrderingComparator orderingComparator = new OrderingComparator(currentLanguage, fallbackLanguage);
			@Override
			public int compare(TestOrderable o1, TestOrderable o2) {
				return orderingComparator.compare(o1.getOrder(), o2.getOrder());
			}
		};
	}
	
	
	def "order correct"() {
		setup:
		def item1 = new TestOrderable(id: 1, order: o(["en":1, "fr":2]))
		def item2 = new TestOrderable(id: 2, order: o(["en":2, "fr":1]))
		def item3 = new TestOrderable(id: 3, order: o(["en":3, "fr":3]))
		def list = [item2, item3, item1]
		
		when:
		Collections.sort(list, getComparator("en", "en"))
		
		then:
		list == [item1, item2, item3]
		
		when:
		Collections.sort(list, getComparator("fr", "en")) 
		
		then:
		list == [item2, item1, item3]
	}
	
	def "fallback language used correctly"() {
		setup:
		def item1 = new TestOrderable(id: 1, order: o(["en":1, "fr":3]))
		def item2 = new TestOrderable(id: 2, order: o(["en":2]))
		def item3 = new TestOrderable(id: 3, order: o(["en":3, "fr":1]))
		def list = [item2, item3, item1]
		
		when:
		Collections.sort(list, getComparator("fr", "en"))
		
		then:
		list == [item3, item2, item1]
	}
	
	def "ordering works when no order"() {
		setup:
		def item1 = new TestOrderable(id: 1, order: o([:]))
		def item2 = new TestOrderable(id: 2, order: o(["en":2]))
		def item3 = new TestOrderable(id: 3, order: o(["en":3, "fr":1]))
		def list = [item2, item3, item1]
		
		when:
		Collections.sort(list, getComparator("fr", "en"))
		
		then:
		list == [item3, item2, item1]
	}
	
	static o(def map) {
		return Initializer.o(map)
	}
}
