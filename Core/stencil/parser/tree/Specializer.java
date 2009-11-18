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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

import org.antlr.runtime.Token; 

public class Specializer extends StencilTree {

	/**Immutable map based on a list of Stencil Map object.*/
	private static final class MapList implements Map<String, Atom> {
		/**Class for handling key requests.  Immutable and backed by the same source as the list.*/
		private final class KeySet implements Set<String> {
			protected KeySet() {super();}

			public boolean contains(Object o) {return (search(o)) != null;}

			public boolean containsAll(Collection<?> c) {
				for (Object o: c) {
					if (search(o) == null) {return false;}
				}
				return true;
			}

			public boolean isEmpty() {return source.size() == 0;}

			public Iterator<String> iterator() {
				return new Iterator<String>() {
					int idx =0;
					public boolean hasNext() {return idx < source.size();}

					public String next() {return source.get(idx++).getKey();}
					public void remove() {throw new UnsupportedOperationException();}
				};
			}

			public boolean add(String o) {throw new UnsupportedOperationException();}
			public boolean addAll(Collection<? extends String> c) {throw new UnsupportedOperationException();}
			public void clear() {throw new UnsupportedOperationException();}
			public boolean remove(Object o) {throw new UnsupportedOperationException();}
			public boolean removeAll(Collection<?> c) {throw new UnsupportedOperationException();}
			public boolean retainAll(Collection<?> c) {throw new UnsupportedOperationException();}
			public int size() {return source.size();}

			public Object[] toArray() {
				Object[] os = new Object[this.size()];
				int i =0;
				for (Object o: this){os[i++] =o;}
				return os;
			}

			public <T> T[] toArray(T[] a) {throw new UnsupportedOperationException();}
		}

		/**Class for handling entry set requests. Immutable, and backed by the same source as the list.*/
		private final class EntrySet implements Set<Map.Entry<String, Atom>> {
			protected EntrySet() {super();}
			public boolean contains(Object o) {return source.contains(o);}

			public boolean containsAll(Collection<?> c) {return source.containsAll(c);}
			public boolean isEmpty() {return source.isEmpty();}
			
			public Iterator<Map.Entry<String, Atom>> iterator() {
				return new Iterator<Map.Entry<String, Atom>>() {
					int idx =0;
					public boolean hasNext() {return idx < source.size();}

					public MapEntry next() {return source.get(idx++);}
					public void remove() {throw new UnsupportedOperationException();}
				};
			}
			
			public int size() {return source.size();}

			public Object[] toArray() {
				Object[] os = new Object[this.size()];
				int i =0;
				for (Object o: this){os[i++] =o;}
				return os;
			}

			public <T> T[] toArray(T[] a) {throw new UnsupportedOperationException();}

			public boolean remove(Object o) {throw new UnsupportedOperationException();}
			public boolean removeAll(Collection<?> c) {throw new UnsupportedOperationException();}
			public boolean retainAll(Collection<?> c) {throw new UnsupportedOperationException();}
			public boolean add(Map.Entry<String, Atom> o) {throw new UnsupportedOperationException();}
			public boolean addAll(Collection<? extends Map.Entry<String, Atom>> c) {throw new UnsupportedOperationException();}
			public void clear() {throw new UnsupportedOperationException();}
		}

		/**Class for handling value set requests. Immutable, and backed by the same source as the list.*/
		private final class Values implements Collection<Atom> {
			protected Values() {super();}

			public boolean isEmpty() {return source.size() ==0;}
			public int size() {return source.size();}

			public Iterator<Atom> iterator() {
				return new Iterator<Atom>() {
					int idx=0;		
					public boolean hasNext() {return idx<source.size();}
					public Atom next() {return source.get(idx++).getValue();}
					public void remove() {throw new UnsupportedOperationException();}					
				};
			}


			public Object[] toArray() {
				Object[] os = new Object[source.size()];
				int i=0;
				for (MapEntry o: source) {os[i++] = o.getValue();}
				return os;
			}

			public boolean contains(Object o) {
				for (Atom a: this) {
					if (a.getValue().equals(o)) {return true;}
				}
				return false;
			}
			
			public <T> T[] toArray(T[] a) {throw new UnsupportedOperationException();}
			public boolean add(Atom o) {throw new UnsupportedOperationException();}
			public boolean addAll(Collection<? extends Atom> c) {throw new UnsupportedOperationException();}
			public void clear() {throw new UnsupportedOperationException();}
			public boolean containsAll(Collection<?> c) {throw new UnsupportedOperationException();}
			public boolean remove(Object o) {throw new UnsupportedOperationException();}
			public boolean removeAll(Collection<?> c) {throw new UnsupportedOperationException();}
			public boolean retainAll(Collection<?> c) {throw new UnsupportedOperationException();}
			
		}
		
		
		protected final List<MapEntry> source;
		protected EntrySet entries = new EntrySet();
		protected KeySet keySet = new KeySet();
		protected Values values = new Values();
		
		public MapList(List<MapEntry> source) {
			this.source = source;
		}
		public void clear() {throw new UnsupportedOperationException();}

		public boolean containsKey(Object key) {return search(key)!= null;}
		
		public Atom get(Object key) {
			MapEntry m = search(key);
			if (m == null) {throw new IllegalArgumentException("Element '" + key + "' not found in specializer map.");}
			return m.getValue();
		}
		public boolean isEmpty() {return source.size() ==0;}

		public Set<String> keySet() {return keySet;}

		public int size() {return source.size();}
		
		public Set<Map.Entry<String, Atom>> entrySet() {return entries;}
			
		protected MapEntry search(Object key) {
			if (!(key instanceof String)) {return null;} 
			
			for (MapEntry m: source) {
				if (m.getKey().equals(key)) {return m;} 
			}
			return null;
		}

		public Collection<Atom> values() {return values;}
		
		public boolean containsValue(Object value) {return values.contains(value);}
		
		public Atom put(String key, Atom value) {throw new UnsupportedOperationException();}
		public void putAll(java.util.Map<? extends String, ? extends Atom> t) {throw new UnsupportedOperationException();}
		public Atom remove(Object key) {throw new UnsupportedOperationException();}

	}
	
	
	public Specializer(Token source) {super(source);}

	/**Is the split before or after the range?*/
	public boolean isPreSplit() {return false;} //TODO: implement pre/post split...

	/**What is the range argument?*/
	public Range getRange() {return (Range) getChild(0);}

	/**What is the split argument?*/
	public Split getSplit() {return (Split) getChild(1);}
	
	/**What additional arguments were passed to the specializer?*/
	public java.util.List<Atom> getArgs() {return (List<Atom>) getChild(2);}

	public java.util.Map<String, Atom> getMap() {return new MapList((List<MapEntry>) getChild(3));}
	
	//TODO: Remove, eventually....
	public boolean isSimple() {
		return getRange().isSimple()
				&& !getSplit().hasSplitField()
				&& getArgs().size() ==0
				&& getMap().size() ==0;
	}
	
	public boolean equals(Object other) {
		if (this == other) {return true;}

		if (other == null || !(other instanceof Specializer)) {return false;}
		Specializer alter = (Specializer) other;

		if (this.getChild(0).getText().equals("DEFAULT") && alter.getChild(0).getText().equals("DEFAULT")) {return true;} //both are default 
		if (this.getChild(0).getText().equals("DEFAULT") || alter.getChild(0).getText().equals("DEFAULT")) {return false;}//only one is default
		
		
		return this.isPreSplit() == alter.isPreSplit() &&
				((this.getArgs() == null && alter.getArgs() == null) ||	(this.getArgs().size() == alter.getArgs().size())) &&
				((this.getRange() == null && alter.getRange() == null)  || (this.getRange().equals(alter.getRange()))) &&
				((this.getSplit() == null && alter.getSplit() == null) || (this.getSplit().equals(alter.getSplit()))) &&
				allArgsEqual(this, alter);
	}
	
	@Override
	public int hashCode() {
		return getSplit().hashCode() * getRange().hashCode() * hashArgs();
	}

	private final int hashArgs() {
		int acc=1;
		for (Atom arg: getArgs()) {acc = acc*arg.hashCode();}
		for (Atom arg: getMap().values()) {acc = acc*arg.hashCode();}
		for (String arg: getMap().keySet()) {acc = acc*arg.hashCode();}
 		return acc;
	}
	
	/**Compare the argument lists.*/
	private static final boolean allArgsEqual(Specializer one, Specializer two) {
		java.util.List<Atom> argsOne = one.getArgs();
		java.util.List<Atom> argsTwo = two.getArgs();
		if (argsOne == null && argsTwo == null) {return true;}
		
		if (argsOne == null || argsOne.size() != argsTwo.size()) {return false;}
		
		for (int i =0; i< argsOne.size(); i++) {
			if (!argsOne.get(i).equals(argsTwo.get(i))) {return false;}
		}
		return true;
	}
}
