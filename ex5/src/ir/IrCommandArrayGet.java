package ir;

import temp.Temp;

public class IrCommandArrayGet extends IrCommand {
    public Temp dst;
    public Temp array;
    public Temp index;

    public IrCommandArrayGet(Temp dst, Temp array, Temp index) {
        this.dst = dst;
        this.array = array;
        this.index = index;
    }

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (array != null) list.add(array);
		if (index != null) list.add(index);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (dst != null) list.add(dst);
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		gen.emitInstruction("sll", "$v1", regMap.get(index), "2");
		gen.emitInstruction("addu", "$v1", "$v1", regMap.get(array));
		gen.emitInstruction("lw", regMap.get(dst), "4($v1)");
	}
}
