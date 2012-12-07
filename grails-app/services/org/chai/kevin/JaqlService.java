package org.chai.kevin;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.Value;

import com.ibm.jaql.json.type.JsonValue;
import com.ibm.jaql.lang.JaqlQuery;

public class JaqlService {

	private static final Log log = LogFactory.getLog(JaqlService.class);
	private static final int CACHE_SIZE = 200;
	
	// we implement our own cache because of springcache bug GPSPRINGCACHE-44
	private static class LruCache<A, B> extends LinkedHashMap<A, B> {
		private static final long serialVersionUID = 4576340669871751619L;
	    private final int maxEntries;

	    public LruCache(final int maxEntries) {
	        super(maxEntries + 1, 1.0f, true);
	        this.maxEntries = maxEntries;
	    }

	    /**
	     * Returns <tt>true</tt> if this <code>LruCache</code> has more entries than the maximum specified when it was
	     * created.
	     *
	     * <p>
	     * This method <em>does not</em> modify the underlying <code>Map</code>; it relies on the implementation of
	     * <code>LinkedHashMap</code> to do that, but that behavior is documented in the JavaDoc for
	     * <code>LinkedHashMap</code>.
	     * </p>
	     *
	     * @param eldest
	     *            the <code>Entry</code> in question; this implementation doesn't care what it is, since the
	     *            implementation is only dependent on the size of the cache
	     * @return <tt>true</tt> if the oldest
	     * @see java.util.LinkedHashMap#removeEldestEntry(Map.Entry)
	     */
	    @Override
	    protected boolean removeEldestEntry(final Map.Entry<A, B> eldest) {
	        return super.size() > maxEntries;
	    }
	}

	private static class CacheEntry {
		String expression;
		Map<String, String> variables;

		public CacheEntry(String expression, Map<String, String> variables) {
			super();
			this.expression = expression;
			this.variables = variables;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((expression == null) ? 0 : expression.hashCode());
			result = prime * result
					+ ((variables == null) ? 0 : variables.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof CacheEntry))
				return false;
			CacheEntry other = (CacheEntry) obj;
			if (expression == null) {
				if (other.expression != null)
					return false;
			} else if (!expression.equals(other.expression))
				return false;
			if (variables == null) {
				if (other.variables != null)
					return false;
			} else if (!variables.equals(other.variables))
				return false;
			return true;
		}
	}
	
// 	this is the ehcache config for this cache
//	<!-- short term cache for json values, to speed up survey save -->
//    <cache name="jsonValueCache"
//    		maxElementsInMemory="100"
//    		timeToLiveSeconds="0"
//    		timeToIdleSeconds="10"
//    />
	private Map<CacheEntry, JsonValue> cache = Collections.synchronizedMap(new LruCache<CacheEntry, JsonValue>(CACHE_SIZE));
	
	public static JsonValue jsonValue(String expression, Map<String, String> variables) {
		Map<String, JsonValue> valueMap = new HashMap<String, JsonValue>();
		for (Entry<String, String> variable : variables.entrySet()) {
			JsonValue variableValue = jsonValue(variable.getValue(), new HashMap<String, String>());
			valueMap.put(variable.getKey(), variableValue);
		}

		return executeQuery(expression, valueMap);
	}

	public JsonValue getJsonValue(String expression, Map<String, String> variables) {
		JsonValue result = null;
		
		// we look in the cache
		CacheEntry entry = new CacheEntry(expression, variables);
		if (cache.containsKey(entry)) {
			result = cache.get(entry);
		}
		else {
			Map<String, JsonValue> valueMap = new HashMap<String, JsonValue>();
			for (Entry<String, String> variable : variables.entrySet()) {
				JsonValue variableValue = getJsonValue(variable.getValue(), new HashMap<String, String>());
				valueMap.put(variable.getKey(), variableValue);
			}
	
			result = executeQuery(expression, valueMap);
		}
		
		// we update the cache
		cache.put(entry, result);
		
		return result;
	}
	
	/**
	 * Return null if the expression is not correctly typed or returns null
	 * 
	 * @throws {@link IllegalArgumentException} if one of the arguments is null
	 */
	public Value evaluate(String expression, Type type, Map<String, Value> variables, Map<String, Type> types) throws IllegalArgumentException {
		if (log.isDebugEnabled()) log.debug("evaluate(expression="+expression+", variables="+variables+")");
		
		Map<String, String> jaqlVariables = new HashMap<String, String>();
		for (Entry<String, Value> variable : variables.entrySet()) {
			// value can be null
			if (variable.getValue() != null) {
				String jaqlValue = types.get(variable.getKey()).getJaqlValue(variable.getValue());
				if (jaqlValue.equals("null")) jaqlValue = "\"null\"";
				jaqlVariables.put("$"+variable.getKey(), jaqlValue);
			}
		}
		
		JsonValue jsonValue = getJsonValue(expression, jaqlVariables);
		if (jsonValue == null) return Value.NULL_INSTANCE();
		return type.getValueFromJaql(jsonValue.toString());
	}
	
	private static JsonValue executeQuery(String expression, Map<String, JsonValue> valueMap) throws IllegalArgumentException {
		if (log.isDebugEnabled()) log.debug("executeQuery(expression="+expression+", valueMap="+valueMap+")");	
		
		JsonValue value = null;
		JaqlQuery query = new JaqlQuery();
		
		if (expression.contains("roundup")) {
			try {
				query.registerJavaUDF("roundup", RoundUp.class);
			} catch (Exception e) {
				log.error("could not load roundup method", e);
			}
		}
		query.setQueryString(expression.replaceAll("\\s", " "));
		
		for (Entry<String, JsonValue> entry : valueMap.entrySet()) {
			query.setVar(entry.getKey(), entry.getValue());
		}
		try {
			value = query.evaluate();
		} catch (Exception e) {
			log.warn("error evaluating: "+expression, e);
			throw new IllegalArgumentException("error evaluating: "+expression, e);
		} finally {
			try {query.close();} catch (IOException e) {}
		}
		
		if (log.isDebugEnabled()) log.debug("executeQuery(...)="+value);
		return value;
	}
	
}
