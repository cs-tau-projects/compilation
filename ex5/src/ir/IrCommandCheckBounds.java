package ir;

import temp.Temp;

public class IrCommandCheckBounds extends IrCommand {
    public Temp array;
    public Temp index;

    public IrCommandCheckBounds(Temp array, Temp index) {
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
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		gen.emitInstruction("lw", "$t0", "0(" + regMap.get(array) + ")");
		gen.emitInstruction("bge", regMap.get(index), "$t0", "Label_access_violation");
		gen.emitInstruction("blt", regMap.get(index), "$zero", "Label_access_violation");
	}
}
