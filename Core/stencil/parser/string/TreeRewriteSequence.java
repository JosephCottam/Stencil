package stencil.parser.string;

import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeRewriter;
import org.antlr.runtime.tree.TreeVisitor;
import org.antlr.runtime.tree.TreeVisitorAction;

public abstract class TreeRewriteSequence extends TreeRewriter {

	public TreeRewriteSequence(TreeNodeStream input) {
		super(input, new RecognizerSharedState());
		// TODO Auto-generated constructor stub
	}
    public TreeRewriteSequence(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);  
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


}
