package ast;

import types.*;

public abstract class AstExp extends AstNode
{
	public types.Type type;
	// semantic analysis
	public Type semantMe() throws SemanticException
	{
		return null;
	}

	// try constant evaluation
	public Integer tryEvaluateConstant()
	{
		return null;
	}
}