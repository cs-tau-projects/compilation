import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PatchIr {
    public static void main(String[] args) throws Exception {
        Files.list(Paths.get("d:/compilation/compilation/ex5/src/ir"))
            .filter(p -> p.toString().endsWith(".java") && !p.getFileName().toString().equals("IrCommand.java") && !p.getFileName().toString().equals("Ir.java") && !p.getFileName().toString().equals("VarId.java"))
            .forEach(PatchIr::patchFile);
    }
    
    static void patchFile(Path p) {
        try {
            String content = new String(Files.readAllBytes(p));
            if (content.contains("getUsedTemps")) {
                System.out.println("Skipped " + p + " (already patched)");
                return;
            }
            
            // extract Temp fields
            List<String> temps = new ArrayList<>();
            Scanner s = new Scanner(content);
            while(s.hasNextLine()) {
                String line = s.nextLine().trim();
                if (line.startsWith("public Temp ") || line.startsWith("public temp.Temp ")) {
                    String[] parts = line.split("\\s+");
                    String name = parts[2].replace(";", "");
                    temps.add(name);
                }
            }
            s.close();
            
            List<String> uses = new ArrayList<>();
            List<String> defs = new ArrayList<>();
            
            for (String t : temps) {
                if (t.equals("dst")) {
                    defs.add(t);
                } else if (p.getFileName().toString().equals("IRcommandConstInt.java") && t.equals("t")) {
                    defs.add(t);
                } else {
                    uses.add(t);
                }
            }
            
            String usedStr = String.join(", ", uses);
            String defStr = String.join(", ", defs);
            
            // Generate the code to inject
            String methods = "\n\t@Override\n\tpublic java.util.List<temp.Temp> getUsedTemps() {\n";
            methods += "\t\tjava.util.List<temp.Temp> list = new java.util.ArrayList<>();\n";
            for (String u : uses) {
                methods += "\t\tif (" + u + " != null) list.add(" + u + ");\n";
            }
            methods += "\t\treturn list;\n\t}\n";
            
            methods += "\n\t@Override\n\tpublic java.util.List<temp.Temp> getDefinedTemps() {\n";
            methods += "\t\tjava.util.List<temp.Temp> list = new java.util.ArrayList<>();\n";
            for (String d : defs) {
                methods += "\t\tif (" + d + " != null) list.add(" + d + ");\n";
            }
            methods += "\t\treturn list;\n\t}\n";
            
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
