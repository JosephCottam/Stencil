package stencil.module.operator.util;

import java.util.Arrays;
import java.util.Collection;
import static java.lang.String.format;

/**Indicate that a facet is now known.*/
public class UnknownFacetException extends IllegalArgumentException {
	public UnknownFacetException(String operator, String facet, Collection<String> facets) {
		super(format("Facet '%1$s' unknown on %2$s (valid facets: %3$s).", facet, operator, Arrays.deepToString(facets.toArray())));
	}
}
