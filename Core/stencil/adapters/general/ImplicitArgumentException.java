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
package stencil.adapters.general;

public class ImplicitArgumentException extends RuntimeException {
	public ImplicitArgumentException(Class source, String att, Object recieved) {
		this (source, att, recieved, null);
	}

	/**
	 * @param source  Which class is throwing this exception
	 * @param att What was the attribute?  (Full or base name may be used)
	 * @param recieved What was the found implicit argument
	 * @param e Exception encountered
	 */
	public ImplicitArgumentException(Class source, String att, Object recieved, Exception e) {
		super(String.format("Implantation %1$s, attribute %2$s recived illegal implicit argument (recieved %3$s).", source.getName(), att, recieved), e);
	}

	public ImplicitArgumentException(Class source, String att) {this(source, att, null);}

	/**
	 * @param source Class throwing the exception
	 * @param att Full name of the attribute
	 * @param e Exception encountered
	 */
	public ImplicitArgumentException(Class source, String att, Exception e) {
		super(String.format("Implantation $1%s, attribute %2$s recived illegal implicit argument.", source.getName(), att), e);
	}

	public ImplicitArgumentException(Class source, String att, String message, Exception e) {
		super(String.format("Implantation %2$s, attribute %2$s reports: %3$s.", source.getName(), att, message), e);
	}


}
