package stencil.operator.util;

import java.util.Arrays;
import java.util.List;

import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.operator.StencilOperator;
import stencil.parser.tree.TupleRef;
import stencil.parser.tree.Value;

public abstract class BasicProject implements StencilOperator {

	
	/**Invokes the 'query' function for each argument.*/
	public List guide(List<Value> formalArguments, List<Object[]> sourceArguments,  List<String> prototype) {
		if (sourceArguments == null) {
			throw new IllegalArgumentException("Argument list must be non-null and greater than zero length.");
		}
		
		Object[] results = new Object[sourceArguments.size()];
		
		int i=0;
		for (Object[] source: sourceArguments) {
			Object[] actual = packArguments(formalArguments, source, prototype);
			Tuple t = query(actual);
			results[i++] = Tuples.toArray(t);
		}
		return Arrays.asList(results);
	}
	

	
	/**By default, projections never go out of date.
	 * They may, if they are not true functions, but
	 * many are so the default is false.
	 **/
	public boolean refreshGuide() {return false;}

	/**Prepares actual arguments for a method call.  Values are provide either from the literals
	 * in the formals list or using the value source and prototype to resolved names.
	 * 
	 * TODO: This should probably live somewhere else...a general Operator utility space?
	 */
	public static final Object[] packArguments(List<Value> formals, Object[] valueSource, List<String> prototype) {
		int a=0;
		Object[] args = new Object[formals.size()];

		for (Value arg: formals) {
			if (arg.isAtom()) {args[a++] = arg.getValue();}			//copy literals
			else if (arg.isTupleRef()){
				TupleRef ref = (TupleRef)arg;
				int idx = ref.toNumericRef(prototype);
				if (idx ==-1) {throw new RuntimeException(String.format("Error locating field %1$s while working with guide creation (valid fields: %2$s).", ref, Arrays.deepToString(prototype.toArray())));}
				args[a++] = valueSource[idx];
			}	//lookup values in the valueSource
		}
		return args;
	}
}
