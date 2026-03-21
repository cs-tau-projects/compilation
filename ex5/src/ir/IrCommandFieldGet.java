package ir;

import temp.Temp;

public class IrCommandFieldGet extends IrCommand {
    public Temp dst;
    public Temp obj;
    public String className;
    public String fieldName;

    public IrCommandFieldGet(Temp dst, Temp obj, String className, String fieldName) {
        this.dst = dst;
        this.obj = obj;
        this.className = className;
        this.fieldName = fieldName;
    }

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (obj != null) list.add(obj);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (dst != null) list.add(dst);
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		gen.emitInstruction("lw", regMap.get(dst), "4(" + regMap.get(obj) + ")");
	}
}
