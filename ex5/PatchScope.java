import java.io.*;
import java.nio.file.*;

public class PatchScope {
    public static void main(String[] args) throws Exception {
        patchAllocate();
        patchLoad();
        patchStore();
    }
    
    static void patchAllocate() throws Exception {
        Path p = Paths.get("d:/compilation/compilation/ex5/src/ir/IrCommandAllocate.java");
        String c = new String(Files.readAllBytes(p)).replace("\r\n", "\n");
        c = c.replace(
            "public IrCommandAllocate(String varName, int scopeOffset)\n" +
            "\t{\n" +
            "\t\tthis.varId = new VarId(varName, scopeOffset);\n" +
            "\t}",
            "public boolean isGlobal;\n" +
            "\tpublic IrCommandAllocate(String varName, int scopeOffset, boolean isGlobal)\n" +
            "\t{\n" +
            "\t\tthis.varId = new VarId(varName, scopeOffset);\n" +
            "\t\tthis.isGlobal = isGlobal;\n" +
            "\t}\n\n" +
            "\tpublic IrCommandAllocate(String varName, int scopeOffset)\n" +
            "\t{\n" +
            "\t\tthis.varId = new VarId(varName, scopeOffset);\n" +
            "\t}"
        );
        Files.write(p, c.getBytes());
    }
    
    static void patchLoad() throws Exception {
        Path p = Paths.get("d:/compilation/compilation/ex5/src/ir/IrCommandLoad.java");
        String c = new String(Files.readAllBytes(p)).replace("\r\n", "\n");
        c = c.replace(
            "public IrCommandLoad(Temp dst, String varName, int scopeOffset)\n" +
            "\t{\n" +
            "\t\tthis.dst   = dst;\n" +
            "\t\tthis.varId = new VarId(varName, scopeOffset);\n" +
            "\t}",
            "public boolean isGlobal;\n" +
            "\tpublic IrCommandLoad(Temp dst, String varName, int scopeOffset, boolean isGlobal)\n" +
            "\t{\n" +
            "\t\tthis.dst   = dst;\n" +
            "\t\tthis.varId = new VarId(varName, scopeOffset);\n" +
            "\t\tthis.isGlobal = isGlobal;\n" +
            "\t}\n\n" +
            "\tpublic IrCommandLoad(Temp dst, String varName, int scopeOffset)\n" +
            "\t{\n" +
            "\t\tthis.dst   = dst;\n" +
            "\t\tthis.varId = new VarId(varName, scopeOffset);\n" +
            "\t}"
        );
        Files.write(p, c.getBytes());
    }
    
    static void patchStore() throws Exception {
        Path p = Paths.get("d:/compilation/compilation/ex5/src/ir/IrCommandStore.java");
        String c = new String(Files.readAllBytes(p)).replace("\r\n", "\n");
        c = c.replace(
            "public IrCommandStore(String varName, int scopeOffset, Temp src)\n" +
            "\t{\n" +
            "\t\tthis.src   = src;\n" +
            "\t\tthis.varId = new VarId(varName, scopeOffset);\n" +
            "\t}",
            "public boolean isGlobal;\n" +
            "\tpublic IrCommandStore(String varName, int scopeOffset, Temp src, boolean isGlobal)\n" +
            "\t{\n" +
            "\t\tthis.src   = src;\n" +
            "\t\tthis.varId = new VarId(varName, scopeOffset);\n" +
            "\t\tthis.isGlobal = isGlobal;\n" +
            "\t}\n\n" +
            "\tpublic IrCommandStore(String varName, int scopeOffset, Temp src)\n" +
            "\t{\n" +
            "\t\tthis.src   = src;\n" +
            "\t\tthis.varId = new VarId(varName, scopeOffset);\n" +
            "\t}"
        );
        Files.write(p, c.getBytes());
    }
}
