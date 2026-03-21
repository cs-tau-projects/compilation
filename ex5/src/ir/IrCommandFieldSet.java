package ir;

import temp.Temp;

public class IrCommandFieldSet extends IrCommand {
    public Temp obj;
    public String className;
    public String fieldName;
    public Temp src;

    public IrCommandFieldSet(Temp obj, String className, String fieldName, Temp src) {
        this.obj = obj;
        this.className = className;
        this.fieldName = fieldName;
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
		gen.emitInstruction("sw", regMap.get(src), "4(" + regMap.get(obj) + ")");
	}
}
