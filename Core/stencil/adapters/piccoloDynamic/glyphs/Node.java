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
package stencil.adapters.piccoloDynamic.glyphs;

import java.util.Enumeration;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;


import stencil.adapters.piccoloDynamic.Adapter;
import stencil.adapters.piccoloDynamic.util.*;
import stencil.types.Converter;
import stencil.util.ConversionException;
import stencil.util.Tuples;
import stencil.streams.Tuple;
import stencil.streams.MutableTuple;
import stencil.parser.tree.Rule;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;


import static stencil.adapters.GlyphAttributes.StandardAttribute;
/**To allow proper functioning of the attributes mechanisms provided
 * in this class, sub-classes (and transitive sub-classes) should provide
 * a constructor that matches the single constructor given here.  This
 * constructor should be protected as it should only be used internally.
 * @author jcottam
 *
 */
public abstract class Node extends PNode {
	public static java.awt.Color BOUNDING_BOX_COLOR = null;

	/**Which attributes are provided for by this class?
	 * Sub classes are must load the attributes provided by the Node class
	 * into their own PROVIDED_ATTRIBUTES variable.  Similarly, attributes
	 * may load those provided by their direct super-class (if it is not in fact
	 * a direct subclass of Node).  If loading attributes from a class other
	 * than Node, the PROVIDED_ATTRIBUTES of Node need not be loaded separately,
	 * as they should be included in the PROVIDED_ATTRIBUTES (or equivalent field)
	 * of the class actually being loaded.
	 * */
	protected static final Attributes PROVIDED_ATTRIBUTES  = new Attributes();

	static {
		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.ID, "getID", "setID", Node.class, null, String.class));
		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.Z, "getZ", "setZ", Node.class, new Double(0)));
		PROVIDED_ATTRIBUTES.put(new Attribute(StandardAttribute.IMPLANTATION, "getImplantation", "setImplantation", Node.class, null, String.class));
	}

	//HACK: The whole dynamic rules implementation is a hack... so is this part.
	public static final class DynamicRule {
		public Tuple source;
		public Rule rule;
		protected Adapter adapter;
		
		public DynamicRule(Tuple source, Rule rule, Adapter adapter) {
			this.source = source;
			this.rule = rule;
			this.adapter = adapter;
		}
	}


	protected String id;
	protected Attributes attributes;
	protected String implantation;


	
	//HACK: The whole dynamic rules implementation is a hack... so is this part.
	public Set<DynamicRule> dynamicRules = new HashSet<DynamicRule>();
	//Part of the dynamic rules hack...
	private MutableTuple self = new stencil.adapters.piccoloDynamic.NodeTuple(this);
	
	/**Piccolo has no in-layer ordering mechanism, so we provide a way to signal it here.*/
	protected double z =0;

	/**
	 * @param id Identifier of this node
	 * @param attributes Attributes of the actual instance.
	 */
	protected Node(String id, String implantation, Attributes attributes) {
		setID(id);
		this.attributes = attributes;
		this.implantation = implantation;
	}

	/**Iterates all attributes, setting their value to the default specified
	 * in the attribute set.  Since object storage is not allocated until
	 * after the super-constructor is invoked, this method cannot be called
	 * automatically in Node's constructor.  However, it should be called
	 * before object initialization is complete for any given node.
	 *
	 * Elements with 'null' as the default value are skipped.
	 */
	protected void applyDefaults() {
		for (Attribute att: attributes.values()) {
			if (att.defaultValue == null) {continue;}  //Skip nulls, they must take up their own defaults
			setAttribute(att.name, att.defaultValue);
		}
	}

	public String getID() {return id;}
	public void setID(String id) {this.id = id;}

	public double getZ() {return z;}
	public void setZ(Double z) {this.z = z;}

	public String getImplantation() {return implantation;}
	public void setImplantation(String implantation) {this.implantation = implantation;}


	/**This method is provided for testing and debugging.  Its
	 * semanitics are not public (sorry).
	 *
	 * @return
	 */
	public Attributes getProvidedAttributes() {return attributes;}

	/**Return a list of all valid properties of this node.
	 * This list needs to include both the 'natural' properties
	 * of the node type, and any additional properties specified on
	 * the given node.
	 * @return
	 */
	public Set<String> getAttributes() {
		Set<String> names = new java.util.HashSet<String>();
		names.addAll(attributes.keySet());
		Enumeration e = super.getClientPropertyKeysEnumeration();
		while (e.hasMoreElements()) {names.add(e.nextElement().toString());}

		return names;
	}

	/**@param legendName Attribute to get value of.
	 * @return Value of named property
	 */
	public Object getAttribute(String fullName) {
		String baseName = baseName(fullName);
		
		if (attributes.containsKey(baseName)) {
			Attribute a = attributes.get(baseName);
			return a.get.invoke(this, fullName, (Object[]) null);
		}
		
		return super.getAttribute(fullName);
	}

	/**Sets the specified attribute to the specified value.
	 * Will convert the passed value to the right type (if possible).
	 *
	 * @param legendName Attribute to set
	 * @param value Value to set attribute to
	 * @throws ConversionException Conversion between the type of the attribute and the passed object is not known.
	 */
	public synchronized void setAttribute(String fullName, Object value) {
		try {
			String baseName = baseName(fullName);
			if (attributes.containsKey(baseName)) {
				Attribute a = attributes.get(baseName);
				value = Converter.convert(value, a.type);
				a.set.invoke(this, fullName, value);
			} else {
				super.addAttribute(fullName, value);
			}
		} catch (Exception e) {throw new RuntimeException(String.format("Exception trying to set %1$s to %2$s.", fullName, value), e);}
	}

	/**What is the default value for the given attribute?
	 * Defaults are set on a per-attribute basis.  As such,
	 * the default is shared between all values that share
	 * a base-name.  The name passed may be either the base or the full name.
	 *
	 * If there is no default for the attribute, null will be returned.  This includes
	 * ad-hoc attributes and any attributes added by the interpreter not explicitly
	 * mentioned in the Stencil or the node definition.
	 *
	 * @param name
	 * @return
	 */
	public Object getDefault(String name) throws IllegalArgumentException{
		String baseName = baseName(name);
		Attribute a = attributes.get(baseName);
		if (a == null) {return null;}
		return a.defaultValue;
	}

	/**What is the expected type of the given attribute.
	 * Types are set on a per-attribute basis.  As such,
	 * the type is shared between all values that share
	 * a base-name.  The name passed may be either the base or the full name.
	 *
	 * @param name
	 * @return
	 */
	public Class getType(String name) {
		String baseName = baseName(name);
		Attribute a = attributes.get(baseName);
		return a.type;
	}

	/**Convert a full property name to just a base property name.
	 * Base properties are the first component of a dotted name,
	 * but to distinguish properties that take arguments from
	 * properties that do not, base names for properties that
	 * take arguments have an 'n' appended to the end.
	 *
	 *  For example:
	 *  	FullName	BaseName	Notes
	 *  	   Y		   Y		  Simple Y that cannot take arguments
	 *  	   Y.1		   Yn		  Y that takes an argument
	 **/
	private static final Pattern NAME_SPLITTER = Pattern.compile("\\."); 
	public static String baseName(String fullName) {
		String baseName = NAME_SPLITTER.split(fullName)[0];
		if (baseName.length() == fullName.length()) {return fullName;} //Return quick if nothing changed
		baseName = baseName.concat("n");
		return baseName;
	}

	/**What are the implicit arguments in the full name passed?
	 * This is the converse of the baseName operation.  Anything
	 * that is not part of the base name is part of the implicit arguments.
	 *
	 */
	public String nameArgs(String fullName) {
		String baseName = baseName(fullName);
		if (baseName.equals(fullName)) {return null;}

		return fullName.substring(baseName(fullName).length());
	}

	/**Set all defined values to defaults (if they were specified).
	 * Will remove all ad-hoc properties as well.
	 *
	 */
	public void reset() {
		this.getClientProperties().removeAttributes(this.getClientProperties());
		for (Attribute a: attributes.values()) {
			if (a.defaultValue != null) {
				a.set.invoke(this, a.name, a.defaultValue);
			}
		}
	}


	protected void paint(PPaintContext paintContext) {
		prePaint();
		super.paint(paintContext);
		if (BOUNDING_BOX_COLOR != null) {
			paintContext.getGraphics().setStroke(new java.awt.BasicStroke(.1f));
			paintContext.getGraphics().setPaint(BOUNDING_BOX_COLOR);
			paintContext.getGraphics().draw(this.getBoundsReference().getBounds2D());
		}
	}

	/**Invoke all dynamic rules.
	 * Exceptions are ignored.
	 *
	 * TODO: THIS WHOLE DYNAMIC THING IS REALLY A BIG HACK....
	 * I NEED TO IMPLEMENT PARALLEL PROXY/REFIY TO GET THIS TO WORK PROPERLY WITH RANGE/SPLIT
	 * IN AN MEANINGFUL SENSE OF THE WORD
	 *
	 * @param paintContext
	 */
	public void prePaint() {
		for (DynamicRule r: dynamicRules) {
			Tuple t;
			try {
				t = r.rule.apply(r.source);
				if (!Tuples.transferNeutral(t, self)) {
					r.adapter.setTransfers();	//Inform adapter that a transfer is being made (e.g. a rule ran with results that cause a change)
					Tuples.transfer(t, self, false);
				}
			} catch (Exception e) {System.err.print("Error in dynamic legend (ignored)."); e.printStackTrace();} //Ignored...
		}

	}

	public void moveInBackOf(PNode alter) {throw new UnsupportedOperationException("ZPNode should have ordering manipulated with the setZ method.");}
	public void moveInFrontOf(PNode alter) {throw new UnsupportedOperationException("ZPNode should have ordering manipulated with the setZ method.");}
	public void moveToBack() {throw new UnsupportedOperationException("ZPNode should have ordering manipulated with the setZ method.");}
	public void moveToFront() {throw new UnsupportedOperationException("ZPNode should have ordering manipulated with the setZ method.");}
}
