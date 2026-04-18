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
		gen.emitInstruction("move", "$sp", "$fp");
		for (int i = 0; i < 10; i++) {
		    gen.emitInstruction("lw", "$t" + i, (i*4) + "($sp)");
		}
		for (int i = 0; i < 8; i++) {
		    gen.emitInstruction("lw", "$s" + i, (40 + i*4) + "($sp)");
		}
		gen.emitInstruction("lw", "$ra", "72($sp)");
		gen.emitInstruction("lw", "$fp", "76($sp)");
		gen.emitInstruction("addiu", "$sp", "$sp", "80");
		gen.emitInstruction("jr", "$ra");
	}
}
