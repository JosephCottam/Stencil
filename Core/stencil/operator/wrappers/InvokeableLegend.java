package stencil.operator.wrappers;
 
import java.lang.reflect.Method;
import java.util.List;
import java.util.Arrays;

import stencil.operator.util.BasicProject;
import stencil.operator.util.Invokeable;
import stencil.parser.tree.Value;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;


/**Wraps a Method/Target pair as a legend.
 * Calls to Query and Map both go to the same method.
 * 
 * TODO: Do smart things so we don't need this anymore!
 */

//Marked final because it is immutable (however, it has mutable components....)
public final class InvokeableLegend extends BasicProject {
	protected String name;
	protected Invokeable<?, Tuple> source;
	protected boolean allowAutoGuide = true;
	
	public InvokeableLegend(String name, Method method) {this(name, method, null);}
	public InvokeableLegend(String name, Method method, Object target) {this(name, new Invokeable(method, target));}
	public InvokeableLegend(String name, String method, Object target) throws NoSuchMethodException {this(name, new Invokeable(method, target));}
	public InvokeableLegend(String name, String method, Class source) throws NoSuchMethodException {this(name, new Invokeable(method, source));}

	public InvokeableLegend(String name, Invokeable source) {
		this.name = name;
		this.source = source;
	}
	
	public String getName() {return name;}
	
	public Tuple map(Object... args) {return invoke(args);}
	public Tuple query(Object... args) {return invoke(args);}
	
	private Tuple invoke(Object...arguments) {return  source.invoke(arguments);}

	
	public InvokeableLegend duplicate() {
		if (source.isStatic()) {return this;}
		throw new UnsupportedOperationException();
	}
	
	public void allowAutoGuide(boolean allowAutoGuide) {this.allowAutoGuide = allowAutoGuide;}
	
	/**Invokes the 'query' function for each argument.*/
	public List guide(List<Value> formalArguments, List<Object[]> argumentSets,  List<String> prototype) {
		if (!allowAutoGuide) {throw new UnsupportedOperationException(String.format("Cannot use synthetic legend %1$s in guide.", name));}
		
		if (argumentSets == null) {throw new IllegalArgumentException("Argument list must be non-null.");}
		
		Object[] results = new Object[argumentSets.size()];
		
		int i=0;
		for (Object[] argSet: argumentSets) {
			Object[] actual = packArguments(formalArguments, argSet, prototype);
			Tuple t = query(actual);
			results[i++] = Tuples.toArray(t);
		}
		return Arrays.asList(results);
	}
	
	public boolean refreshGuide() {
		if (!allowAutoGuide) {throw new UnsupportedOperationException(String.format("Cannot use synthetic legend %1$s in guide.", name));}
		return false;
	}

}

