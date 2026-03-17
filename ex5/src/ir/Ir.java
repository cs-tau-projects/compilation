/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.ArrayList;
import java.util.List;

/*******************/
/* PROJECT IMPORTS */
/*******************/

public class Ir
{
	/****************************************/
	/* Store IR commands in an ArrayList   */
	/* for easy iteration during analysis  */
	/****************************************/
	private List<IrCommand> commands = new ArrayList<>();

	/******************/
	/* Add Ir command */
	/******************/
	public void AddIrCommand(IrCommand cmd)
	{
		commands.add(cmd);
	}

	/****************************************/
	/* Get all IR commands for analysis    */
	/****************************************/
	public List<IrCommand> getCommands()
	{
		return commands;
	}

	/****************************************/
	/* Get the number of IR commands       */
	/****************************************/
	public int size()
	{
		return commands.size();
	}

	/****************************************/
	/* Reset the IR (for testing purposes) */
	/****************************************/
	public void reset()
	{
		commands.clear();
	}

	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static Ir instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected Ir() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static Ir getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new Ir();
		}
		return instance;
	}
}
