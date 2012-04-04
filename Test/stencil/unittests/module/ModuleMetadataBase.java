package stencil.unittests.module;


import stencil.module.util.ann.Module;
import stencil.module.util.ann.StreamTypes;
import stencil.unittests.StencilTestCase;

public abstract class ModuleMetadataBase extends StencilTestCase {
	protected final Class target;
	protected final int streams;
	
	protected ModuleMetadataBase(Class target, int streams){
		this.target = target;
		this.streams = streams;
	}
	
	public void testStreamTypes()  throws Exception {
		if (streams == 0) {return;}
		
		StreamTypes t = (StreamTypes) target.getAnnotation(StreamTypes.class);
		assertNotNull("Stream types annotation not found.", t);
		assertEquals("Unexpected number of streams indicated.", streams, t.classes().length);
	}
	
	public void testModuleTag()  throws Exception {
		Module t = (Module) target.getAnnotation(Module.class);
		assertNotNull("Module annotation not found.", t);
	}
}
