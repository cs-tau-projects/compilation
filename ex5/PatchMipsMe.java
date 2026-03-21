import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PatchMipsMe {
    public static void main(String[] args) throws Exception {
        Files.list(Paths.get("d:/compilation/compilation/ex5/src/ir"))
            .filter(p -> p.toString().endsWith(".java") && !p.getFileName().toString().equals("IrCommand.java") && !p.getFileName().toString().equals("Ir.java") && !p.getFileName().toString().equals("VarId.java"))
            .forEach(PatchMipsMe::patchFile);
    }
    
    static void patchFile(Path p) {
        try {
            String content = new String(Files.readAllBytes(p));
            if (content.contains("mipsMe")) {
                System.out.println("Skipped " + p + " (already patched)");
                return;
            }
            
            String methods = "\n\tpublic void mipsMe(mips.MipsGenerator gen, java.util.Map<temp.Temp, String> regMap) {\n\t\t// TODO: implement\n\t}\n";
            
            // Insert before last '}'
            int lastBrace = content.lastIndexOf('}');
            if (lastBrace != -1) {
                content = content.substring(0, lastBrace) + methods + "}\n";
                Files.write(p, content.getBytes());
                System.out.println("Patched " + p);
            }
        } catch(Exception e) { e.printStackTrace(); }
    }
}
