package stencil.module.util;

import java.io.FileInputStream;
import java.io.InputStream;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

import stencil.parser.ParseStencil;
import stencil.parser.tree.Specializer;
import stencil.tuple.prototype.TuplePrototype;

public class ModuleDataParser {
	/**Constructor class to handle loading the stencil-specific constructs.*/
	private static class StencilYAMLConstructor extends Constructor {
	    public StencilYAMLConstructor(Class root) {
	    	super(root);
	        this.yamlConstructors.put(new Tag("!spec"), new ConstructSpecializer());
	        this.yamlConstructors.put(new Tag("!proto"), new ConstructPrototype());
	    }

	    private class ConstructPrototype extends AbstractConstruct {
	        public TuplePrototype construct(Node node) {
	            String source = (String) constructScalar((ScalarNode) node);
	            if (source.equals("NULL")) {source = "";}
	            
	            source = String.format("(%1$s)", source);
	            try {
	            	return ParseStencil.parsePrototype(source,false);
	            } catch (Exception e) {throw new RuntimeException("Error parsing configuration defined prototype: " + source, e);}
	        }
	    }

	    private class ConstructSpecializer extends AbstractConstruct {
	        public Specializer construct(Node node) {
	            String source = (String) constructScalar((ScalarNode) node);
	            try {
	            	return ParseStencil.parseSpecializer(source);
	            } catch (Exception e) {throw new RuntimeException("Error parsing configuration defined specializer: " + source, e);}
	        }
	    }
	}
	
	/**Given an input stream, will parse the contents 
	 * and return a ModuleData object.
	 */
	public static ModuleData load(InputStream source) {
		Constructor constructor = new StencilYAMLConstructor(ModuleData.class);
		TypeDescription mdDesc = new TypeDescription(ModuleData.class);
		mdDesc .putListPropertyType("operators", OperatorData.class);
		constructor.addTypeDescription(mdDesc);

		Yaml yaml = new Yaml(constructor);

		ModuleData md = (ModuleData) yaml.load(source);
		return md;
	}
	
	public static ModuleData load(String fileName) throws Exception {
		return load(new FileInputStream(fileName));
	}
}
