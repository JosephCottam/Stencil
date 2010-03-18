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


import org.antlr.runtime.Token; 

public class Specializer extends StencilTree {

	public Specializer(Token source) {super(source);}

	/**Is the split before or after the range?*/
	public boolean isPreSplit() {return false;} //TODO: implement pre/post split...

	/**What is the range argument?*/
	public Range getRange() {return (Range) getChild(0);}

	/**What is the split argument?*/
	public Split getSplit() {return (Split) getChild(1);}
	
	/**What additional arguments were passed to the specializer?*/
	public java.util.List<Atom> getArgs() {return (List<Atom>) getChild(2);}

	public java.util.Map<String, Atom> getMap() {
		return new MapEntry.MapList((List<MapEntry>) getChild(3));
	}
	
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
