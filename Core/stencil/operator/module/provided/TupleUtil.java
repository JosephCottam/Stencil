package stencil.operator.module.provided;



import stencil.operator.StencilOperator;
import stencil.operator.module.*;
import stencil.operator.module.util.*;
import stencil.operator.util.BasicProject;
import stencil.parser.string.ParseStencil;
import stencil.parser.tree.Specializer;
import stencil.tuple.PrototypedTuple;
import stencil.tuple.Tuple;


public class TupleUtil extends BasicModule {
	
	/**Rename the components of a tuple with new names, 
	 * like an echo but with variable name.
	 * 
	 * TODO: Merge echo and categorize and Rename
	 */
	public static final class Rename extends BasicProject {
		public static final String NAME = "Rename";
		public static final Specializer DEFAULT_SPECAILIZER;
		static {
			try {DEFAULT_SPECAILIZER = ParseStencil.parseSpecializer("[VALUE]");}
			catch (Exception e) {throw new Error("Error creating default specializer for Rename.");}
		}
		
		String[] keys;
		
		public Rename(Specializer specializer) {
			keys = new String[specializer.getArgs().size()];
			for (int i=0; i< keys.length; i++) {
				keys[i] = specializer.getArgs().get(i).toString();
			}
		}		
		public Rename(String...keys) {this.keys = keys;}
		public Tuple query(Object... args) {return map(args);}
		public Tuple map(Object... values) {
			assert keys.length == values.length : "Keys and values lengths do not match.";
			
			return new PrototypedTuple(keys, values);
		}
		public String getName() {return NAME;}

		public static boolean accepts(Specializer specializer) {
			return specializer.getArgs().size() > 0 &&
				specializer.getRange().isSimple() &&
				!specializer.getSplit().hasSplitField();
		}
		
		public Rename duplicate() {return new Rename(keys);}
	}

	/**How many elements does the given tuple have?
	 * This does a single-level descent into all tuples passed
	 * as arguments. If no tuple is passed, the result is 
	 * equal to the number of arguments.
	 * 
	 * TODO: This may really be a categorize...I'm not sure!
	 * */
	public static final class Length extends BasicProject {
		public String getName() {return this.getClass().getSimpleName();}
		public Tuple query(Object... args) {return map(args);}		

		public Tuple map(Object... args) {
			int sum =0;
			for (Object o: args) {
				if (o instanceof Tuple) {
					sum += ((Tuple) args[0]).getPrototype().size();
				} else {sum++;}
			}
			return PrototypedTuple.singleton(sum);
		}
		
		public static boolean accepts(Specializer specializer) {return specializer.isSimple();}
		public Length duplicate() {return this;}
	}
	
	public TupleUtil(ModuleData md) {super(md);}
	
	protected void validate(String name, Specializer specializer) throws SpecializationException {
		if (!moduleData.getOperators().contains(name)) {throw new IllegalArgumentException("Name not known : " + name);}

		if (name.equals("Rename") && !Rename.accepts(specializer)) {
			throw new SpecializationException(moduleData.getName(), name, specializer);
		} else if (name.equals("Length") && !Length.accepts(specializer)) { 
			throw new SpecializationException(moduleData.getName(), name, specializer);			
		} 
	}
	
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException {		
		validate(name, specializer);
		OperatorData ld = moduleData.getOperatorData(name);

		if (ld.isComplete()) {return ld;}
		throw new MetaDataHoleException(moduleData.getName(), name, specializer, ld);
	}

	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		validate(name, specializer);
		
		//TODO: Extends modules to auto-detect a static 'instance' method or constructor to take a specializer use it instead of the zero-argument constructor 
		if (name.equals("Rename")) {return new Rename(specializer);}
		else if (name.equals("Length")) {return new Length();}

		throw new RuntimeException(String.format("Legend name not known: %1$s.", name));
	}
}
