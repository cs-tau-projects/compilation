package ir;

import temp.Temp;

public class IrCommandNewObject extends IrCommand {
    public Temp dst;
    public String className;

    public IrCommandNewObject(Temp dst, String className) {
        this.dst = dst;
        this.className = className;
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
		gen.emitInstruction("li", "$a0", "8");
		gen.emitInstruction("li", "$v0", "9");
		gen.emitInstruction("syscall");
		gen.emitInstruction("la", "$t0", "vtable_" + className);
		gen.emitInstruction("sw", "$t0", "0($v0)");
		gen.emitInstruction("move", regMap.get(dst), "$v0");
	}
}
