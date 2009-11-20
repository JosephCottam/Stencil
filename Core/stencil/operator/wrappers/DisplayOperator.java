package stencil.operator.wrappers;

import java.util.Arrays;
import java.util.List;

import stencil.display.DisplayLayer;
import stencil.operator.StencilOperator;
import stencil.operator.module.OperatorData;
import stencil.operator.module.util.Modules;
import stencil.parser.tree.Value;
import stencil.tuple.Tuple;

/**Wraps a layer as a legend. 
 * Exposes find, makeOrFind, make and remove.
 * 
 * MakeOrFind is used for Map.
 * Find is used for Query.
 */

//Marked final because it is immutable (however, it has mutable components....)
public final class DisplayOperator implements StencilOperator {
	protected DisplayLayer layer;
	protected long cachedSize =-1;
	
	public DisplayOperator(DisplayLayer layer) {this.layer = layer;}
	
	public String getName() {return layer.getName();}

	public Tuple find(String ID) {return layer.find(ID);}
	public Tuple remove(String ID) {layer.remove(ID); return null;}
	
	public Tuple map(Object... args) {return layer.find((String) args[0]);}

	public Tuple query(Object... args) {return map(args);}

	public DisplayOperator duplicate() {throw new UnsupportedOperationException();}

	/**Returns a list of all IDs on the layer.*/
	public List guide(List<Value> formalArguments, List<Object[]> sourceArguments,  List<String> prototype) {
		String[] elements = new String[layer.size()];
		
		int i=0;
		for (Object name: layer) {elements[i++] = (String) name;}
		cachedSize = elements.length;
		return Arrays.asList(elements);
	}

	/**Has the layer size changed since the last time Guide was called?
	 * NOTE: This maybe wrong, since values can be both added and removed...
	 */
	public boolean refreshGuide() {return cachedSize != layer.size();}
	
	public OperatorData getOperatorData(String module) {
		return Modules.basicLegendData(module, getName());
	}
}