package stencil.adapters.java2D.render.guides;

import static stencil.parser.ParserConstants.NAME_SEPARATOR_PATTERN;
import static stencil.parser.ParserConstants.NAME_SEPARATOR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.PrototypedArrayTuple;
import stencil.tuple.prototype.TuplePrototypes;

public class GuideUtil {
	/**Given an update prototype, inserts an implantation for each prefix encountered.  
	 * Assumes the tuple is not nested (so fields appear as name.subName).
	 * Will override existing implantations.
	 * 
	 * @param t        Tuple to modify
	 * @param implant  What to set the implantation to
	 * @param exclude  Prefixes not to add an implantation to (if any)
	 * @return
	 */
	public static PrototypedTuple fullImplant(PrototypedTuple t, String implant, String... exclude) {
		List excludes = Arrays.asList(exclude);
		List<String> fields = new ArrayList();
		List<String> implants = new ArrayList();
		
		for (String field: TuplePrototypes.getNames(t.prototype())) {
			String[] parts = field.split(NAME_SEPARATOR_PATTERN);
			
			if (parts.length == 1) {continue;}
			if (excludes.contains(parts[0])) {continue;}
			
			String newName = parts[0] + NAME_SEPARATOR + "IMPLANT"; 
			if (!fields.contains(newName)) {
				fields.add(newName);
				implants.add(implant);
			}
		}
		return Tuples.merge(t, new PrototypedArrayTuple(fields, implants));
		
	}
}
