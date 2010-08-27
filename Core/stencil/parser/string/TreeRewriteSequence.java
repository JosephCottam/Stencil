package stencil.parser.string;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeRewriter;
import org.antlr.runtime.tree.TreeVisitor;
import org.antlr.runtime.tree.TreeVisitorAction;

import stencil.parser.ParseStencil;

public abstract class TreeRewriteSequence extends TreeRewriter {
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
		
		public Object rule() throws RecognitionException {
			try {
				return method.invoke(target);
			} catch (Exception e) {
				if (e.getCause() instanceof RecognitionException) {
					throw (RecognitionException) e.getCause();
				} else {
					throw new Error("Error invoking sequence item.",e);
				}
			}
		}
		
	}
	
	
	public TreeRewriteSequence(TreeNodeStream input) {
		super(input, new RecognizerSharedState());
	}
    public TreeRewriteSequence(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);  
    }
    
    protected Object downup(Object t, TreeRewriter target, String down) {return downup(t, target, down, DEFAULT_UP_OPERATION);}
    protected Object downup(Object t, TreeRewriter target, String down, String up) {
    	return downup(t, new operation(target, down), new operation(target, up));

    }
    protected Object downup(Object t, final fptr down, final fptr up) {
        TreeVisitor v = new TreeVisitor(new CommonTreeAdaptor());
        TreeVisitorAction actions = new TreeVisitorAction() {
            public Object pre(Object tree)  { return applyOnce(tree, down); }
            public Object post(Object tree) { return applyRepeatedly(tree, up); }
        };
        t = v.visit(t, actions);
        return t;    
    }


    /**Preferred method for executing ALL stencil rewrite sequences.
     * 
     * All classes that sub-class should include their own apply method.
     * The default implementation of which should be:
     * 
     * public static Tree apply(Tree tree) { 
     *  return (Tree) TreeFilterSequence.apply(tree);
     * }
     * 
     * The static reference is suggested since method uses stack trace inspection
     * to detect the declaring class of the calling method (a horrible, horrible hack...but it makes calling parsers so much easier).
     * That class  will then be instantiated with the additional arguments passed (if any).
     */
    protected static Tree apply(Tree p, Object... args) {
    	Class implementing;
    	try {
    		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
    		implementing = Class.forName(stack[2].getClassName());	//Prior frame should contain the name of the actual implementing class...
    	} catch (Exception e) {
    		throw new Error("Inspection failure trying to determine implementing class.");
    	}
    	return apply(p, implementing, args);
    }
    
    
    private static Tree apply(Tree p, Class implementing, Object... args) {
    	TreeRewriteSequence fs;
    	try {
	    	Constructor<? extends TreeRewriteSequence> c = implementing.getConstructor(TreeNodeStream.class);
	    	fs = c.newInstance(ParseStencil.TOKEN_STREAM);
	    	fs.setTreeAdaptor(ParseStencil.TREE_ADAPTOR);
    	} catch (Exception e){
    		throw new Error("Tree sequence does not provide required constructor.", e);
    	}
    	fs.setup(args);
    	return (Tree) fs.downup(p);    	
    }
    
    protected void setup(Object... args) {}
    public abstract void setTreeAdaptor(TreeAdaptor adaptor);
}
