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

import stencil.display.CanvasTuple;
import stencil.parser.ParserConstants;
import stencil.streams.Tuple;

public class Canvas extends Target{
	public Canvas(Token source) {super(source);}

	/**Modify the global canvas, doing order-wise matching of fields from the source
	 * to fields specified in the prototype.  Returns the canvas tuple.
	 */
	public Tuple finalize(Tuple source) {
		source = simpleFinalize(source);
		stencil.util.Tuples.transfer(source, Global.getCanvas(), false);
		return Global.getCanvas();
	}


	public static abstract class Global {
		/**Global canvas object.  Only one is allowed per stencil right now.*/
		//TODO: Make it so we can have multiple canvases
		protected static CanvasTuple canvas;

		/**Set the global canvas object.*/
		public static void setCanvas(CanvasTuple canvas) {Global.canvas = canvas;}

		/**Get the global canvas object.*/
		public static CanvasTuple getCanvas() {return canvas;}


		/**Converts a name identifiable as a 'viewField' into a regular
		 * field name.
		 */
		public static final String regularField(String name) {
			return name.substring(ParserConstants.CANVAS_PREFIX.length()+1);
		}


		/**Does the passed name indicate a field in the view tuple?*/
		public static final boolean isCanvasField(String name) {
			return name != null && name.startsWith(ParserConstants.CANVAS_PREFIX);
		}
	}
}
