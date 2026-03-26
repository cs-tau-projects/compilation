package ir;

import temp.Temp;

/**
 * IR command to load an object field.
 * dst := base[byteOffset]
 */
public class IrCommandFieldGet extends IrCommand {
    public Temp dst;
    public Temp obj;
    public String className;
    public String fieldName;
    public int byteOffset;

    // New constructor with pre-computed byte offset
    public IrCommandFieldGet(Temp dst, Temp obj, int byteOffset) {
        this.dst = dst;
        this.obj = obj;
        this.byteOffset = byteOffset;
    }

    // Legacy constructor with className + fieldName (resolves offset at MIPS time)
    public IrCommandFieldGet(Temp dst, Temp obj, String className, String fieldName) {
        this.dst = dst;
        this.obj = obj;
        this.className = className;
        this.fieldName = fieldName;
        this.byteOffset = -1; // computed at mipsMe time
    }

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (obj != null) list.add(obj);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (dst != null) list.add(dst);
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		int offset = byteOffset;
		if (offset < 0 && className != null && fieldName != null) {
			// Resolve from type system as fallback
			types.TypeClass tc = (types.TypeClass) symboltable.SymbolTable.getInstance().find(className);
			if (tc != null) {
				offset = ClassLayout.getFieldOffset(tc, fieldName);
			}
		}
		if (offset < 0) offset = 4; // fallback to first field

		// Null pointer check
		String okLabel = IrCommand.getFreshLabel("NullOk");
		gen.emitInstruction("bne", regMap.get(obj), "$zero", okLabel);
		gen.emitInstruction("j", "Label_invalid_ptr_deref");
		gen.emitLabel(okLabel);

		gen.emitInstruction("lw", regMap.get(dst), offset + "(" + regMap.get(obj) + ")");
	}
}
