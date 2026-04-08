package mips;

import java.io.*;
import java.util.*;

public class MipsGenerator {
    private List<String> textSection = new ArrayList<>();
    private List<String> dataSection = new ArrayList<>();

    // For local variable stack offsets
    private Map<Integer, Integer> localOffsets = new HashMap<>();
    private int currentFpOffset = -4; // after $ra, $fp? Wait, standard is to allocate below

    public void resetLocals() {
        currentFpOffset = 0; // The prologue has already allocated locals according to some scheme, or we
                             // just allocate downwards
    }

    public void allocateLocal(int scopeOffset) {
        currentFpOffset -= 4;
        localOffsets.put(scopeOffset, currentFpOffset);
    }

    public void allocateParam(int scopeOffset, int frameOffset) {
        localOffsets.put(scopeOffset, frameOffset);
    }

    public int getLocalOffset(int scopeOffset) {
        return localOffsets.getOrDefault(scopeOffset, 0);
    }

    public MipsGenerator() {
        dataSection.add(".data");
        textSection.add(".text");
        addRuntimeErrorHandlers();
        addRuntimeStringHandlers();
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

    private void addRuntimeStringHandlers() {
        // --- String Equality (Runtime_StrEq) ---
        textSection.add("Runtime_StrEq:");
        textSection.add("\tmove $s0, $a0");
        textSection.add("\tmove $s1, $a1");
        textSection.add("StrEq_Loop:");
        textSection.add("\tlbu $s2, 0($s0)");
        textSection.add("\tlbu $s3, 0($s1)");
        textSection.add("\tbne $s2, $s3, StrEq_False");
        textSection.add("\tbeq $s2, $zero, StrEq_True");
        textSection.add("\taddiu $s0, $s0, 1");
        textSection.add("\taddiu $s1, $s1, 1");
        textSection.add("\tj StrEq_Loop");
        textSection.add("\tli $v0, 0");
        textSection.add("\tjr $ra");
        textSection.add("StrEq_True:");
        textSection.add("\tli $v0, 1");
        textSection.add("\tjr $ra");

        // --- String Concatenation (Runtime_StrConcat) ---
        // Find length of string 1
        textSection.add("\tmove $s0, $a0");
        textSection.add("\tli $s4, 0");
        textSection.add("StrConcat_Len1:");
        textSection.add("\tlbu $s2, 0($s0)");
        textSection.add("\tbeq $s2, $zero, StrConcat_Len1_End");
        textSection.add("\taddiu $s0, $s0, 1");
        textSection.add("\taddiu $s4, $s4, 1");
        textSection.add("\tj StrConcat_Len1");
        textSection.add("StrConcat_Len1_End:");

        // Find length of string 2
        textSection.add("\tmove $s1, $a1");
        textSection.add("\tli $s5, 0");
        textSection.add("StrConcat_Len2:");
        textSection.add("\tlbu $s3, 0($s1)");
        textSection.add("\tbeq $s3, $zero, StrConcat_Len2_End");
        textSection.add("\taddiu $s1, $s1, 1");
        textSection.add("\taddiu $s5, $s5, 1");
        textSection.add("\tj StrConcat_Len2");
        textSection.add("StrConcat_Len2_End:");

        // Total Length + 1 ($s6)
        textSection.add("\tadd $s6, $s4, $s5");
        textSection.add("\taddiu $s6, $s6, 1");
        // Align to 4 bytes
        textSection.add("\taddiu $s6, $s6, 3");
        textSection.add("\tsrl $s6, $s6, 2");
        textSection.add("\tsll $s6, $s6, 2");

        // Save $a0 and $a1 before syscall overrides them
        textSection.add("\tmove $s8, $a0");
        textSection.add("\tmove $s9, $a1");

        // Allocate memory (sbrk, syscall 9)
        textSection.add("\tmove $a0, $s6");
        textSection.add("\tli $v0, 9");
        textSection.add("\tsyscall");
        textSection.add("\tmove $s7, $v0"); // output buffer

        // Copy string 1
        textSection.add("\tmove $s0, $s8"); // Restore string 1 base
        textSection.add("StrConcat_Copy1:");
        textSection.add("\tlbu $s2, 0($s0)");
        textSection.add("\tbeq $s2, $zero, StrConcat_Copy1_End");
        textSection.add("\tsb $s2, 0($s7)");
        textSection.add("\taddiu $s0, $s0, 1");
        textSection.add("\taddiu $s7, $s7, 1");
        textSection.add("\tj StrConcat_Copy1");
        textSection.add("StrConcat_Copy1_End:");

        // Copy string 2
        textSection.add("\tmove $s1, $s9"); // Restore string 2 base
        textSection.add("StrConcat_Copy2:");
        textSection.add("\tlbu $s3, 0($s1)");
        textSection.add("\tbeq $s3, $zero, StrConcat_Copy2_End");
        textSection.add("\tsb $s3, 0($s7)");
        textSection.add("\taddiu $s1, $s1, 1");
        textSection.add("\taddiu $s7, $s7, 1");
        textSection.add("\tj StrConcat_Copy2");
        textSection.add("StrConcat_Copy2_End:");

        // Null terminator and return (result base is already preserved in $v0)
        textSection.add("\tsb $zero, 0($s7)");
        textSection.add("\tjr $ra");
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
        dataSection.add("\t.align 2");
        dataSection.add(label + ": .word " + value); // reserve space for global variables
    }

    public void emitVtable(String className, List<String> methodLabels) {
        dataSection.add("\t.align 2");
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
            for (String s : dataSection)
                writer.println(s);
            writer.println();
            for (String s : textSection)
                writer.println(s);
        }
    }
}
