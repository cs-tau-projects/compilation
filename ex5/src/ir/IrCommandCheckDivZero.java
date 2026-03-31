package ir;

import temp.Temp;

public class IrCommandCheckDivZero extends IrCommand {
    public Temp denominator;

    public IrCommandCheckDivZero(Temp denominator) {
        this.denominator = denominator;
    }

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (denominator != null) list.add(denominator);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		gen.emitInstruction("beq", regMap.get(denominator), "$zero", "Label_division_by_zero");
	}
}
