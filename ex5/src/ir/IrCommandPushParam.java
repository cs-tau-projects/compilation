package ir;

import temp.Temp;

public class IrCommandPushParam extends IrCommand {
    public Temp param;

    public IrCommandPushParam(Temp param) {
        this.param = param;
    }

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (param != null) list.add(param);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		gen.emitInstruction("sw", regMap.get(param), "0($sp)");
		gen.emitInstruction("sub", "$sp", "$sp", "4");
	}
}
