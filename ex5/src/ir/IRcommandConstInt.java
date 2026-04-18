package ir;

import temp.*;

public class IRcommandConstInt extends IrCommand
{
	public Temp t;
	public int value;

	public IRcommandConstInt(Temp t, int value)
	{
		this.t = t;
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
		if (t != null) list.add(t);
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		gen.emitInstruction("li", regMap.get(t), String.valueOf(value));
	}
}
