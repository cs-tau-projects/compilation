package ast;

import types.*;

public abstract class AstVar extends AstNode
{
	public Type type;
	// semant
	public abstract Type semantMe() throws SemanticException;
}
