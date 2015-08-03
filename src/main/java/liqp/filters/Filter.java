package liqp.filters;

import liqp.LValue;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Output markup takes filters. Filters are simple methods. The first
 * parameter is always the output of the left side of the filter. The
 * return value of the filter will be the new left value when the next
 * filter is run. When there are no more filters, the template will
 * receive the resulting string.
 * <p/>
 * -- https://github.com/Shopify/liquid/wiki/Liquid-for-Designers
 */
public abstract class Filter extends LValue {

    /**
     * A list holding standard filters.
     */
	private static final Filter []standardFiltersList = {
        new Append(),
        new Capitalize(),
        new Date(),
        new Divided_By(),
        new Downcase(),
        new Escape(),
        new Escape_Once(),
        new First(),
        new H(),
        new Join(),
        new Last(),
        new liqp.filters.Map(),
        new Minus(),
        new Modulo(),
        new Plus(),
        new Prepend(),
        new Remove(),
        new Remove_First(),
        new Replace(),
        new Replace_First(),
        new Size(),
        new Sort(),
        new Split(),
        new Strip_HTML(),
        new Strip_Newlines(),
        new Times(),
        new Truncate(),
        new Truncatewords(),
        new Upcase()
	};

    /**
     * A map holding all filters.
     */
    private static final ThreadLocal<Map<String, Filter>> FILTERS = new ThreadLocal<Map<String, Filter>>() {
    	/* (non-Javadoc)
    	 * @see java.lang.ThreadLocal#initialValue()
    	 */
    	protected java.util.Map<String,Filter> initialValue() {
    		Map<String, Filter> standardFilters = new ConcurrentHashMap<String, Filter>();
    		for(Filter filter: standardFiltersList) {
    	        // Initialize all standard filters.
    			standardFilters.put(filter.name, filter);
    		}
    		return standardFilters;
    	}
    };

    /**
     * The name of the filter.
     */
    public final String name;

    /**
     * Used for all package protected filters in the liqp.filters-package
     * whose name is their class name lower cased.
     */
    protected Filter() {
        this.name = this.getClass().getSimpleName().toLowerCase();
    }

    /**
     * Creates a new instance of a Filter.
     *
     * @param name
     *         the name of the filter.
     */
    public Filter(String name) {
        this.name = name;
    }

    /**
     * Applies the filter on the 'value'.
     *
     * @param value
     *         the string value `AAA` in: `{{ 'AAA' | f:1,2,3 }}`
     * @param params
     *         the values [1, 2, 3] in: `{{ 'AAA' | f:1,2,3 }}`
     *
     * @return the result of the filter.
     */
    public abstract Object apply(Object value, Object... params);

    /**
     * Check the number of parameters and throws an exception if needed.
     *
     * @param params
     *         the parameters to check.
     * @param expected
     *         the expected number of parameters.
     */
    public final void checkParams(Object[] params, int expected) {
        if(params == null || params.length != expected) {
            throw new RuntimeException("Liquid error: wrong number of arguments (" +
                    (params.length + 1) + " for " + (expected + 1) + ")");
        }
    }

    /**
     * Returns a value at a specific index from an array of parameters.
     * If no such index exists, a RuntimeException is thrown.
     *
     * @param index
     *         the index of the value to be retrieved.
     * @param params
     *         the values.
     *
     * @return a value at a specific index from an array of
     *         parameters.
     */
    protected Object get(int index, Object... params) {

        if (index >= params.length) {
            throw new RuntimeException("error in filter '" + name +
                    "': cannot get param index: " + index +
                    " from: " + Arrays.toString(params));
        }

        return params[index];
    }

    /**
     * Retrieves a filter with a specific name.
     *
     * @param name
     *         the name of the filter to retrieve.
     *
     * @return a filter with a specific name.
     */
    public static Filter getFilter(String name) {

        Filter filter = FILTERS.get().get(name);

        if (filter == null) {
            throw new RuntimeException("unknown filter: " + name);
        }

        return filter;
    }

    /**
     * Registers a new filter.
     *
     * @param filter
     *         the filter to be registered.
     */
    public static void registerFilter(Filter filter) {
        FILTERS.get().put(filter.name, filter);
    }
}
