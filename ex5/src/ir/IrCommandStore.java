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
import temp.*;

public class IrCommandStore extends IrCommand
{
	public VarId varId;
	public Temp src;

	public IrCommandStore(VarId varId, Temp src)
	{
		this.src   = src;
		this.varId = varId;
	}

	/****************************************/
	/* Convenience constructors            */
	/****************************************/
	public boolean isGlobal;
	public IrCommandStore(String varName, int scopeOffset, Temp src, boolean isGlobal)
	{
		this.src   = src;
		this.varId = new VarId(varName, scopeOffset);
		this.isGlobal = isGlobal;
	}

	public IrCommandStore(String varName, int scopeOffset, Temp src)
	{
		this.src   = src;
		this.varId = new VarId(varName, scopeOffset);
	}

	public IrCommandStore(String varName, int scopeOffset, VarId.Kind kind, int fpOffset, Temp src)
	{
		this.src   = src;
		this.varId = new VarId(varName, scopeOffset, kind, fpOffset);
		this.isGlobal = (kind == VarId.Kind.GLOBAL);
	}

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (src != null) list.add(src);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		if (varId.kind == VarId.Kind.GLOBAL || this.isGlobal) {
		    gen.emitInstruction("sw", regMap.get(src), "global_" + varId.name);
		} else {
		    // PARAM or LOCAL — use fp-relative offset
		    gen.emitInstruction("sw", regMap.get(src), varId.fpOffset + "($fp)");
		}
	}
}
