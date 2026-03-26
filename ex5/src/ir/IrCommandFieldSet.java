package ir;

import temp.Temp;

/**
 * IR command to store a value into an object field.
 * base[byteOffset] := src
 */
public class IrCommandFieldSet extends IrCommand {
    public Temp obj;
    public String className;
    public String fieldName;
    public Temp src;
    public int byteOffset;

    // New constructor with pre-computed byte offset
    public IrCommandFieldSet(Temp obj, int byteOffset, Temp src) {
        this.obj = obj;
        this.byteOffset = byteOffset;
        this.src = src;
    }

    // Legacy constructor with className + fieldName
    public IrCommandFieldSet(Temp obj, String className, String fieldName, Temp src) {
        this.obj = obj;
        this.className = className;
        this.fieldName = fieldName;
        this.src = src;
        this.byteOffset = -1; // computed at mipsMe time
    }

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (obj != null) list.add(obj);
		if (src != null) list.add(src);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		int offset = byteOffset;
		if (offset < 0 && className != null && fieldName != null) {
			types.TypeClass tc = (types.TypeClass) symboltable.SymbolTable.getInstance().find(className);
			if (tc != null) {
				offset = ClassLayout.getFieldOffset(tc, fieldName);
			}
		}
		if (offset < 0) offset = 4; // fallback

		// Null pointer check
		String okLabel = IrCommand.getFreshLabel("NullOk");
		gen.emitInstruction("bne", regMap.get(obj), "$zero", okLabel);
		gen.emitInstruction("j", "Label_invalid_ptr_deref");
		gen.emitLabel(okLabel);

		gen.emitInstruction("sw", regMap.get(src), offset + "(" + regMap.get(obj) + ")");
	}
}
