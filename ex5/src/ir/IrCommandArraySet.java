package ir;

import temp.Temp;

public class IrCommandArraySet extends IrCommand {
	public Temp array;
	public Temp index;
	public Temp src;

	public IrCommandArraySet(Temp array, Temp index, Temp src) {
		this.array = array;
		this.index = index;
		this.src = src;
	}

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (array != null)
			list.add(array);
		if (index != null)
			list.add(index);
		if (src != null)
			list.add(src);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		gen.emitInstruction("sll", "$v1", regMap.get(index), "2");
		gen.emitInstruction("addu", "$v1", "$v1", regMap.get(array));
		gen.emitInstruction("sw", regMap.get(src), "4($v1)");
	}
}
