package ir;
import temp.*;

public class IrCommandBinopDivIntegers extends IrCommand
{
	public Temp t1;
	public Temp t2;
	public Temp dst;
	
	public IrCommandBinopDivIntegers(Temp dst, Temp t1, Temp t2)
	{
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (t1 != null) list.add(t1);
		if (t2 != null) list.add(t2);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (dst != null) list.add(dst);
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		gen.emitInstruction("div", regMap.get(t1), regMap.get(t2));
		gen.emitInstruction("mflo", regMap.get(dst));
		
		String skipLabel = "Label_div_skip_" + Math.abs(this.hashCode());
		gen.emitInstruction("mfhi", "$s0");
		gen.emitInstruction("beq", "$s0", "$zero", skipLabel);
		gen.emitInstruction("xor", "$s1", regMap.get(t1), regMap.get(t2));
		gen.emitInstruction("bgez", "$s1", skipLabel);
		gen.emitInstruction("addiu", regMap.get(dst), regMap.get(dst), "-1");
		gen.emitLabel(skipLabel);
		
		gen.addSaturation(regMap.get(dst));
	}
}
