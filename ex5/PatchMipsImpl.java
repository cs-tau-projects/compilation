import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PatchMipsImpl {
    public static void main(String[] args) throws Exception {
        Map<String, String[]> map = new HashMap<>();
        
        // Add mappings here
        map.put("IRcommandConstInt.java", new String[] {
            "gen.emitInstruction(\"li\", regMap.get(t), String.valueOf(value));"
        });
        map.put("IrCommandConstString.java", new String[] {
            "String label = \"str_\" + java.util.UUID.randomUUID().toString().replace(\"-\", \"\");",
            "gen.emitDataString(label, \"\\\"\" + value + \"\\\"\");",
            "gen.emitInstruction(\"la\", regMap.get(dst), label);"
        });
        map.put("IrCommandBinopAddIntegers.java", new String[] {
            "gen.emitInstruction(\"add\", regMap.get(dst), regMap.get(t1), regMap.get(t2));",
            "gen.addSaturation(regMap.get(dst));"
        });
        map.put("IrCommandBinopSubIntegers.java", new String[] {
            "gen.emitInstruction(\"sub\", regMap.get(dst), regMap.get(t1), regMap.get(t2));",
            "gen.addSaturation(regMap.get(dst));"
        });
        map.put("IrCommandBinopMulIntegers.java", new String[] {
            "gen.emitInstruction(\"mul\", regMap.get(dst), regMap.get(t1), regMap.get(t2));",
            "gen.addSaturation(regMap.get(dst));"
        });
        map.put("IrCommandBinopDivIntegers.java", new String[] {
            // Check div by zero in AST runtime check, not here. Here just div.
            "gen.emitInstruction(\"div\", regMap.get(t1), regMap.get(t2));",
            "gen.emitInstruction(\"mflo\", regMap.get(dst));",
            "gen.addSaturation(regMap.get(dst));"
        });
        map.put("IrCommandBinopEqIntegers.java", new String[] {
            "gen.emitInstruction(\"seq\", regMap.get(dst), regMap.get(t1), regMap.get(t2));"
        });
        map.put("IrCommandBinopLtIntegers.java", new String[] {
            "gen.emitInstruction(\"slt\", regMap.get(dst), regMap.get(t1), regMap.get(t2));"
        });
        map.put("IrCommandBinopGtIntegers.java", new String[] {
            "gen.emitInstruction(\"sgt\", regMap.get(dst), regMap.get(t1), regMap.get(t2));"
        });
        map.put("IrCommandPrintInt.java", new String[] {
            "gen.emitInstruction(\"move\", \"$a0\", regMap.get(t));",
            "gen.emitInstruction(\"li\", \"$v0\", \"1\");",
            "gen.emitInstruction(\"syscall\");",
            "gen.emitInstruction(\"la\", \"$a0\", \"msg_space\");",
            "gen.emitInstruction(\"li\", \"$v0\", \"4\");",
            "gen.emitInstruction(\"syscall\");"
        });
        map.put("IrCommandPrintString.java", new String[] {
            "gen.emitInstruction(\"move\", \"$a0\", regMap.get(t));",
            "gen.emitInstruction(\"li\", \"$v0\", \"4\");",
            "gen.emitInstruction(\"syscall\");"
        });
        map.put("IrCommandLabel.java", new String[] {
            "gen.emitLabel(labelName);"
        });
        map.put("IrCommandJumpLabel.java", new String[] {
            "gen.emitInstruction(\"j\", labelName);"
        });
        map.put("IrCommandJumpIfEqToZero.java", new String[] {
            "gen.emitInstruction(\"beq\", regMap.get(t), \"$zero\", labelName);"
        });
        map.put("IrCommandExit.java", new String[] {
            "gen.emitInstruction(\"li\", \"$v0\", \"10\");",
            "gen.emitInstruction(\"syscall\");"
        });
        map.put("IrCommandLoad.java", new String[] {
            "if (this.isGlobal) {",
            "    gen.emitInstruction(\"lw\", regMap.get(dst), \"global_\" + varId.name);",
            "} else {",
            "    gen.emitInstruction(\"lw\", regMap.get(dst), gen.getLocalOffset(varId.scopeOffset) + \"($fp)\");",
            "}"
        });
        map.put("IrCommandStore.java", new String[] {
            "if (this.isGlobal) {",
            "    gen.emitInstruction(\"sw\", regMap.get(src), \"global_\" + varId.name);",
            "} else {",
            "    gen.emitInstruction(\"sw\", regMap.get(src), gen.getLocalOffset(varId.scopeOffset) + \"($fp)\");",
            "}"
        });
        map.put("IrCommandAllocate.java", new String[] {
            "if (this.isGlobal) {",
            "    gen.emitGlobalWord(\"global_\" + varId.name, 0);",
            "} else {",
            "    gen.allocateLocal(varId.scopeOffset);",
            "}"
        });
        
        Files.list(Paths.get("d:/compilation/compilation/ex5/src/ir"))
            .filter(p -> p.toString().endsWith(".java") && map.containsKey(p.getFileName().toString()))
            .forEach(p -> patch(p, map.get(p.getFileName().toString())));
    }
    
    static void patch(Path p, String[] body) {
        try {
            String content = new String(Files.readAllBytes(p));
            int start = content.indexOf("public void mipsMe");
            if (start == -1) return;
            start = content.indexOf("{", start);
            int end = content.indexOf("}", start);
            
            String newBody = "{\n";
            for (String b : body) newBody += "\t\t" + b + "\n";
            newBody += "\t}";
            
            content = content.substring(0, start) + newBody + content.substring(end + 1);
            Files.write(p, content.getBytes());
            System.out.println("Implemented " + p.getFileName());
        } catch(Exception e) {}
    }
}
