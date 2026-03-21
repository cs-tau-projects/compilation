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
		gen.emitInstruction("mul", "$a0", "$a0", "4");
		gen.emitInstruction("add", "$a0", "$a0", "4");
		gen.emitInstruction("li", "$v0", "9");
		gen.emitInstruction("syscall");
		gen.emitInstruction("sw", regMap.get(sizeTemp), "0($v0)");
		gen.emitInstruction("move", regMap.get(dst), "$v0");
	}
}
