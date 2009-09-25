package stencil.operator.module.provided;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import stencil.operator.StencilOperator;
import stencil.operator.module.*;
import stencil.operator.module.util.*;
import stencil.operator.util.BasicProject;
import stencil.parser.string.ParseStencil;
import stencil.parser.tree.Atom;
import stencil.parser.tree.Specializer;
import stencil.parser.tree.StencilNumber;
import stencil.parser.tree.StencilString;
import stencil.parser.tree.Value;
import stencil.streams.Tuple;
import stencil.util.BasicTuple;
import stencil.util.Tuples;

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
			
			return new BasicTuple(keys, values);
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
					sum += ((Tuple) args[0]).getFields().size();
				} else {sum++;}
			}
			return BasicTuple.singleton(sum);
		}
		
		public static boolean accepts(Specializer specializer) {return specializer.isSimple();}
		public Length duplicate() {return this;}
	}
	

	/**Returns exactly what it was passed, but
	 * records elements seen (for categorization).
	 * 
	 * TODO: Merge with a generalized echo. Requires better 'split' semantics
	 */
	public static final class EchoCategorize implements StencilOperator {
		private String[] names;
		private List<Object[]> seen;
		private int priorCount;
		
		private EchoCategorize() {
			names = null;
			seen = new ArrayList<Object[]>();
			priorCount =0;
		}
		
		public EchoCategorize(String...names) {
			this();
			this.names = names;
		}
		
		public EchoCategorize(Specializer s) throws SpecializationException {
			this();
			if (s.getArgs().get(0) instanceof StencilNumber && s.getArgs().size() ==1) {
				names = Tuples.defaultNames(((StencilNumber) s.getArgs().get(0)).getValue().intValue(),null);
			} else if (s.getArgs().size() >0){
				names = new String[s.getArgs().size()];
				Atom atom = s.getArgs().get(0);
				for (int i=0; i< names.length; atom = s.getArgs().get(++i)) {
					if (atom instanceof StencilString) {
						names[i] = atom.getText();
					} else {
						throw new SpecializationException("TupleUtil", getName(), s);
					}
				}
			} else {
				throw new SpecializationException("TupleUtil", getName(), s);				
			}
		}
		
		public StencilOperator duplicate() throws UnsupportedOperationException {
			return new EchoCategorize();
		}

		public String getName() {return "Echo";}

		public synchronized List guide(List<Value> formalArguments, List<Object[]> sourceArguments, List<String> prototype) {
			assert sourceArguments == null && prototype == null : "Non-null sourceArgument or prototpye passed to categorize operator's Guide facet.";
			
			priorCount = seen.size();
			return seen;
		}

		public Tuple map(Object... args) {
			if (!deepContains(seen, args)) {seen.add(args);}
			return new BasicTuple(names, args);
		}
		
		private static final boolean deepContains(List<Object[]> list, Object[] candidate) {
			for (Object[] e: list) {if (Arrays.deepEquals(e, candidate)) {return true;}}
			return false;
		}

		public Tuple query(Object... args) {
			return new BasicTuple(names, args);
		}

		public synchronized boolean refreshGuide() {
			return seen.size() != priorCount;
		}
		
		/**Does this legend accept the given specializeR?*/
		protected static boolean accepts(Specializer specializer) {
			return specializer.getRange().isFullRange() && 
				   ((specializer.getArgs().get(0) instanceof StencilNumber && specializer.getArgs().size() ==1) ||
					(specializer.getArgs().get(0) instanceof StencilString));
		}
		
		/**Complete the legend data, given the specializer.*/
		protected static OperatorData complete(OperatorData base, Specializer spec) {
			MutableOperatorData ld = new MutableOperatorData(base);
			FacetData fd = ld.getFacetData("Map");
			fd = new BasicFacetData(fd.getName(), fd.getFacetType(), spec.getArgs());
			ld.addFacet(fd);
			
			fd = ld.getFacetData("Query");
			fd = new BasicFacetData(fd.getName(), fd.getFacetType(), spec.getArgs());
			ld.addFacet(fd);
			
			return ld;
			
		}
	}
	
	public TupleUtil(ModuleData md) {super(md);}

	
	protected void validate(String name, Specializer specializer) throws SpecializationException {
		if (!moduleData.getOperators().contains(name)) {throw new IllegalArgumentException("Name not known : " + name);}

		if (name.equals("Rename") && !Rename.accepts(specializer)) {
			throw new SpecializationException(moduleData.getName(), name, specializer);
		} else if (name.equals("Length") && !Length.accepts(specializer)) { 
			throw new SpecializationException(moduleData.getName(), name, specializer);			
		} else if (name.equals("EchoCategorize") && !EchoCategorize.accepts(specializer)) {
			throw new SpecializationException(moduleData.getName(), name, specializer);
		}
	}
	
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException {		
		validate(name, specializer);
		OperatorData ld = moduleData.getOperatorData(name);
		if (name.equals("EchoCategorize")) {ld = EchoCategorize.complete(ld,specializer);}

		if (ld.isComplete()) {return ld;}
		throw new MetaDataHoleException(moduleData.getName(), name, specializer, ld);
	}

	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		validate(name, specializer);
		
		//TODO: Extends modules to auto-detect a static 'instance' method or constructor to take a specializer use it instead of the zero-argument constructor 
		if (name.equals("Rename")) {return new Rename(specializer);}
		else if (name.equals("Length")) {return new Length();}
		else if (name.equals("EchoCategorize")) {return new EchoCategorize(specializer);}

		throw new RuntimeException(String.format("Legend name not known: %1$s.", name));
	}
}
