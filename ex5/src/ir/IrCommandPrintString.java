package ir;

import temp.Temp;

public class IrCommandPrintString extends IrCommand {
	public Temp strTemp;

	public IrCommandPrintString(Temp strTemp) {
		this.strTemp = strTemp;
	}

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (strTemp != null)
			list.add(strTemp);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		gen.emitInstruction("move", "$a0", regMap.get(strTemp));
		gen.emitInstruction("li", "$v0", "4");
		gen.emitInstruction("syscall");
	}
}
