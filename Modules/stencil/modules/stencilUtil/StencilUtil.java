package stencil.modules.stencilUtil;


import java.util.*;

import stencil.module.MetadataHoleException;
import stencil.module.SpecializationException;
import stencil.module.operator.StencilOperator;
import stencil.module.operator.util.DirectOperator;
import stencil.module.operator.util.MethodInvokeFailedException;
import stencil.module.util.BasicModule;
import stencil.module.util.ModuleData;
import stencil.module.util.OperatorData;
import stencil.module.util.ModuleDataParser.MetadataParseException;
import stencil.module.util.ann.*;
import stencil.modules.stencilUtil.range.Range;
import stencil.modules.stencilUtil.split.Split;
import stencil.interpreter.guide.SampleOperator;
import stencil.interpreter.guide.SampleSeed;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.Tuple;
import stencil.tuple.Tuples;
import stencil.tuple.instances.ArrayTuple;
import stencil.types.Converter;
import stencil.util.streams.DeferredStream;
import stencil.util.streams.binary.BinaryTupleStream;
import stencil.util.streams.binary.SocketTupleStream;
import stencil.util.streams.numbers.RandomStream;
import stencil.util.streams.numbers.SequenceStream;
import stencil.util.streams.sql.QueryTuples;
import stencil.util.streams.twitter.TwitterTuples;
import stencil.util.streams.txt.DelimitedParser;
import stencil.util.streams.ui.MouseStream;

import static stencil.module.util.ModuleDataParser.operatorData;
import static stencil.module.util.ModuleDataParser.moduleData;


@Module
@Description("Operators used in various stencil transformations.")
@StreamTypes(classes = {BinaryTupleStream.Reader.class, SocketTupleStream.class, 
						SequenceStream.class, RandomStream.class, QueryTuples.class, 
						TwitterTuples.class, DelimitedParser.class, MouseStream.class,DeferredStream.class})
public final class StencilUtil extends BasicModule {
	@Operator()
	@Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
	@Description("Use the Converter to change the value(s) passed into a tuple.")
	public static final Tuple toTuple(Object... values) {return Converter.toTuple(values);} 
	
	
	@Operator()
	@Description("No-Op.  Always returns the empty tuple.")
	public static final class Nop extends DirectOperator implements SampleOperator {
		protected Nop(OperatorData od) {super(od);}

		@Override
		@Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
		public Object invoke(Object[] arguments) throws MethodInvokeFailedException {return Tuples.EMPTY_TUPLE;}

		@Override
		public List<Tuple> sample(SampleSeed seed, Specializer details) {return new ArrayList();}
	}
	
	@Operator()
	@Facet(memUse="FUNCTION", prototype="()", alias={"map","query"})
	@Description("Repackage the the values passed as a tuple (simple wrapping, no conversion attempted).")
	public static final Tuple valuesTuple(Object... values) {return new ArrayTuple(values);} 

	@Override
	protected ModuleData loadOperatorData() throws MetadataParseException {
		final String MODULE_NAME = this.getClass().getSimpleName();
		
		ModuleData md = moduleData(this.getClass());	//Loads the meta-data for operators defined in this class
		
		
		md.addOperator(operatorData(MonitorCategorical.class, MODULE_NAME));
		md.addOperator(operatorData(MonitorContinuous.class, MODULE_NAME));
		md.addOperator(operatorData(MonitorFlex.class, MODULE_NAME));
		md.addOperator(operatorData(MonitorSegments.class, MODULE_NAME));
		md.addOperator(operatorData(MonitorDate.class, MODULE_NAME));
		
		md.addOperator(operatorData(Map.class, MODULE_NAME));
		md.addOperator(operatorData(Range.class, MODULE_NAME));
		md.addOperator(operatorData(Split.class, MODULE_NAME));
		md.addOperator(operatorData(SpecialTuples.Canvas.class, MODULE_NAME));
		md.addOperator(operatorData(SpecialTuples.View.class, MODULE_NAME));
		
		return md;
	}
	
	
	protected void validate(String name, Specializer specializer) throws SpecializationException {
		if (!moduleData.getOperatorNames().contains(name)) {throw new IllegalArgumentException("Name not known : " + name);}
	}
	
	public OperatorData getOperatorData(String name, Specializer specializer) throws SpecializationException {		
		validate(name, specializer);
		OperatorData od = moduleData.getOperator(name);

		if (od.isComplete()) {return od;}
		throw new MetadataHoleException(od, specializer);
	}
	
	@Override
	public StencilOperator instance(String name, Specializer specializer) throws SpecializationException {
		OperatorData operatorData = getOperatorData(name, specializer);

		if (name.equals("ToTuple") || name.equals("ValuesTuple")) {
			return super.instance(name, specializer);
		} else if (name.equals("Nop")) {
			return new Nop(operatorData);
		} else if (name.equals("MonitorCategorical")) {
			return new MonitorCategorical(operatorData, specializer);
		} else if (name.equals("MonitorContinuous")) {
			return new MonitorContinuous(operatorData, specializer);			
		} else if (name.equals("MonitorFlex")) {
			return new MonitorFlex(operatorData, specializer);			
		} else if (name.equals("MonitorSegments")) {
			return new MonitorSegments(operatorData, specializer);			
		} else if (name.equals("MonitorDate")) {
			return new MonitorDate(operatorData);			
		} else if (name.equals(SpecialTuples.VIEW_TUPLE_OP)) {
			return new SpecialTuples.View(operatorData);
		} else if (name.equals(SpecialTuples.CANVAS_TUPLE_OP)) {
			return new SpecialTuples.Canvas(operatorData);
		} else if (name.equals("Map")) {
			return new Map(operatorData);
		} else if (name.equals("Range")) {
			return new Range(operatorData, specializer);	
		} else if (name.equals("Split")) {
			return new Split(operatorData, specializer);			
		} 
		
		throw new Error("Could not instantiate  operator " + name);
	}
}
