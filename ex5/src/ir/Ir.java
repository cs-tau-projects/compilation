package ir;

import java.util.ArrayList;
import java.util.List;

public class Ir {
	// IR commands list
	private List<IrCommand> commands = new ArrayList<>();

	// add command
	public void AddIrCommand(IrCommand cmd) {
		commands.add(cmd);
	}

	/****************************************/
	/* Get all IR commands for analysis */
	/****************************************/
	public List<IrCommand> getCommands() {
		return commands;
	}

	/****************************************/
	/* Get the number of IR commands */
	/****************************************/
	public int size() {
		return commands.size();
	}

	/****************************************/
	/* Reset the IR (for testing purposes) */
	/****************************************/
	public void reset() {
		commands.clear();
	}

	// --- Singleton Implementation ---
	private static Ir instance = null;

	protected Ir() {
	}

	public static Ir getInstance() {
		if (instance == null) {
			instance = new Ir();
		}
		return instance;
	}
}
