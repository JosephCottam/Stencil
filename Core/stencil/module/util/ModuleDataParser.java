package stencil.module.util;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stencil.module.MetadataHoleException;
import stencil.module.util.FacetData.MemoryUse;
import stencil.module.util.ann.*;
import stencil.parser.ParseStencil;
import stencil.interpreter.tree.Specializer;
import stencil.tuple.prototype.TuplePrototype;

public class ModuleDataParser {
	public static final class MetadataParseException extends RuntimeException {
		private final String message;

		public MetadataParseException(String message) {
			super();
			this.message = message;
		}
		
		public MetadataParseException(Class source, Exception cause) {
			super(cause);
			message = String.format("%1$s\n(For class %2$s).", super.getMessage(), source.getCanonicalName());
		}
		
		public MetadataParseException(Method source, Exception cause) {
			super(cause);
			message = String.format("%1$s\n(For static method %2$s in %3$s).", super.getMessage(), source.getName(), source.getDeclaringClass().getCanonicalName());
		}
		
		public String getMessage() {return message;}
	}
	
	private static final String EMPTY = "";
	
	private static OperatorData operatorData(Method m, String moduleName) throws Exception {
		Operator o = m.getAnnotation(Operator.class);
		Facet f = m.getAnnotation(Facet.class);
		if (o == null || f == null) {return null;}

		String defaultName = m.getName().substring(0,1).toUpperCase() + m.getName().substring(1);
		final String opName = EMPTY.equals(o.name().trim()) ? defaultName : o.name();
		
		final Specializer spec = ParseStencil.specializer(o.spec());
		FacetData[] facets = makeFacetData(f, m.getName());

		OperatorData od = new OperatorData(moduleName, opName, spec, m.getName(), o.defaultFacet(), Arrays.asList(facets));
		
		return od;
	}



	/**Make the FacetData object from a facet annotation and information about its context.*/
	private static final FacetData[] makeFacetData(Facet f, String target) throws Exception {
		final String[] aliases = f.alias().length ==0 ? new String[]{target} : f.alias();
		
		final MemoryUse memUse = MemoryUse.valueOf(f.memUse().trim().toUpperCase());
		
		final String counterpart = f.counterpart().trim().length()==0 && memUse != MemoryUse.OPAQUE ? aliases[0] : f.counterpart().trim();
		
		final TuplePrototype proto = ParseStencil.prototype(f.prototype(), true);

		FacetData[] results=new FacetData[aliases.length];
		for (int i=0;i<aliases.length; i++) {
			FacetData fd = new FacetData(aliases[i], target, counterpart, memUse, proto);
			results[i] = fd;
		}
		return results;
	}
	
	/**
	 * @param c  Class supplying the operator candidate
	 * @param moduleName Name of the surrounding module
	 * @param suppress Should the suppress flag be adhered to?
	 * @return
	 * @throws MetadataParseException
	 */
	public static OperatorData operatorData(Class c, String moduleName) {
		try {
			Operator o = (Operator) c.getAnnotation(Operator.class);
			if (o == null) {return null;}
			
			final String opName = EMPTY.equals(o.name().trim()) ? c.getSimpleName() : o.name();
			final Specializer spec = ParseStencil.specializer(o.spec());
			
			List<FacetData> facets = new ArrayList();
			for (Method m: c.getMethods()) {
				Facet f = m.getAnnotation(Facet.class);
				if (f== null) {continue;}
	
				try {
					FacetData[] fds = makeFacetData(f, m.getName());
					facets.addAll(Arrays.asList(fds));
				} catch (MetadataHoleException e) {
					throw new MetadataHoleException(moduleName, opName, e.getMessage());
				}
			}
	
			return new OperatorData(moduleName, opName, spec, c.getSimpleName(), o.defaultFacet(), facets);
		} 
		catch (MetadataHoleException e) {throw e;}
		catch (Exception e) {throw new MetadataParseException(c, e);}
	}
	
	/**Given an input stream, will parse the contents 
	 * and return a ModuleData object.
	 * @throws MetadataParseException 
	 */
	public static ModuleData moduleData(Class source) throws MetadataParseException {
		Module ma = (Module) source.getAnnotation(Module.class);
		if (ma == null) {throw new MetadataParseException("No module annotation found in " + source.getCanonicalName());}

		final String moduleName = EMPTY.equals(ma.name().trim()) ? source.getSimpleName():ma.name();			
		ModuleData md = new ModuleData(moduleName);
		
			for (Class c: source.getClasses()) {
				try {
					final OperatorData od = operatorData(c, moduleName);
					if (od == null) {continue;}
					md.addOperator(od);
				} 
				catch (MetadataHoleException e) {throw e;}
				catch (Exception e) {throw new MetadataParseException(c,e);}
			}
			
			for (Method m: source.getMethods()) {
				try {md.addOperator(operatorData(m, moduleName));}
				catch (Exception e) {throw new MetadataParseException(m,e);}
			}

		return md;
	}
	
	public static ModuleData moduleData(String className) throws ClassNotFoundException, MetadataParseException {
		return moduleData(Class.forName(className));
	}
}
