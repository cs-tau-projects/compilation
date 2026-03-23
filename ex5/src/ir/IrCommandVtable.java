package ir;

import java.util.List;
import temp.Temp;

public class IrCommandVtable extends IrCommand {
    public String className;
    public List<String> methods;

    public IrCommandVtable(String className, List<String> methods) {
        this.className = className;
        this.methods = methods;
    }

    @Override
    public java.util.List<Temp> getUsedTemps() {
        return new java.util.ArrayList<>();
    }

    @Override
    public java.util.List<Temp> getDefinedTemps() {
        return new java.util.ArrayList<>();
    }

    @Override
    public void mipsMe(mips.MipsGenerator gen, java.util.Map<Temp, String> regMap) {
        gen.emitVtable(className, methods);
    }
}
