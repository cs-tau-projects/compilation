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

public class IrCommandPrintString extends IrCommand
{
	Temp t;
	
	public IrCommandPrintString(Temp t)
	{
		this.t = t;
	}
}