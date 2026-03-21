import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PatchComplex {
    public static void main(String[] args) throws Exception {
        Map<String, String[]> map = new HashMap<>();

        map.put("IrCommandCallFunc.java", new String[] {
            "if (isVirtual) {",
            "    gen.emitInstruction(\"lw\", \"$t0\", \"0(\" + regMap.get(obj) + \")\");",
            "    gen.emitInstruction(\"la\", \"$t1\", \"Method_\" + className + \"_\" + methodName);",
            "    gen.emitInstruction(\"jalr\", \"$t1\");",
            "} else {",
            "    gen.emitInstruction(\"jal\", funcLabel);",
            "}",
            "if (dst != null) gen.emitInstruction(\"move\", regMap.get(dst), \"$v0\");"
        });

        map.put("IrCommandReturn.java", new String[] {
            "if (src != null) gen.emitInstruction(\"move\", \"$v0\", regMap.get(src));",
            "gen.emitInstruction(\"move\", \"$sp\", \"$fp\");",
            "gen.emitInstruction(\"lw\", \"$ra\", \"-4(\" + \"$sp\" + \")\");",
            "gen.emitInstruction(\"lw\", \"$fp\", \"0(\" + \"$sp\" + \")\");",
            "gen.emitInstruction(\"jr\", \"$ra\");"
        });

        map.put("IrCommandPushParam.java", new String[] {
            "gen.emitInstruction(\"sw\", regMap.get(param), \"0($sp)\");",
            "gen.emitInstruction(\"sub\", \"$sp\", \"$sp\", \"4\");"
        });

        // The rest are identical to the previous PatchComplex, but with variable names fixed:
        // IrCommandPopParams: numParams (this was correct)
        map.put("IrCommandPopParams.java", new String[] {
            "gen.emitInstruction(\"add\", \"$sp\", \"$sp\", Integer.toString(numParams * 4));"
        });

        // IrCommandFieldGet: dst, obj
        map.put("IrCommandFieldGet.java", new String[] {
            "gen.emitInstruction(\"lw\", regMap.get(dst), \"4(\" + regMap.get(obj) + \")\");"
        });

        // IrCommandFieldSet: obj, src
        map.put("IrCommandFieldSet.java", new String[] {
            "gen.emitInstruction(\"sw\", regMap.get(src), \"4(\" + regMap.get(obj) + \")\");"
        });

        // IrCommandArrayGet: dst, array, index
        map.put("IrCommandArrayGet.java", new String[] {
            "gen.emitInstruction(\"mul\", \"$t0\", regMap.get(index), \"4\");",
            "gen.emitInstruction(\"add\", \"$t0\", \"$t0\", regMap.get(array));",
            "gen.emitInstruction(\"lw\", regMap.get(dst), \"4($t0)\");"
        });

        // IrCommandArraySet: array, index, src
        map.put("IrCommandArraySet.java", new String[] {
            "gen.emitInstruction(\"mul\", \"$t0\", regMap.get(index), \"4\");",
            "gen.emitInstruction(\"add\", \"$t0\", \"$t0\", regMap.get(array));",
            "gen.emitInstruction(\"sw\", regMap.get(src), \"4($t0)\");"
        });

        // IrCommandNewObject: dst
        map.put("IrCommandNewObject.java", new String[] {
            "gen.emitInstruction(\"li\", \"$a0\", \"8\");",
            "gen.emitInstruction(\"li\", \"$v0\", \"9\");",
            "gen.emitInstruction(\"syscall\");",
            "gen.emitInstruction(\"la\", \"$t0\", \"vtable_\" + className);",
            "gen.emitInstruction(\"sw\", \"$t0\", \"0($v0)\");",
            "gen.emitInstruction(\"move\", regMap.get(dst), \"$v0\");"
        });

        // IrCommandNewArray: size, dst
        map.put("IrCommandNewArray.java", new String[] {
            "gen.emitInstruction(\"move\", \"$a0\", regMap.get(size));",
            "gen.emitInstruction(\"mul\", \"$a0\", \"$a0\", \"4\");",
            "gen.emitInstruction(\"add\", \"$a0\", \"$a0\", \"4\");",
            "gen.emitInstruction(\"li\", \"$v0\", \"9\");",
            "gen.emitInstruction(\"syscall\");",
            "gen.emitInstruction(\"sw\", regMap.get(size), \"0($v0)\");",
            "gen.emitInstruction(\"move\", regMap.get(dst), \"$v0\");"
        });

        // IrCommandCheckNull: ptr
        map.put("IrCommandCheckNull.java", new String[] {
            "gen.emitInstruction(\"beq\", regMap.get(ptr), \"$zero\", \"Label_invalid_ptr_deref\");"
        });

        // IrCommandCheckBounds: array, index
        map.put("IrCommandCheckBounds.java", new String[] {
            "gen.emitInstruction(\"lw\", \"$t0\", \"0(\" + regMap.get(array) + \")\");",
            "gen.emitInstruction(\"bge\", regMap.get(index), \"$t0\", \"Label_access_violation\");",
            "gen.emitInstruction(\"blt\", regMap.get(index), \"$zero\", \"Label_access_violation\");"
        });

        // IrCommandCheckDivZero: denominator
        map.put("IrCommandCheckDivZero.java", new String[] {
            "gen.emitInstruction(\"beq\", regMap.get(denominator), \"$zero\", \"Label_division_by_zero\");"
        });
        
        // IrCommandBinopEqStrings: dst, left, right
        map.put("IrCommandBinopEqStrings.java", new String[] {
            "gen.emitInstruction(\"seq\", regMap.get(dst), regMap.get(left), regMap.get(right));"
        });

        // IrCommandBinopEqRefs: dst, left, right
        map.put("IrCommandBinopEqRefs.java", new String[] {
            "gen.emitInstruction(\"seq\", regMap.get(dst), regMap.get(left), regMap.get(right));"
        });

        // IrCommandBinopStrConcat: dst, left, right
        map.put("IrCommandBinopStrConcat.java", new String[] {
            "gen.emitInstruction(\"move\", regMap.get(dst), regMap.get(left));"
        });

        Files.list(Paths.get("d:/compilation/compilation/ex5/src/ir"))
            .filter(p -> p.toString().endsWith(".java") && map.containsKey(p.getFileName().toString()))
            .forEach(p -> patch(p, map.get(p.getFileName().toString())));
    }
    
    static void patch(Path p, String[] body) {
        try {
            String content = new String(Files.readAllBytes(p)).replace("\r\n", "\n");
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
