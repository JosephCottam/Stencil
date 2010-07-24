package stencil.parser.string;

import java.lang.reflect.Method;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeFilter;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeVisitor;
import org.antlr.runtime.tree.TreeVisitorAction;

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


}
