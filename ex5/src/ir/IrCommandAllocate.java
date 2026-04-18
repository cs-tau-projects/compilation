package ir;

// allocate local/global
public class IrCommandAllocate extends IrCommand
{
	public VarId varId;

	public IrCommandAllocate(VarId varId)
	{
		this.varId = varId;
	}

	// backward compat constructor
	public boolean isGlobal;
	public IrCommandAllocate(String varName, int scopeOffset, boolean isGlobal)
	{
		this.varId = new VarId(varName, scopeOffset);
		this.isGlobal = isGlobal;
	}

	public IrCommandAllocate(String varName, int scopeOffset)
	{
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
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		if (this.isGlobal) {
		    gen.emitGlobalWord("global_" + varId.name, 0);
		} else {
		    gen.allocateLocal(varId.scopeOffset);
		}
	}
}
