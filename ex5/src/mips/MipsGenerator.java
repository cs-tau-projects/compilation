package mips;

import java.io.*;
import java.util.*;
import temp.Temp;

public class MipsGenerator {
    private List<String> textSection = new ArrayList<>();
    private List<String> dataSection = new ArrayList<>();
    
    // For local variable stack offsets
    private Map<Integer, Integer> localOffsets = new HashMap<>();
    private int currentFpOffset = -4; // after $ra, $fp? Wait, standard is to allocate below
    
    public void resetLocals() {
        localOffsets.clear();
        currentFpOffset = 0; // The prologue has already allocated locals according to some scheme, or we just allocate downwards
    }
    
    public void allocateLocal(int scopeOffset) {
        currentFpOffset -= 4;
        localOffsets.put(scopeOffset, currentFpOffset);
    }
    
    public void allocateParam(int scopeOffset, int stackOffset) {
        localOffsets.put(scopeOffset, stackOffset);
    }
    
    public int getLocalOffset(int scopeOffset) {
        return localOffsets.getOrDefault(scopeOffset, 0);
    }

    public MipsGenerator() {
        dataSection.add(".data");
        textSection.add(".text");
        addRuntimeErrorHandlers();
    }
    
    private void addRuntimeErrorHandlers() {
        dataSection.add("msg_div_zero: .asciiz \"Illegal Division By Zero\"");
        dataSection.add("msg_invalid_ptr: .asciiz \"Invalid Pointer Dereference\"");
        dataSection.add("msg_access_violation: .asciiz \"Access Violation\"");
        dataSection.add("msg_space: .asciiz \" \"");
        
        textSection.add("Label_division_by_zero:");
        textSection.add("\tla $a0, msg_div_zero");
        textSection.add("\tli $v0, 4");
        textSection.add("\tsyscall");
        textSection.add("\tli $v0, 10");
        textSection.add("\tsyscall");
        
        textSection.add("Label_invalid_ptr_deref:");
        textSection.add("\tla $a0, msg_invalid_ptr");
        textSection.add("\tli $v0, 4");
        textSection.add("\tsyscall");
        textSection.add("\tli $v0, 10");
        textSection.add("\tsyscall");
        
        textSection.add("Label_access_violation:");
        textSection.add("\tla $a0, msg_access_violation");
        textSection.add("\tli $v0, 4");
        textSection.add("\tsyscall");
        textSection.add("\tli $v0, 10");
        textSection.add("\tsyscall");
        textSection.add("");
    }
    
    public void emitLabel(String label) {
        textSection.add(label + ":");
    }
    
    public void emitInstruction(String op, String... args) {
        if (args.length > 0) {
            textSection.add("\t" + op + " " + String.join(", ", args));
        } else {
            textSection.add("\t" + op);
        }
    }
    
    public void emitDataString(String label, String value) {
        dataSection.add(label + ": .asciiz " + value);
    }
    
    public void emitGlobalWord(String label, int value) {
        dataSection.add(label + ": .word " + value); // reserve space for global variables
    }

    public void emitVtable(String className, List<String> methodLabels) {
        dataSection.add("vtable_" + className + ":");
        for (String m : methodLabels) {
            dataSection.add("\t.word " + m);
        }
    }

    public void addSaturation(String reg) {
        // Clamp to max
        emitInstruction("li", "$s0", "32767");
        emitInstruction("slt", "$s1", "$s0", reg);
        emitInstruction("movn", reg, "$s0", "$s1");
        
        // Clamp to min
        emitInstruction("li", "$s0", "-32768");
        emitInstruction("slt", "$s1", reg, "$s0");
        emitInstruction("movn", reg, "$s0", "$s1");
    }

    public void writeToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (String s : dataSection) writer.println(s);
            writer.println();
            for (String s : textSection) writer.println(s);
        }
    }
}
