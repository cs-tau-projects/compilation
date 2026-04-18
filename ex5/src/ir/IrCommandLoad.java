package ir;
import temp.*;

public class IrCommandLoad extends IrCommand
{
	public Temp dst;
	public VarId varId;

	public IrCommandLoad(Temp dst, VarId varId)
	{
		this.dst   = dst;
		this.varId = varId;
	}

	// convenience constructor
	public boolean isGlobal;
	public IrCommandLoad(Temp dst, String varName, int scopeOffset, boolean isGlobal)
	{
		this.dst   = dst;
		this.varId = new VarId(varName, scopeOffset);
		this.isGlobal = isGlobal;
	}

	public IrCommandLoad(Temp dst, String varName, int scopeOffset)
	{
		this.dst   = dst;
		this.varId = new VarId(varName, scopeOffset);
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
		if (this.isGlobal) {
		    gen.emitInstruction("lw", regMap.get(dst), "global_" + varId.name);
		} else {
		    gen.emitInstruction("lw", regMap.get(dst), gen.getLocalOffset(varId.scopeOffset) + "($fp)");
		}
	}
}
