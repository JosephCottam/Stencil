package stencil.parser.string;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeFilter;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeVisitor;
import org.antlr.runtime.tree.TreeVisitorAction;

import stencil.parser.ParseStencil;

public abstract class TreeFilterSequence extends TreeFilter {
	private static final String DEFAULT_UP_OPERATION = "bottomup";
	protected static final class operation implements fptr {
		final Method method;
		final Object target;
		public operation(Object target, String name) {
			try {
				this.target = target;
				this.method = target.getClass().getMethod(name);
			} catch (Exception e) {
				throw new Error("Incorrectly specified tree operation in a sequence: " + name);
			}
		}
		
		public void rule() throws RecognitionException {
			try {
				method.invoke(target);
			} catch (Exception e) {
				if (e.getCause() instanceof RecognitionException) {
					throw (RecognitionException) e.getCause();
				} else {
					throw new Error("Error invoking sequence item.",e);
				}
			}
		}
		
	}
	
	public TreeFilterSequence(TreeNodeStream input) {
		super(input, new RecognizerSharedState());
	}
    public TreeFilterSequence(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);  
    }
    
    protected Object downup(Object t, TreeFilter target, String down) {return downup(t, target, down, DEFAULT_UP_OPERATION);}
    protected Object downup(Object t, TreeFilter target, String down, String up) {
    	return downup(t, new operation(target, down), new operation(target, up));

    }
    protected Object downup(Object t, final fptr down, final fptr up) {
        TreeVisitor v = new TreeVisitor(new CommonTreeAdaptor());
        TreeVisitorAction actions = new TreeVisitorAction() {
            public Object pre(Object tree)  {applyOnce(tree, down); return tree;}
            public Object post(Object tree) {applyOnce(tree, up); return tree;}
        };
        t = v.visit(t, actions);
        return t;    
    }

    /**This is a convenience method for executing filter sequences.
     * It is suggested that each filter include an apply method that calls this method
     * with the appropriate class and addition arguments.
     */
    protected static void apply(Tree p, Class implementing, Object... args) {
    	TreeFilterSequence fs;
    	try {
	    	Constructor<? extends TreeFilterSequence> c = implementing.getConstructor(TreeNodeStream.class);
	    	fs = c.newInstance(ParseStencil.TOKEN_STREAM);
    	} catch (Exception e){
    		throw new Error("Tree sequence does not provide required constructor.", e);
    	}
    	fs.setup(args);
    	fs.downup(p);    	
    }    

    /**This method is invoked by the static apply method.
     * The args passed to the static apply will be passed here.
     * @param objects
     */
    protected void setup(Object...objects) {}
}
