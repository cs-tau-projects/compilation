package ir;

public class IrCommandFuncPrologue extends IrCommand {
    public IrCommandFuncPrologue() {}

    @Override
    public java.util.List<temp.Temp> getUsedTemps() {
        return new java.util.ArrayList<>();
    }

    @Override
    public java.util.List<temp.Temp> getDefinedTemps() {
        return new java.util.ArrayList<>();
    }

    @Override
    public void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {
        gen.resetLocals(); 
        
        // Count locals for this function to pre-allocate stack space
        int numLocals = 0;
        java.util.List<IrCommand> commands = Ir.getInstance().getCommands();
        int myIndex = commands.indexOf(this);
        if (myIndex != -1) {
            for (int i = myIndex + 1; i < commands.size(); i++) {
                IrCommand cmd = commands.get(i);
                if (cmd instanceof IrCommandFuncPrologue) break;
                if (cmd instanceof IrCommandAllocate) {
                    if (!((IrCommandAllocate) cmd).isGlobal) {
                        numLocals++;
                    }
                }
            }
        }

        // save FP
        gen.emitInstruction("subu", "$sp", "$sp", "4");
        gen.emitInstruction("sw", "$fp", "0($sp)");
        // save RA
        gen.emitInstruction("subu", "$sp", "$sp", "4");
        gen.emitInstruction("sw", "$ra", "0($sp)");
        // save S-registers
        for (int i = 0; i < 8; i++) {
            gen.emitInstruction("sw", "$s" + i, (i*4) + "($sp)");
        }
        // save T-registers
        for (int i = 0; i < 10; i++) {
            gen.emitInstruction("sw", "$t" + i, (i*4) + "($sp)");
        }
        // set FP
        gen.emitInstruction("move", "$fp", "$sp");

        // allocate locals
        if (numLocals > 0) {
            gen.emitInstruction("subu", "$sp", "$sp", String.valueOf(numLocals * 4));
        }
    }
}
