package ir;

import temp.*;

// print int
public class IrCommandPrintInt extends IrCommand
{
	public Temp t;

	public IrCommandPrintInt(Temp t)
	{
		this.t = t;
	}

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (t != null) list.add(t);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		gen.emitInstruction("move", "$a0", regMap.get(t));
		gen.emitInstruction("li", "$v0", "1");
		gen.emitInstruction("syscall");
		gen.emitInstruction("la", "$a0", "msg_space");
		gen.emitInstruction("li", "$v0", "4");
		gen.emitInstruction("syscall");
	}
}
