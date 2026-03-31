package ir;

import temp.Temp;

public class IrCommandMove extends IrCommand {
    public Temp dst;
    public Temp src;

    public IrCommandMove(Temp dst, Temp src) {
        this.dst = dst;
        this.src = src;
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
		if (dst != null) list.add(dst);
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		gen.emitInstruction("move", regMap.get(dst), regMap.get(src));
	}
}
