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
	Temp t;
	int value;
	
	public IRcommandConstInt(Temp t, int value)
	{
		this.t = t;
		this.value = value;
	}
}
