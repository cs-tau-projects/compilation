package ast;

import temp.*;
import types.*;

public abstract class AstExp extends AstNode
{
	/***********************************************/
	/* The default semantic action for an AST node */
	/***********************************************/
	@Override
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

	/********************************************************/
	/* Not doing this as abstract because there might be    */
	/* use cases where we don't need IR for an expression   */
	/********************************************************/
	@Override
	public Temp irMe(){
		return null;
	}
}