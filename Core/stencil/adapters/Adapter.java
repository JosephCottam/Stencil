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
 */package stencil.adapters;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import stencil.display.StencilPanel;
import stencil.parser.tree.Program;
import stencil.parser.tree.Layer;
import stencil.display.DisplayLayer;

/**The generator takes a stencil program and generators an
 * executable entity.  It returns an operating panel that
 * can be joined to a UI and will display the results.
 * Furthermore, it also collects the necessary support
 * objects for the particular adapter package.
 *
 * In addition to the methods listed here, to work with the Explore
 * application's adapter switching a public static variable
 * named INSTANCE must also be included.
 *
 * @author jcottam
 *
 */
public interface Adapter<T extends Glyph> {
	/**Graphics object that may be used by adapters for planning purposes.*/
	public static final BufferedImage REFERENCE_CANVAS = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	public static final Graphics2D REFERENCE_GRAPHICS = (Graphics2D) REFERENCE_CANVAS.getGraphics();

	//------------------------------------------------------------------------------------------
	//Compile-time support operations.  These methods are used by the host application
	//and compiler to create the visualization before any data is added.

	/**Create a StencilPanel that corresponds to this adapter.  
	 * The panel should be compatible with the layers, guides, etc. produced 
	 * by this adapter.*/
	public StencilPanel generate(Program program);

	/**Create a layer in this adapter.*/
	public DisplayLayer makeLayer(Layer l);
		
	
	/**Given a guide type, return the corresponding class.*/
	public Class getGuideClass(String name);

	
	//TODO: Re-think adapter-based finalize.. why not just invoke this on the panel?  Is there a time that the adapter knows something the panel does not?
	public void finalize(StencilPanel panel);
	
	/**Given a stencil program as a string, return a new panel ready to go...*/
	public StencilPanel compile(String programSource)  throws Exception;
	
	//------------------------------------------------------------------------------------------
	//Optional operations to influence the behavior of the adapter.
	//The host application may try to use these, but should not fail if they 
	//throw exceptions
	
	/**Should a debugging color be used?  If so, it is set here.*/
	public void setDebugColor(java.awt.Color c) throws UnsupportedOperationException;

	/**Should a default set of mouse responses be used?*/
	public void setDefaultMouse(boolean m) throws UnsupportedOperationException;
	
	/**Indicate the rendering quality. The value passed may be anything,
	 * but all adapters should be prepared to accept LOW and HIGH.
	 * 
	 * @throws IllegalArgumentException The value passed is not understood.
	 * @throws UnsupportedOperationException The render quality cannot be set on this adapter.
	 */
	public void setRenderQuality(String value) throws IllegalArgumentException, UnsupportedOperationException;

}