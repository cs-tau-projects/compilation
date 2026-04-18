package ir;

public class IrCommandLabel extends IrCommand
{
	public String labelName;

	public IrCommandLabel(String labelName)
	{
		this.labelName = labelName;
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
		gen.emitLabel(labelName);
	}
}
