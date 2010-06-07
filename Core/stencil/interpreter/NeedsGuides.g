tree grammar NeedsGuides;
options {
	tokenVocab = Stencil;
	ASTLabelType = CommonTree;	
	filter = true;
}

@header{
/**Operators over a properly formed-AST and ensures that
 * all guides are up-to date.
 */ 

	package stencil.interpreter;
	
	import stencil.parser.tree.*;
	import stencil.tuple.NumericSingleton;	
}

@members {
	private static final Object[] EMPTY_ARGS = new Object[0];
	private static final Map<Object, Integer> stateIDs = new HashMap(); //TODO: Would this be any faster as an array? (The length and offsets can be known at Stencil compile time.)
	private boolean needsGuide;
	 	
	public boolean check(Program program) {
		needsGuide = false;
		downup(program);
		return needsGuide;
	}
	
	public boolean needsGuide(Tree t) {
    AstInvokeable i = (AstInvokeable) t;
    int nowID = (Integer) i.getInvokeable().invoke(EMPTY_ARGS);
         
    if (!stateIDs.containsKey(i)) {
       stateIDs.put(i, nowID+1); //Make it different...
    }
    int cachedID = stateIDs.get(i);
    stateIDs.put(i, nowID);
    return (cachedID != nowID);
	}
}

topdown: ^(STATE_QUERY target*);
target
	: i=AST_INVOKEABLE 
	  {needsGuide =  needsGuide || needsGuide(i);};
