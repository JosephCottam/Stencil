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

//Marked final for immutability preservation
public final class Attribute {

	/**Name of the attribute being represented.*/
	public final String name;

	/**Method used to get an attribute.  Must be supplied*/
	public final MethodInfo get;

	/**Method used to set attribute.  May be null for read-only attributes.*/
	public final MethodInfo set;

	/**What is the default value for this attribute.  Null indicates no default value.*/
	public final Object defaultValue;

	/**What is the type of this attribute.
	 * Must match the type of default value.
	 * Since default value may be null or it may be a sub-class, this separate storage is not redundant.*/
	public final Class type;


	/**
	 * @param name Attribute name (corresponds to the baseName of the actual attribute invocation)
	 * @param get Method name to be used to get
	 * @param set Method name to be used for set
	 * @param target Class to being target by get/set
	 * @param defaultValue Default value for this attribute
	 * @param type Type of the default attribute (if left null, will be derived from defaultValue, but must be supplied if there is no defaultValue)
	 */
	public Attribute(String name, String get, String set,  Class target, Object defaultValue, Class type) {
		this(name, get, set, target, false, defaultValue, type);
	}
	public Attribute(Enum name, String get, String set,  Class target, Object defaultValue, Class type) {
		this(name.name(), get, set, target, false, defaultValue, type);
	}

	/**Convenience constructor for providing a defaultValue and having the class type derived*/
	public Attribute(String name, String get, String set,  Class target, Object defaultValue) {
		this(name, get, set, target, false, defaultValue, defaultValue.getClass());
	}
	public Attribute(Enum name, String get, String set,  Class target, Object defaultValue) {
		this(name.name(), get, set, target, false, defaultValue, defaultValue.getClass());
	}

	/**
	 * @param name
	 * @param get
	 * @param set
	 * @param target
	 * @param passName When constructing MethodInfo's, use this value to indicate if the fullName needs to be passed to the method
	 * @param defaultValue
	 * @param type
	 */
	public Attribute(String name, String get, String set,  Class target, boolean passName, Object defaultValue, Class type) {
		this(name, new MethodInfo(get, passName,target), new MethodInfo(set, passName, target), defaultValue, type);

	}
	public Attribute(Enum name, String get, String set,  Class target, boolean passName, Object defaultValue, Class type) {
		this(name.name(), new MethodInfo(get, passName,target), new MethodInfo(set, passName, target), defaultValue, type);

	}

	public Attribute(Enum name, MethodInfo get, MethodInfo set, Object defaultValue, Class type) {
		this(name.name(), get, set, defaultValue, type);
	}
	public Attribute(String name, MethodInfo get, MethodInfo set, Object defaultValue, Class type) {
		assert (type != null) : "Type cannot be null";
		assert (defaultValue == null || type.isAssignableFrom(defaultValue.getClass())) : "Type of default value does not match the type parameter";

		assert (!get.methodName.startsWith("set")) : "Get-method starts with 'set'";
		assert (!set.methodName.startsWith("get")) : "Set-method starts with 'set'";

		this.name = name;
		this.get = get;
		this.set = set;
		this.defaultValue = defaultValue;
		this.type = type;
	}

	/**Return an attribute that is the same as the current one, except the default value
	 * is set to the new value.  If the new value equals the old value, the current
	 * attribute may be returned instead.
	 * 
	 * @param Value to use for the default instead of the current default.*/
	public Attribute changeDefault(Object newDefault) {
		if ((newDefault == null && defaultValue == null) || (newDefault != null && newDefault.equals(defaultValue))) {return this;}			
		return new Attribute(name, get, set, newDefault, type);
	}
}
