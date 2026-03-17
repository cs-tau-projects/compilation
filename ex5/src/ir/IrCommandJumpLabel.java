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

public class IrCommandJumpLabel extends IrCommand
{
	public String labelName;

	public IrCommandJumpLabel(String labelName)
	{
		this.labelName = labelName;
	}
}
