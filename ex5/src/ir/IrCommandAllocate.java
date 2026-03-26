/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/

public class IrCommandAllocate extends IrCommand
{
	public VarId varId;

	public IrCommandAllocate(VarId varId)
	{
		this.varId = varId;
	}

	/****************************************/
	/* Convenience constructors            */
	/****************************************/
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

	public IrCommandAllocate(String varName, int scopeOffset, VarId.Kind kind, int fpOffset)
	{
		this.varId = new VarId(varName, scopeOffset, kind, fpOffset);
		this.isGlobal = (kind == VarId.Kind.GLOBAL);
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
		if (varId.kind == VarId.Kind.GLOBAL || this.isGlobal) {
		    gen.emitGlobalWord("global_" + varId.name, 0);
		}
		// For PARAM and LOCAL, the prologue already allocated the space.
		// No MIPS code needed here — offset is tracked in the VarId.
	}
}
