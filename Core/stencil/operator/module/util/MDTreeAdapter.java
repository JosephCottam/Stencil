package stencil.operator.module.util;

import java.util.*;
import java.lang.reflect.*;

import org.antlr.runtime.tree.*;
import org.antlr.runtime.Token;

import stencil.operator.module.*;
import stencil.operator.module.OperatorData.OpType;
import stencil.parser.ProgramParseException;
import stencil.parser.tree.Specializer;
import stencil.parser.string.ParseStencil;
import stencil.util.ANTLRTree;
import stencil.util.ANTLRTree.NameNotFoundException;


class MDTreeAdapter extends CommonTreeAdaptor {
	/**In a static meta-data object, if a value equals the SPECIALIZATION_DEPENDENT_VALUE flag,
	 * it cannot be determined without the specializer.  
	 */
	public static final String SPECIALIZATION_DEPENDENT_VALUE = "%";
	
	protected static class MetaTree extends CommonTree {
		public MetaTree(Token t) {super(t);}
		public MetaTree(MetaTree node) {super(node);}
		
		protected final Tree search(String key) throws NameNotFoundException {return ANTLRTree.search(this, key);}
		protected final Tree search(Tree root, String key) throws NameNotFoundException {return ANTLRTree.search(root, key);}
		
		public MetaTree dupNode() {return new MetaTree(this);}
	}
	
	protected static class FacetData extends MetaTree implements stencil.operator.module.FacetData {
		public FacetData(Token t) {super(t);}
		public FacetData(FacetData node) {super(node);}

		public String getName() {return getToken().getText();}
		public OpType getFacetType() {return OpType.valueOf(search("type").getChild(0).getText());}
		public boolean isProject() {return getFacetType().equals("project");}		
		public boolean isCategorize() {return getFacetType().equals("categorize");}
		public List<String> tupleFields() {return Arrays.asList(search("prototype").getChild(0).getText().split(","));}
		
		/**Complete if nothing depends on the operator specializer 
		 * (indicated by a SPECIALIZATION_DEPENDENT_VALUE instead of a normal value).*/
		public boolean isComplete() {
			for (Tree child: (List<Tree>) this.getChildren()) {
				if (child.getChild(0).getText().equals(SPECIALIZATION_DEPENDENT_VALUE)) {return false;}
			}
			return true;
		}
		
		public FacetData dupNode() {return new FacetData(this);}
	}
	
	protected static class OpData extends MetaTree implements stencil.operator.module.OperatorData {
		public OpData(Token t) {super(t);}
		public OpData(OpData node) {super(node);}

		
		public String getModule() {return ((ModuleData) this.getParent()).getName();}

		public String getName() {return this.getText();}
		public String getTarget() {return getChild(0).getText();}
		public FacetData getFacetData(String name) {return (FacetData) search(getChild(1), name);}
		public Specializer getDefaultSpecializer() {
			String source=getAttribute("specializer");
			try {
				return ParseStencil.parseSpecializer(source);
			} catch (ProgramParseException e) {
				throw new Error(String.format("Error in module meta-data for %1$s.  Specailizer %2$s cannot be parsed.", getModule(),source),e);
			}
		}
		
		public Collection<String> getAttributes() {
			Collection<String> c = new HashSet<String>();
			
			for (int i=0; i< getChild(0).getChildCount(); i++) {
				c.add(getChild(0).getChild(i).getText());
			}
			
			return c;
		}
		
		public String getAttribute(String name) {
			try {
				Tree t = search(getChild(0), name); //Searches the 'options' section...
				return t.getChild(0).getText();		//The child is the 'VAL' node and its text is the value
			} catch (NameNotFoundException e) {return null;}
		}
		
		
		public Collection<String> getFacets() {
			String[] s = new String[getChild(1).getChildCount()];
			for (CommonTree fd: (List<CommonTree>) ((CommonTree) getChild(1)).getChildren()) {
				s[fd.childIndex] = fd.getText();
			}
			return Arrays.asList(s);
		}
		
		/**Complete if all facets are complete.*/
		public boolean isComplete() {
			for (FacetData fd: (List<FacetData>) ((CommonTree) getChild(1)).getChildren()) {
				if (!fd.isComplete()) {return false;}
			}
			return true;
		}
		
		public OpData dupNode() {return new OpData(this);}
	}

	protected static class ModuleData extends MetaTree implements stencil.operator.module.ModuleData {
		protected Module module;
		public ModuleData(Token t) {super(t);}
		public ModuleData(ModuleData node) {super(node);}

		public String getName() {return token.getText();}
		public String getModuleClassName() {return getChild(0).getText();}
		public String getDescription() {return getChild(1).getText();}

		public Class getModuleClass() throws Exception {
			return MDTreeAdapter.class.getClassLoader().loadClass(getModuleClassName());
		}
		
		public Module getModule() {
			if (module == null) {
				Class clss;
				Constructor c;
				
				try {clss = getModuleClass();}
				catch (Exception e) {throw new RuntimeException(String.format("Class '%1$s' not known instantiating module '%2$s'.", getModuleClassName(), getName()));}
				
				try {c = clss.getConstructor(stencil.operator.module.ModuleData.class);}
				catch (Exception e) {throw new RuntimeException(String.format("Could not find constructor with ModuleData argument in class %1$s for module %2$s.",getModuleClassName(), getName()));}
				
				try {module = (Module) c.newInstance(this);}
				catch (Exception e) {throw new RuntimeException(String.format("Error invoking ModuleData constructor for %1$s.", getName()));}
			}
			return module;
		}
				
		public Collection<String> getOperators() {
			Set<String> ops = new HashSet();
			for (Object t: getChildren()) {
				if (t instanceof OpData) {
					ops.add(((OpData) t).getName());
				}
			}
			return ops;
		}
		
		public OperatorData getOperatorData(String name){
			for (Object t: getChildren()) {
				if (t instanceof OpData && ((OpData) t).getName().equals(name)) {
					return (OpData) t;
				}
			}
			throw new IllegalArgumentException("Request for operator data for unknown operator:" + name);			
		}
		
		public Specializer getDefaultSpecializer(String op) {
			return getOpData(op).getDefaultSpecializer();
		}

		public OpData getOpData(String name) {
			for (Object t: getChildren()) {
				if (t instanceof OpData) {
					if (((OpData) t).getName().equals(name)) {return ((OpData) t);}
				}
			}
			throw new IllegalArgumentException("Default specializer requested for legend not in module:" + name);			
		}
		
		public ModuleData dupNode() {return new ModuleData(this);}
	}
	
	public Object create(Token token) {
		if (token == null) {return super.create(token);}
		
		switch (token.getType()) {
			case ModuleDataParser.MODULE_DATA: return new ModuleData(token);
			case ModuleDataParser.OPERATOR: return new OpData(token);
			case ModuleDataParser.FACET: return new FacetData(token);
			default: return new MetaTree(token);
		}
	}
	
}
