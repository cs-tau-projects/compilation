package ir;

import temp.Temp;

/**
 * Allocate a new object on the heap.
 * Allocates objectSize bytes via sbrk, stores vtable pointer at offset 0.
 */
public class IrCommandNewObject extends IrCommand {
    public Temp dst;
    public String className;
    public int objectSize;

    public IrCommandNewObject(Temp dst, String className, int objectSize) {
        this.dst = dst;
        this.className = className;
        this.objectSize = objectSize;
    }

    // Legacy constructor (will compute size at mipsMe time)
    public IrCommandNewObject(Temp dst, String className) {
        this.dst = dst;
        this.className = className;
        this.objectSize = -1;
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
		int size = objectSize;
		if (size <= 0) {
			// Compute from type system as fallback
			types.TypeClass tc = (types.TypeClass) symboltable.SymbolTable.getInstance().find(className);
			if (tc != null) {
				size = ClassLayout.getObjectSize(tc);
			}
			if (size <= 0) size = 8; // minimum: vtable + 1 field
		}

		gen.emitInstruction("li", "$a0", String.valueOf(size));
		gen.emitInstruction("li", "$v0", "9");
		gen.emitInstruction("syscall");
		gen.emitInstruction("la", "$s0", "vtable_" + className);
		gen.emitInstruction("sw", "$s0", "0($v0)");
		gen.emitInstruction("move", regMap.get(dst), "$v0");
	}
}
