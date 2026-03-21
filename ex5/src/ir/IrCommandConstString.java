package ir;

import temp.Temp;

public class IrCommandConstString extends IrCommand {
    public Temp dst;
    public String value;

    public IrCommandConstString(Temp dst, String value) {
        this.dst = dst;
        this.value = value;
    }

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (dst != null) list.add(dst);
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		String label = "str_" + java.util.UUID.randomUUID().toString().replace("-", "");
		gen.emitDataString(label, "\"" + value + "\"");
		gen.emitInstruction("la", regMap.get(dst), label);
	}
}
