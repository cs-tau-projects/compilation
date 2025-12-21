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

public class IrCommandLabel extends IrCommand
{
	String labelName;
	
	public IrCommandLabel(String labelName)
	{
		this.labelName = labelName;
	}
}
