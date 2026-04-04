package ir;

import temp.Temp;

public class IrCommandNewObject extends IrCommand {
    public Temp dst;
    public String className;
    public int size;

    public IrCommandNewObject(Temp dst, String className, int size) {
        this.dst = dst;
        this.className = className;
        this.size = size;
    }

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (dst != null) list.add(dst);
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		gen.emitInstruction("li", "$a0", String.valueOf(size));
		gen.emitInstruction("li", "$v0", "9");
		gen.emitInstruction("syscall");
		
		// Move pointer to $t8 immediately to protect it
		gen.emitInstruction("move", "$t8", "$v0");

		// Zero-initialize memory (skip vtable at offset 0)
		String labelStart = IrCommand.getFreshLabel("zero_start");
		String labelEnd = IrCommand.getFreshLabel("zero_end");
		gen.emitInstruction("li", "$t9", "4"); // Progress counter (offset)
		gen.emitLabel(labelStart);
		gen.emitInstruction("bge", "$t9", String.valueOf(size), labelEnd);
		gen.emitInstruction("addu", "$v1", "$t8", "$t9"); // Element address
		gen.emitInstruction("sw", "$zero", "0($v1)");
		gen.emitInstruction("addiu", "$t9", "$t9", "4");
		gen.emitInstruction("j", labelStart);
		gen.emitLabel(labelEnd);

		gen.emitInstruction("la", "$v1", "vtable_" + className);
		gen.emitInstruction("sw", "$v1", "0($t8)");
		gen.emitInstruction("move", regMap.get(dst), "$t8");
	}
}
