package ast;

import types.*;

public abstract class AstVar extends AstNode
{
	/***********************************************/
	/* Abstract method for semantic analysis      */
	/* Must be implemented by all subclasses      */
	/***********************************************/
	public abstract Type semantMe() throws SemanticException;
}
