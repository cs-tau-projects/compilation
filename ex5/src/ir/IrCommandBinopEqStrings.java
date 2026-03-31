package ir;

import temp.Temp;

public class IrCommandBinopEqStrings extends IrCommand {
    public Temp dst;
    public Temp left;
    public Temp right;

    public IrCommandBinopEqStrings(Temp dst, Temp left, Temp right) {
        this.dst = dst;
        this.left = left;
        this.right = right;
    }

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (left != null) list.add(left);
		if (right != null) list.add(right);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (dst != null) list.add(dst);
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		gen.emitInstruction("move", "$a0", regMap.get(left));
		gen.emitInstruction("move", "$a1", regMap.get(right));
		gen.emitInstruction("jal", "Runtime_StrEq");
		gen.emitInstruction("move", regMap.get(dst), "$v0");
	}
}
