package ir;

public class IrCommandPopParams extends IrCommand {
    public int numParams;

    public IrCommandPopParams(int numParams) {
        this.numParams = numParams;
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
		gen.emitInstruction("addiu", "$sp", "$sp", Integer.toString(numParams * 4));
	}
}
