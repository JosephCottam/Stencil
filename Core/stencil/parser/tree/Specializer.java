/* Copyright (c) 2006-2008 Indiana University Research and Technology Corporation.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * - Neither the Indiana University nor the names of its contributors may be used
 *  to endorse or promote products derived from this software without specific
 *  prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package stencil.parser.tree;

import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.Token; 
import org.antlr.runtime.tree.TreeAdaptor;

import stencil.parser.string.StencilParser;

public class Specializer extends StencilTree {
	private MapEntry.MapList map;
	
	/**Customary key used to store range descriptor.*/
	public static final String RANGE = "range";
	
	/**Customary key used to store split descriptor.*/
	public static final String SPLIT = "split";
	
	public Specializer(Token source) {super(source);}

	/**Direct access to the map arguments.*/
	public Atom get(String key) {return getMap().get(key);}

	/**Direct access to the map key query.*/
	public boolean containsKey(String key) {return getMap().containsKey(key);}
	
	/**What map arguments were passed to the specializer?*/
	public MapEntry.MapList getMap() {
		if (map == null) {map =new MapEntry.MapList((List<MapEntry>) getChild(0));}
		return map;
	}
	
	/**A simple specializer has no arguments.*/
	public boolean isSimple() {return getMap().size()==0;}

	/**A specializer with at most a Range and Split argument.*/
	public boolean isBasic() {
		return isSimple()
		    || (getMap().size() == 1 && containsKey(RANGE)) 
			|| (getMap().size() ==1 && containsKey(SPLIT))
			|| (getMap().size() == 2 && (containsKey(RANGE) && containsKey(SPLIT)));
	}

	
	public boolean equals(Object other) {
		if (this == other) {return true;}

		if (other == null || !(other instanceof Specializer)) {return false;}
		Specializer alter = (Specializer) other;

		if (this.getChild(0).getText().equals("DEFAULT") && alter.getChild(0).getText().equals("DEFAULT")) {return true;} //both are default 
		if (this.getChild(0).getText().equals("DEFAULT") || alter.getChild(0).getText().equals("DEFAULT")) {return false;}//only one is default
		
		
		return allArgsEqual(this, alter);
	}
	
	@Override
	public int hashCode() {
		return hashArgs();
	}

	private final int hashArgs() {
		int acc=1;
		for (Atom arg: getMap().values()) {acc = acc*arg.hashCode();}
		for (String arg: getMap().keySet()) {acc = acc*arg.hashCode();}
 		return acc;
	}
	
	/**Compare the argument lists.*/
	private static final boolean allArgsEqual(Specializer one, Specializer two) {
		java.util.Map mapOne = one.getMap();
		java.util.Map mapTwo = two.getMap();
		

		if (mapOne.size() != mapTwo.size()) {return false;}
		
		for (Object key: mapOne.keySet()) {
			Object v1 = mapOne.get(key);
			Object v2 = mapTwo.get(key);
			if (v1 != v2 && v1 != null && !v1.equals(v2)) {return false;}
		}
		return true;
	}
	
	public static Specializer blendMaps(Specializer defaults, Specializer update, TreeAdaptor adaptor) {
		Specializer result = (Specializer) adaptor.dupTree(update);
		StencilTree mapList = (StencilTree) adaptor.create(StencilParser.LIST, "<map args>");

		Map<String, Atom> entries = new HashMap();
		entries.putAll(defaults.getMap());
		entries.putAll(update.getMap());

		for (String key: entries.keySet()) {
			MapEntry entry = (MapEntry) adaptor.create(StencilParser.MAP_ENTRY, key);
			adaptor.addChild(entry, adaptor.dupTree(entries.get(key)));
			adaptor.addChild(mapList, entry);
		}

		int mapIdx = result.getMap().getSource().getChildIndex();    
		adaptor.replaceChildren(result, mapIdx, mapIdx, mapList);
		return result;
	}
}
