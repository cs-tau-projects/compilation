package ir;

import temp.Temp;

public class IrCommandFieldSet extends IrCommand {
    public Temp obj;
    public int offset;
    public Temp src;

    public IrCommandFieldSet(Temp obj, int offset, Temp src) {
        this.obj = obj;
        this.offset = offset;
        this.src = src;
    }

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (obj != null) list.add(obj);
		if (src != null) list.add(src);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		gen.emitInstruction("sw", regMap.get(src), offset + "(" + regMap.get(obj) + ")");
	}
}
