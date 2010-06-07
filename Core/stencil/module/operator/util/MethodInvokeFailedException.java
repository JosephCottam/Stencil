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
package stencil.module.operator.util;

import java.lang.reflect.Method;

public class MethodInvokeFailedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MethodInvokeFailedException() {super();}
	public MethodInvokeFailedException(String message, Throwable arg1) {super(message, arg1);}
	public MethodInvokeFailedException(String message, Method m, Object t, Throwable arg1) {super(formatMessage(message, m, t), arg1);}
	public MethodInvokeFailedException(String message, Method m, Object t) {super(formatMessage(message, m, t));}
	public MethodInvokeFailedException(String message) {super(message);}
	public MethodInvokeFailedException(Throwable message) {super(message);}
	
	private static String formatMessage(String message, Method method, Object target) {
		String targetName;
		if (target == null) {
			targetName = method.getDeclaringClass().getSimpleName();
		} else {
			targetName = target.getClass().getSimpleName();
		}
		return String.format("%1$s.%2$s: %3$s", targetName, method.getName(), message);
	}


}
