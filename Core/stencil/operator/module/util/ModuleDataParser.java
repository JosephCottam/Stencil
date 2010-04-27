package stencil.operator.module.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;

import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import stencil.parser.ParseStencil;
import stencil.parser.tree.Specializer;
import stencil.testUtilities.StringUtils;
import stencil.tuple.prototype.TuplePrototype;



public class ModuleDataParser {
	/**Constructor class to handle loading the stencil-specific constructs.*/
	private static class StencilYAMLConstructor extends Constructor {
	    public StencilYAMLConstructor(Class root) {
	    	super(root);
	        this.yamlConstructors.put("!spec", new ConstructSpecializer());
	        this.yamlConstructors.put("!proto", new ConstructPrototype());
	    }

	    private class ConstructPrototype extends AbstractConstruct {
	        public TuplePrototype construct(Node node) {
	            String source = (String) constructScalar((ScalarNode) node);
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

		Loader loader = new Loader(constructor);
		Yaml yaml = new Yaml(loader);

		ModuleData md = (ModuleData) yaml.load(source);
		return md;
	}
	
	public static ModuleData load(String fileName) throws Exception {
		return load(new FileInputStream(fileName));
	}
	
	public static void main(String[] args) throws Exception {
		Collection<String> files= StringUtils.allFiles("./configs/", ".yml");
		if (files.size() ==0) {System.err.println("No files found.");}
		
		for (String file: files) {
			String name = new File(file).getName();
			try {
				load(new FileInputStream(file));
				System.out.println("Loaded: " + name);
			}
			catch (Throwable e) {
				System.err.println("Error parsing: " + name);
				e.printStackTrace();
			}			
		}
	}

	
}
