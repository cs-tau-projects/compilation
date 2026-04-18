package ir;
import temp.*;

public class IrCommandBinopMulIntegers extends IrCommand
{
	public Temp t1;
	public Temp t2;
	public Temp dst;
	
	public IrCommandBinopMulIntegers(Temp dst, Temp t1, Temp t2)
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
		gen.emitInstruction("mul", regMap.get(dst), regMap.get(t1), regMap.get(t2));
		gen.addSaturation(regMap.get(dst));
	}
}
