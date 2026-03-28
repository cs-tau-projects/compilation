package ir;

import temp.Temp;

public class IrCommandNewArray extends IrCommand {
    public Temp dst;
    public Temp sizeTemp;

    public IrCommandNewArray(Temp dst, Temp sizeTemp) {
        this.dst = dst;
        this.sizeTemp = sizeTemp;
    }

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (sizeTemp != null) list.add(sizeTemp);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (dst != null) list.add(dst);
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		gen.emitInstruction("move", "$a0", regMap.get(sizeTemp));
		gen.emitInstruction("sll", "$a0", "$a0", "2");
		gen.emitInstruction("addiu", "$a0", "$a0", "4");
		gen.emitInstruction("li", "$v0", "9");
		gen.emitInstruction("syscall");
		
		// Move pointer to $t8 immediately to protect it
		gen.emitInstruction("move", "$t8", "$v0");

		// Recalculate total size in $t7 after syscall (regMap.get(sizeTemp) is callee-saved and safe)
		gen.emitInstruction("move", "$t7", regMap.get(sizeTemp));
		gen.emitInstruction("sll", "$t7", "$t7", "2");
		gen.emitInstruction("addiu", "$t7", "$t7", "4");

		// Zero-initialize memory (skip size at offset 0)
		String labelStart = IrCommand.getFreshLabel("zero_start_array");
		String labelEnd = IrCommand.getFreshLabel("zero_end_array");

		gen.emitInstruction("li", "$t9", "4"); // Progress counter (offset) in protected $t9
		gen.emitLabel(labelStart);
		// Check against saved $t7 bound
		gen.emitInstruction("bge", "$t9", "$t7", labelEnd);
		gen.emitInstruction("addu", "$v1", "$t8", "$t9"); // Element address in $v1
		gen.emitInstruction("sw", "$zero", "0($v1)");
		gen.emitInstruction("addiu", "$t9", "$t9", "4");
		gen.emitInstruction("j", labelStart);
		gen.emitLabel(labelEnd);

		gen.emitInstruction("sw", regMap.get(sizeTemp), "0($t8)");
		gen.emitInstruction("move", regMap.get(dst), "$t8");
	}
}
