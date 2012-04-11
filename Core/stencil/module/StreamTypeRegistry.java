package stencil.module;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import stencil.interpreter.tree.Specializer;
import stencil.interpreter.tree.StreamDec;
import stencil.module.util.ann.Stream;
import stencil.parser.ParseStencil;
import stencil.parser.ParserConstants;
import stencil.tuple.prototype.TuplePrototype;
import stencil.tuple.stream.TupleStream;
import stencil.types.Converter;
import stencil.util.streams.DelayStream;
import stencil.util.streams.QueuedStream;
import static stencil.parser.ParserConstants.SYSTEM_STREAM_TYPE;

public class StreamTypeRegistry {
	public static final String STREAM_KEY = "streamType";
	public static final String QUEUE_KEY = "queue";
	public static final String QUEUE_THREAD_KEY = "queueThread";
	public static final String DELAY_KEY = "delay";
	
	
	private static final Map<String, Class> registry = new HashMap();
	private static final Map<String, Specializer> defSpec = new HashMap();
	
	/**Remove all registered stream types.**/
	public static void reset() {
		registry.clear();
		defSpec.clear();
	}
	
	/**@param clazz Register this class
	 * @param key If the class was loaded through the properties system, what was its entry key? (null otherwise)
	 */
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
			clazz.getConstructor(String.class, TuplePrototype.class, Specializer.class);
		} catch (Exception e) {
			try {
				clazz.getConstructor(String.class, TuplePrototype.class, Specializer.class, Object[].class);
			} catch (Exception ex) {
				throw new IllegalArgumentException("Error in stream type " + ann.name() + ": Must provide an accessible constructor that takes stream name, prototype and specializer (optionally: additional Object[]).", e);
			}
		}
		
		registry.put(ann.name(), clazz);
		defSpec.put(ann.name(), spec);
	}
	
	/**Construct a stream source of the given type with the provided specializer (and possibly other arguments).
	 * If a specializer-argument-only constructor exists, it will be used.
	 * Otherwise a specializer+Object[] constructor will be sought and used if found.
	 * It is up to the implementing class to sort and employ the passed arguments. 
	 * 
	 * The instantiation process performed here will also take care of queuing and delaying, if indicated by the specializer,
	 * so the specific stream types do not need to.
	 * 
	 */
	public static TupleStream instance(StreamDec streamDec, Object... args) {
		String type = streamDec.type();
		String name = streamDec.name();
		Specializer spec = streamDec.specializer();
		TuplePrototype proto = streamDec.prototype();
		
		if (type.equals(SYSTEM_STREAM_TYPE)) {return null;}
		
		
		Class clazz = registry.get(type);
		if (clazz == null) {throw new IllegalArgumentException("Stream type unknown: " + streamDec.type());}
		
		Constructor<TupleStream> c;
		TupleStream s;
		boolean additionalArgs = false;
		try {c = clazz.getConstructor(String.class, TuplePrototype.class, Specializer.class);
		} catch (Exception e) {
			try {
				c = clazz.getConstructor(String.class, TuplePrototype.class, Specializer.class, Object[].class);
				additionalArgs = true;
			}
			catch (Exception ex) {
				String msg = String.format("Error constructing stream of type %1$s: No compatible constructor found.", name);
				throw new RuntimeException(msg, e);
			}
		}
		
		try {
			if (!additionalArgs) {s = c.newInstance(name, proto, spec);}
			else {s = c.newInstance(name, proto, spec, args);}		
		} catch (Exception e) {
			String msg = String.format("Error invoking constructor on stream %1$s.", name);
			throw new RuntimeException(msg, e);
		}

		
		int queueSize = Converter.toInteger(spec.get(QUEUE_KEY, -1));
		int delayLen = Converter.toInteger(spec.get(DELAY_KEY, -1));
		boolean queueThread = Converter.toBoolean(spec.get(QUEUE_THREAD_KEY, true));

		if (queueSize > 0) {s = new QueuedStream(s,queueSize, queueThread);}
		if (delayLen > 0) {s = new DelayStream(s,delayLen);}
		return s;
		
	}
	
	public static Specializer defaultSpecializer(String type) {
		if (type.startsWith("#")) {return ParserConstants.EMPTY_SPECIALIZER;}
		
		Specializer spec = defSpec.get(type);
		if (spec == null) {
			throw new IllegalArgumentException("Stream type unknown: " + type);
		}
		return spec;
	}
	
}
