package ir;

import temp.Temp;

public class IrCommandCallFunc extends IrCommand {
    public Temp dst;
    public String funcLabel;
    public Temp objAddr;
    public String className;
    public String methodName;

    public IrCommandCallFunc(Temp dst, String funcLabel) {
        this.dst = dst;
        this.funcLabel = funcLabel;
    }

    public int vtableOffset;

    public IrCommandCallFunc(Temp dst, Temp objAddr, int vtableOffset) {
        this.dst = dst;
        this.funcLabel = null;
        this.objAddr = objAddr;
        this.vtableOffset = vtableOffset;
    }

	@Override
	public java.util.List<temp.Temp> getUsedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (objAddr != null) list.add(objAddr);
		return list;
	}

	@Override
	public java.util.List<temp.Temp> getDefinedTemps() {
		java.util.List<temp.Temp> list = new java.util.ArrayList<>();
		if (dst != null) list.add(dst);
		return list;
	}

	public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
		if (objAddr != null) {
		    gen.emitInstruction("lw", "$v1", "0(" + regMap.get(objAddr) + ")");
		    gen.emitInstruction("lw", "$a3", vtableOffset + "($v1)");
		    gen.emitInstruction("jalr", "$a3");
		} else {
		    gen.emitInstruction("jal", funcLabel);
		}
		if (dst != null) gen.emitInstruction("move", regMap.get(dst), "$v0");
	}
}
