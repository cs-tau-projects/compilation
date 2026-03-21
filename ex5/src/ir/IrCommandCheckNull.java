package ir;

import temp.Temp;

public class IrCommandCheckNull extends IrCommand {
    public Temp ptr;

    public IrCommandCheckNull(Temp ptr) {
        this.ptr = ptr;
    }

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (ptr != null) list.add(ptr);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		gen.emitInstruction("beq", regMap.get(ptr), "$zero", "Label_invalid_ptr_deref");
	}
}
