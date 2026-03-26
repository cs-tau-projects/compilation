package ir;

import temp.Temp;

public class IrCommandReturn extends IrCommand {
    public Temp src;

    public IrCommandReturn(Temp src) {
        this.src = src; // can be null if void return
    }

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (src != null) list.add(src);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		if (src != null) gen.emitInstruction("move", "$v0", regMap.get(src));
		// Restore stack pointer to frame pointer
		gen.emitInstruction("move", "$sp", "$fp");
		// Restore old frame pointer (saved at 0($fp))
		gen.emitInstruction("lw", "$fp", "0($sp)");
		// Restore return address (saved at 4($fp), which is now 4($sp))
		gen.emitInstruction("lw", "$ra", "4($sp)");
		// Pop saved $fp and $ra
		gen.emitInstruction("addu", "$sp", "$sp", "8");
		// Return to caller
		gen.emitInstruction("jr", "$ra");
	}
}
