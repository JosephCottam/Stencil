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
package stencil.adapters.piccoloDynamic.util;

import stencil.adapters.piccoloDynamic.glyphs.*;
import stencil.adapters.piccoloDynamic.guides.Axis;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import static stencil.adapters.GlyphAttributes.StandardAttribute;


public class ZPLayer extends PLayer {
	private static final long serialVersionUID = 1L;

	private Axis xAxis;
	private Axis yAxis;
		
	public void addAxis(Axis axis) {
		if (axis == null) {return;}
		if (axis.getAxis() == Axis.AXIS.X && xAxis != axis) {
			this.removeChild(xAxis);
			xAxis = axis;
		} else if (yAxis != axis) {
			this.removeChild(yAxis);
			yAxis = axis;
			
		}
		this.addChild(0, axis);
	}
	
	public void addChild(int idx, PNode child) {
		throw new UnsupportedOperationException("Ordering is handled by z-value in ZPLayer.  Specifying insertion index is not allowed.");
	}

	public void addChild(PNode child) {
		if (!(child instanceof Node)) {throw new IllegalArgumentException("ZLayer can only accept nodes of type ZPNode. Tried to add item of type " + child.getClass());}
		Node zChild = (Node) child;
		java.util.List children = this.getChildrenReference();
		if (children.size() == 0) {
			super.addChild(0, child);
			return;
		} else if (zChild.getZ() == (Double) StandardAttribute.Z.getDefaultValue()) {
			super.addChild(children.size()-1, child);
			return;
		}

		int idx;
		for (idx=0; idx<children.size(); idx++) {
			Node sibling = (Node) children.get(idx);
			if (sibling.getZ() >= zChild.getZ()) {
				break;
			}
		}

		super.addChild(idx, child);
	}

}
