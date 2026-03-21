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

public abstract class IrCommand
{
	/*****************/
	/* Label Factory */
	/*****************/
	protected static int labelCounter = 0;
	public    static String getFreshLabel(String msg)
	{
		return String.format("Label_%d_%s", labelCounter++,msg);
	}

	public java.util.List<temp.Temp> getUsedTemps() {
		return new java.util.ArrayList<>();
	}

	public java.util.List<temp.Temp> getDefinedTemps() {
		return new java.util.ArrayList<>();
	}

	public abstract void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap);
}
