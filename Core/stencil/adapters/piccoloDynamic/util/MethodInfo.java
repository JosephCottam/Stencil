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

import java.lang.reflect.Method;

/**Wrapper for method invocation.  Groups together the class, method name
 * and some information on the invocation parameters.  This is used
 * in the reflection mechanisms for tuple to glyph mapping.
 *
 * @author jcottam
 *
 */
public class MethodInfo {
	public static class MethodError extends Error {
		public MethodError(String m, Exception e) {super(m,e);}

		public static MethodError find(String name, Class target, Exception e) {
			String message = String.format("Could not find %1$s in %2$s", name, target.getName());
			return new MethodError(message, e);
		}

		public static MethodError Invoke(String name, Object[] args, Exception e) {
//			String[] argTypes = new String[args.length];
//			for (int i=0; i< args.length; i++) {argTypes[i] = args.getClass().getName();}
			String message = String.format("Error invoking %1$s with arguments %2$s", name, java.util.Arrays.deepToString(args));
			return new MethodError(message, e);
		}
	}
	/**What is the name of the method being invoked*/
	public String methodName;

	/**Should the attribute name be passed as an argument?
	 * Some attributes use the name as an implicit parameter (e.g. X1, Y4, etc)
	 * so the method may need the attribute name used to properly
	 * process the request.  This may be distinct from the baseName used
	 * to find a attributes in the attributes set.
	 * */
	public boolean passName;

	protected Method method;

	/**Create a method info.
	 *
	 * @param name Method name on the class
	 * @param passAttributeName Should the attribute name be passed during invocation?
	 * @param target What class is this operating on
	 * @throws MethodError Thrown if no method of the given name is found on the class.
	 */
	public MethodInfo(String name, boolean passName, Class target) throws MethodError {
		this.methodName = name;
		this.passName = passName;
		findMethod(target);
	}

	/**Invoke the represented method on the given target object.
	 * If passName is true, the 'name' parameter will be pre-pended to the args.  Otherwise it will be ignored.
	 * @param target Object to invoke on
	 * @param name Attribute name used (this is the full name, not the base name).
	 * @param args Additional arguments, if any.
	 * @return The result of invoking the method with arguments given
	 */
	public Object invoke(Object target, String name, Object... args) {
		try {
			if (passName && args != null) {
				Object[] args2 = new Object[args.length +1];
				args2[0] = name;
				System.arraycopy(args, 0, args2, 1, args.length);
				return method.invoke(target, args2);
			}
			else if (passName) {return method.invoke(target, name);}
			else {return method.invoke(target, args);}
		}catch (Exception e) {throw MethodError.Invoke(name, args, e);}
	}

	/**Locate a method of a given name on the given class.  Just does name
	 * matching, not signature matching.
	 */
	private void findMethod(Class target) {
		for (Method m: target.getMethods()) {
			if (m.getName().equals(methodName)) {method = m; break;}
		}

		if (this.method == null) {throw MethodError.find(methodName, target, null);}
	}
}
