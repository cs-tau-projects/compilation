/***********/
/* PACKAGE */
/***********/
package ir;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.Objects;

/*******************/
/* PROJECT IMPORTS */
/*******************/

/**
 * VarId uniquely identifies a variable by its name and scope offset.
 * Also carries addressing information for MIPS code generation:
 * whether the variable is global, a parameter, or a local.
 */
public class VarId
{
	/****************/
	/* VARIABLE KIND */
	/****************/
	public enum Kind {
		GLOBAL,  // in .data section, addressed by label
		PARAM,   // on stack at positive $fp offset
		LOCAL    // on stack at negative $fp offset
	}

	/****************/
	/* DATA MEMBERS */
	/****************/
	public final String name;
	public final int scopeOffset;
	public Kind kind;
	public int fpOffset; // offset from $fp in bytes

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public VarId(String name, int scopeOffset)
	{
		this.name = name;
		this.scopeOffset = scopeOffset;
		this.kind = Kind.GLOBAL;
		this.fpOffset = 0;
	}

	public VarId(String name, int scopeOffset, Kind kind, int fpOffset)
	{
		this.name = name;
		this.scopeOffset = scopeOffset;
		this.kind = kind;
		this.fpOffset = fpOffset;
	}

	/****************************************/
	/* Get the display name for output     */
	/* (just the variable name, not offset) */
	/****************************************/
	public String getDisplayName()
	{
		return name;
	}

	/****************************************/
	/* equals and hashCode for use in Sets */
	/* and Maps during dataflow analysis   */
	/****************************************/
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		VarId varId = (VarId) obj;
		return scopeOffset == varId.scopeOffset && Objects.equals(name, varId.name);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(name, scopeOffset);
	}

	@Override
	public String toString()
	{
		return name + "@" + scopeOffset;
	}
}

