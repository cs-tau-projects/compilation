package ir;

import java.util.ArrayList;
import java.util.List;

// ir manager
public class Ir
{
	// command list
	private List<IrCommand> commands = new ArrayList<>();

	// add command
	public void AddIrCommand(IrCommand cmd)
	{
		commands.add(cmd);
	}

	// get all commands
	public List<IrCommand> getCommands()
	{
		return commands;
	}

	// num commands
	public int size()
	{
		return commands.size();
	}

	// reset ir
	public void reset()
	{
		commands.clear();
	}

	// singleton
	private static Ir instance = null;

	protected Ir() {}

	public static Ir getInstance()
	{
		if (instance == null)
		{
			// singleton instance
			instance = new Ir();
		}
		return instance;
	}
}
