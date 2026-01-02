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
 * This is necessary to distinguish between shadowed variables with the same name
 * in different scopes (e.g., global x vs local x in main).
 * 
 * The offset is the index in the symbol table when the variable was entered,
 * which uniquely identifies the variable declaration.
 */
public class VarId
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public final String name;
	public final int scopeOffset;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public VarId(String name, int scopeOffset)
	{
		this.name = name;
		this.scopeOffset = scopeOffset;
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

