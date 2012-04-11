package stencil.parser.string.util;

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

    /**Preferred method for executing ALL stencil filter sequences.
     * 
     * All classes that sub-class should include their own apply method.
     * The default implementation of which should be:
     * 
     * public static void apply(Tree tree) { 
     *  TreeFilterSequence.apply(tree);
     * }
     * 
     * The static reference is suggested since method uses stack trace inspection
     * to detect the declaring class of the calling method (a horrible, horrible hack...but it makes calling parsers so much easier).
     * That class  will then be instantiated with the additional arguments passed (if any).
     */    
    protected static void apply(Tree p, Object... args) {
    	Class implementing;
    	try {
    		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
    		implementing = Class.forName(stack[2].getClassName());	//Prior frame should contain the name of the actual implementing class...
    	} catch (Exception e) {
    		throw new Error("Inspection failure trying to determine implementing class.");
    	}
    	apply(p, implementing, args);
    }
    
    private static void apply(Tree p, Class implementing, Object... args) {
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
