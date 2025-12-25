/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.List;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import temp.*;

public class IrCommandCall extends IrCommand
{
	public Temp dst;
	public String label;
	public List<Temp> args;
	
	public IrCommandCall(Temp dst, String label, List<Temp> args)
	{
		this.dst = dst;
		this.label = label;
		this.args = args;
	}
}