/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import temp.*;

public class IrCommandLoad extends IrCommand
{
	public Temp dst;
	public VarId varId;

	public IrCommandLoad(Temp dst, VarId varId)
	{
		this.dst   = dst;
		this.varId = varId;
	}

	/****************************************/
	/* Convenience constructor for backward */
	/* compatibility during transition      */
	/****************************************/
	public IrCommandLoad(Temp dst, String varName, int scopeOffset)
	{
		this.dst   = dst;
		this.varId = new VarId(varName, scopeOffset);
	}
}
