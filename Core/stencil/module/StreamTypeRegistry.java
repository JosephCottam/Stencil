package stencil.module;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import stencil.interpreter.tree.Specializer;
import stencil.module.util.ann.Stream;
import stencil.parser.ParseStencil;
import stencil.tuple.stream.TupleStream;
import stencil.util.collections.PropertyUtils;

public class StreamTypeRegistry {
	public static final String STREAM_KEY = "streamType";
	
	private static final Map<String, Class> registry = new HashMap();
	private static final Map<String, Specializer> defSpec = new HashMap();
	
	public static void registerStreams(Properties props) {
		for (String key: PropertyUtils.filter(props, STREAM_KEY)) {
			String className = props.getProperty(key);
			Class streamClass;
			try {
				if (className.startsWith("file://")) {
					streamClass = ClassLoader.getSystemClassLoader().loadClass(className);
				} else {
					streamClass = Class.forName(className);
				}
			} catch (Exception e) {
				throw new RuntimeException(String.format("Error accessing stream class: " + className), e);
			}
			register(streamClass);
		}
	}
	
	public static void register(Class clazz) {
		if (!TupleStream.class.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException("Stream annotated classes must subtype TupleStream.");
		}
		
		Stream ann = (Stream) clazz.getAnnotation(Stream.class);
		if (ann == null) {throw new IllegalArgumentException("Only classes with a 'Stream' annotation may be registered.");}
		
		Specializer spec;
		try {spec = ParseStencil.specializer(ann.spec());}
		catch (Exception e) {throw new IllegalArgumentException("Invalid default specializer on stream type " + ann.name());}
				
		//validate information about the class
		try {
			clazz.getConstructor(Specializer.class);
		} catch (Exception e) {
			try {
				clazz.getConstructor(Specializer.class, Object[].class);
			} catch (Exception ex) {
				throw new IllegalArgumentException("Error in stream type " + ann.name() + ": Must provide an accessible constructor that takes either a single specializer or a specializer and an object array.", e);
			}
		}
		
		registry.put(ann.name(), clazz);
		defSpec.put(ann.name(), spec);
	}
	
	/**Construct a stream source of the given type with the provided specializer (and possibly other arguments).
	 * If a specializer-argument-only constructor exists, it will be used.
	 * Otherwise a specializer+Object[] constructor will be sought and used if found.
	 * It is up to the implementing class to sort and employ the passed arguments. 
	 */
	public static TupleStream instance(String type, Specializer spec, Object... args) {
		Class clazz = registry.get(type);
		if (clazz == null) {throw new IllegalArgumentException("Stream type unknown: " + type);}
				
		Constructor<TupleStream> c;
		try {
			c = clazz.getConstructor(Specializer.class);
			return c.newInstance(spec);
		} catch (Exception e) {
			try {
				c = clazz.getConstructor(Specializer.class, Object[].class);
				return c.newInstance(spec, args);
			}
			catch (Exception ex) {
				String msg = String.format("Error constructing stream of type %1$s: No compatible constructor found.", type);
				throw new RuntimeException(msg, e);
			}
		}
	}
	
	public static Specializer defaultSpecializer(String type) {
		Specializer spec = defSpec.get(type);
		if (spec == null) {throw new IllegalArgumentException("Stream type unknown: " + type);}
		return spec;
	}
	
}
