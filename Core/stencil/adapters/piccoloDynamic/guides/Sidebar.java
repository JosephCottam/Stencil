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
package stencil.adapters.piccoloDynamic.guides;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

import static stencil.adapters.GlyphAttributes.StandardAttribute;
import stencil.adapters.piccoloDynamic.NodeTuple;
import stencil.adapters.piccoloDynamic.glyphs.Shape;
import stencil.adapters.piccoloDynamic.glyphs.CommonNode;
import stencil.adapters.piccoloDynamic.glyphs.Text;
import stencil.display.DisplayGuide;
import stencil.util.AutoguidePair;
import stencil.util.Tuples;
import stencil.parser.string.ParseStencil;
import stencil.parser.tree.Specializer;
import static stencil.parser.ParserConstants.SIMPLE_DEFAULT;


public class Sidebar extends CommonNode implements DisplayGuide{	
	private class Pair {
		String label;
		Object value;
		
		public Pair(String label, Object value) {
			this.label = label;
			this.value = value;
		}
		public String toString() {return "(" + label + "," + value + ")";}
	}
	
	public static final String IMPLANTATION_NAME = "SIDE_BAR";
	public static final String LABEL_PROPERTY_TAG = "label";
	public static final String EXAMPLE_PROPERTY_TAG = "example";
	
	private static final String defaultArguments = "[label.FONT_SIZE=1, label.FONT_COLOR=@color(BLACK), example.SIZE=.8, spacing=.25, displayOn=\"" + SIMPLE_DEFAULT + "\"]";
	public static final Specializer DEFAULT_ARGUMENTS;
	static {
		try {DEFAULT_ARGUMENTS = ParseStencil.parseSpecializer(defaultArguments);}
		catch (Exception e) {throw new Error("Error parsing default axis arguments.", e);}
	}

	
	private NodeTuple<Text> prototypeLabel = new NodeTuple<Text>(new Text("prototype"));;
	private NodeTuple<Shape> prototypeExample = new NodeTuple<Shape>(new Shape("prototype"));;
	private NodeTuple<Sidebar> self;
	private float exampleWidth;
	private float exampleHeight;

	private float vSpacing;
	private float hSpacing;
	
	private boolean autoPlace = true;
	
	//Public because of how the applyDefaualts system works
	public float spacing = .25f;

	//Public because of how the applyDefaualts system works
	public String displayOn;
	
	public Sidebar(String id, Specializer specializer, int idx) {
		super(id, IMPLANTATION_NAME, CommonNode.PROVIDED_ATTRIBUTES);
		self = new NodeTuple(this);
		
		GuideUtils.setValues(DEFAULT_ARGUMENTS, this);
		GuideUtils.setValues(specializer, this);
		
		GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, LABEL_PROPERTY_TAG, prototypeLabel);
		GuideUtils.applyDefaults(specializer, LABEL_PROPERTY_TAG, prototypeLabel);

		GuideUtils.applyDefaults(DEFAULT_ARGUMENTS, EXAMPLE_PROPERTY_TAG, prototypeExample);
		GuideUtils.applyDefaults(specializer, EXAMPLE_PROPERTY_TAG, prototypeExample);
		
		
		exampleHeight = Math.max((Float) prototypeLabel.get(StandardAttribute.HEIGHT.name(), Float.class), (Float)  prototypeExample.get("SIZE", Float.class));
		exampleWidth = (Float) prototypeExample.get("SIZE", Float.class);
		vSpacing = spacing * exampleHeight;
		hSpacing = spacing * exampleWidth;
		
		
		if (specializer.getMap().containsKey(StandardAttribute.X.name()) || specializer.getMap().containsKey(StandardAttribute.Y.name())) {autoPlace = false;}
		if (SIMPLE_DEFAULT.equals(displayOn)) {displayOn = id;}
	}

	public void setElements(List<AutoguidePair> elements) {
		List<Pair> listing = validate(elements);
		Collection<PNode> marks = createLabeledBoxes(listing);
		this.removeAllChildren();
		this.addChildren(marks);
		this.invalidateFullBounds();
		
		if (autoPlace) {
			PBounds bounds = this.getFullBoundsReference();
			self.set("Y", -bounds.getHeight());
			self.set("X", -bounds.getWidth());
		}
	}
	
	private Collection<PNode> createLabeledBoxes(List<Pair> elements) {
		Collection<PNode> marks = new ArrayList<PNode>(elements.size() *2);
		for (int i=0; i< elements.size(); i++) {
			marks.addAll(createLabeledBox(elements.get(i), i));
		}
		return marks;
	}
	
	private Collection<PNode> createLabeledBox(Pair contents, int idx) {
		NodeTuple<Text> newLabel = new NodeTuple(new Text(contents.label));
		Tuples.transfer(prototypeLabel, newLabel, false);
		
		NodeTuple<Shape> newExample = new NodeTuple(new Shape(contents.label));
		Tuples.transfer(prototypeExample, newExample, false);
		
		float indexOffset = (idx *exampleHeight) + (idx * vSpacing);  
		
		newLabel.set("Y", indexOffset);
		newLabel.set("X", exampleWidth + hSpacing);
		newLabel.set("TEXT", contents.label);
		
		newExample.set("Y", indexOffset);
		newExample.set(displayOn, contents.value);
		
		return Arrays.asList(new PNode[]{newLabel.getNode(), newExample.getNode()});
	}

	
	/**Verify that all Autoguide pairs have exactly one result, and that result
	 * is some type of Number.
	 * 
	 * @param elements
	 * @return
	 */
	private List<Pair> validate(Collection<AutoguidePair> elements) {
		List<Pair> pairs = new ArrayList<Pair>(elements.size());
		Comparator c = new Comparator<Pair>() {
			public int compare(Pair p1, Pair p2) { 
				String l1 = p1.label;
				String l2 = p2.label;
				
				return l1.compareTo(l2);				
			}
			
		};
				
		for (AutoguidePair p: elements) {
			//TODO: Validate that the result is of the right type for the attribute it is to be applied to...
			Pair pair = new Pair(p.getInput()[0].toString(), p.getResult()[0]);
			pairs.add(pair);
		}
		
		Collections.sort(pairs, c);
		return pairs;
	}
}
