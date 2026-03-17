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

public class IRcommandConstInt extends IrCommand
{
	public Temp t;
	public int value;

	public IRcommandConstInt(Temp t, int value)
	{
		this.t = t;
		this.value = value;
	}
}
