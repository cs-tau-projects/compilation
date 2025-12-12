package ast;

import types.*;

public abstract class AstExp extends AstNode
{
	/***********************************************/
	/* The default semantic action for an AST node */
	/***********************************************/
	public Type semantMe() throws SemanticException
	{
		return null;
	}

	/********************************************************/
	/* Try to evaluate this expression as a constant        */
	/* Returns the integer value if constant, null otherwise */
	/********************************************************/
	public Integer tryEvaluateConstant()
	{
		return null;
	}
}