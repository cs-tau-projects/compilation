# Exercise 5 — Code Generation: Implementation Plan

## Overview

Exercise 5 is the **final phase** of the L compiler: translating a semantically-valid L program into **MIPS assembly** that runs on the SPIM 8.0 simulator.

The codebase is currently a copy of ex4, which already provides:
- Lexing, parsing, AST construction
- Semantic analysis (type checking)
- IR generation ([irMe()](file:///c:/Users/admin/projects/compilation/ex5/src/ast/AstDecFunc.java#140-149) on AST nodes producing IR commands)
- CFG construction and uninitialized-variable dataflow analysis

**What must be added for ex5** falls into three major parts:

---

## Part 1 — Extend IR Generation (AST → IR)

The existing [irMe()](file:///c:/Users/admin/projects/compilation/ex5/src/ast/AstDecFunc.java#140-149) in each AST node produces a *partial* IR — mainly integer binops, loads, stores, labels, and jumps. It must be **extended** to cover the *full* L language.

### Step 1.1 — New IR Command Classes

Create new [IrCommand](file:///c:/Users/admin/projects/compilation/ex5/src/ir/IrCommand.java#14-25) subclasses in `src/ir/` for constructs not yet covered:

| New IR Command | Purpose |
|---|---|
| `IrCommandCallFunc` | Call a function (push args, jump-and-link, retrieve return value) |
| `IrCommandReturn` | Return from a function (place return value, restore frame, jump back) |
| `IrCommandPushParam` | Push a parameter before a function call |
| `IrCommandPopParams` | Pop parameters after a function call returns |
| `IrCommandPrintString` | Print a string via MIPS syscall 4 |
| `IrCommandConstString` | Assign a string constant to a temp |
| `IrCommandNewObject` | Allocate a class object on the heap (`malloc`) |
| `IrCommandNewArray` | Allocate an array on the heap (`malloc`) |
| `IrCommandFieldGet` | Load a field from a class object (base address + offset) |
| `IrCommandFieldSet` | Store a value into a class field |
| `IrCommandArrayGet` | Load an element from an array (base + index * 4) |
| `IrCommandArraySet` | Store a value into an array element |
| `IrCommandBinopStrConcat` | Concatenate two strings (allocate + copy) |
| `IrCommandBinopEqStrings` | Compare two strings for content equality |
| `IrCommandBinopEqRefs` | Compare two reference addresses (arrays / objects) |
| `IrCommandMove` | Copy one temp into another |
| `IrCommandRuntimeCheck` | Emit a runtime check (div-by-zero / null-deref / out-of-bounds) |

### Step 1.2 — Update [irMe()](file:///c:/Users/admin/projects/compilation/ex5/src/ast/AstDecFunc.java#140-149) in AST Nodes

Modify the following AST nodes (or add [irMe()](file:///c:/Users/admin/projects/compilation/ex5/src/ast/AstDecFunc.java#140-149) where missing):

| AST Node | Changes |
|---|---|
| [AstDecFunc](file:///c:/Users/admin/projects/compilation/ex5/src/ast/AstDecFunc.java#8-150) | Emit function prologue/epilogue: label, frame setup, parameters load, body, return |
| `AstDecClass` | Emit virtual-method table (vtable) initialization; handle field offsets |
| `AstDecVar` | Handle global variable initialization (including non-constant expressions) |
| `AstDecList` | Initialize globals *before* jumping to [main](file:///c:/Users/admin/projects/compilation/ex5/src/Main.java#11-105) |
| [AstExpBinop](file:///c:/Users/admin/projects/compilation/ex5/src/ast/AstExpBinop.java#7-276) | Handle string concat (`+` on strings), string equality (`=` on strings), reference equality (`=` on classes/arrays) — currently only integer binops are emitted |
| `AstExpCall` | Emit parameter evaluation (left-to-right), push params, call, pop params, get return value |
| `AstExpNew` | Allocate object on heap, initialize fields, set vtable pointer |
| `AstExpNil` | Load 0 into a temp (nil ≡ address 0) |
| `AstExpString` | Store string constant in `.data` section, load address into temp |
| `AstExpVar` | Already partial; extend for class field access and array subscript |
| `AstVarField` | Emit null-check + field offset load |
| `AstVarSubscript` | Emit null-check + bounds-check + element load |
| `AstStmtAssign` | Handle field/subscript assignment targets (store through pointer) |
| `AstStmtAssignNew` | Handle `new` for arrays |
| `AstStmtReturn` | Emit return IR (place value in return temp, epilogue) |
| `AstStmtIf` / `AstStmtIfElse` | Already has jump-if-zero; verify correctness |
| `AstStmtWhile` | Already has loop structure; verify correctness |
| `AstStmtCallExp` | Statement-level function call (discard return value) |

### Step 1.3 — Runtime Checks in IR

Insert IR commands for the three runtime checks wherever needed:

1. **Division by zero** — before every `IrCommandBinopDivIntegers`, check if `t2 == 0`
2. **Invalid pointer dereference** — before every field access, method call on object, or array element access, check if base address is 0 (nil)
3. **Out of bounds array access** — before every array subscript, check `0 ≤ index < length`

Each check should:
- Emit a conditional jump to an error-handling label
- The error handler prints the error message string and exits via syscall

### Step 1.4 — Function Calling Convention

Define a calling convention using the MIPS stack:

1. **Caller side (call site):**
   - Evaluate arguments left-to-right
   - Push arguments onto the stack
   - Push return address (`$ra`)
   - Jump-and-link to the callee's label
   - Pop arguments after return
   - Retrieve return value from `$v0`

2. **Callee side (function entry/exit):**
   - **Prologue:** Save `$fp`, set new `$fp = $sp`, allocate locals
   - **Body:** Execute function body; parameters accessed via `$fp + offset`
   - **Epilogue:** Place return value in `$v0`, restore `$sp`, `$fp`, `$ra`, jump back (`jr $ra`)

### Step 1.5 — Virtual Method Tables (vtables)

For each class:
- Build a vtable in the `.data` section containing labels of all methods (including inherited ones)
- Override entries for overridden methods
- At object creation (`new`), the first word of the object stores a pointer to its vtable
- Method calls on class instances: load vtable pointer, index into it, jump to method

### Step 1.6 — Global Variable Initialization

- Before [main()](file:///c:/Users/admin/projects/compilation/ex5/src/Main.java#11-105) starts, emit IR to evaluate and store all global variable initializers *in source order*
- The entry point of the MIPS program should first run the global-init sequence, then jump to [main](file:///c:/Users/admin/projects/compilation/ex5/src/Main.java#11-105)

---

## Part 2 — Liveness Analysis & Register Allocation (IR → Colored IR)

### Step 2.1 — Liveness Analysis

> [!IMPORTANT]
> This should be done on the IR, **per function**.

1. **Build a CFG for each function's IR** — reuse/adapt the existing [CFG.java](file:///c:/Users/admin/projects/compilation/ex5/src/cfg/CFG.java) class
2. **Compute liveness** — iterative dataflow analysis:
   - For each IR command, define `use(cmd)` and `def(cmd)` sets of temps
   - Add `getUsedTemps()` and `getDefinedTemps()` methods to each [IrCommand](file:///c:/Users/admin/projects/compilation/ex5/src/ir/IrCommand.java#14-25) subclass
   - Compute **live-in** and **live-out** sets for each IR command using the standard backward equations:
     - `live-out[n] = ∪ live-in[s]` for all successors `s` of `n`
     - `live-in[n] = use[n] ∪ (live-out[n] − def[n])`

### Step 2.2 — Interference Graph

1. Build an undirected graph where:
   - Each node represents a temp
   - An edge [(t1, t2)](file:///c:/Users/admin/projects/compilation/ex5/src/ir/Ir.java#16-81) exists iff `t1` and `t2` are both live at the same point (i.e., both in the same live-out set, or one is defined while the other is live)
2. Implementation: `src/regalloc/InterferenceGraph.java`

### Step 2.3 — Graph Coloring (Simplification Only)

> [!IMPORTANT]
> Per the assignment: implement **only simplification-based** coloring. No spilling or MOV coalescing.

1. **K = 10** (registers `$t0`–`$t9`)
2. **Simplification:** Repeatedly remove a node with degree < K, push it onto a stack
3. **Select:** Pop nodes from the stack and assign colors (registers) that don't conflict with already-colored neighbors
4. If at any point no node has degree < K, print `Register Allocation Failed` and exit
5. Implementation: `src/regalloc/RegisterAllocator.java`

### Step 2.4 — Apply Allocation

- Replace all `Temp` references in IR commands with their assigned physical register (`$t0`–`$t9`)
- This mapping is passed down to the MIPS generation step

---

## Part 3 — MIPS Code Generation (Colored IR → MIPS Assembly)

### Step 3.1 — Create `src/mips/MipsGenerator.java`

As mentioned in the assignment starter code, this class handles outputting MIPS instructions to the output file. Key features:

- Maintain a `StringBuilder` or `PrintWriter` for `.data` and `.text` sections
- Provide helper methods:
  - `emitLabel(String label)`
  - `emitInstruction(String op, String... args)` — e.g. `add $t0, $t1, $t2`
  - `emitSyscall(int code)`
  - `emitDataString(String label, String value)` — adds a `.asciiz` to `.data`

### Step 3.2 — Add `mipsMe()` to Each IR Command

Each [IrCommand](file:///c:/Users/admin/projects/compilation/ex5/src/ir/IrCommand.java#14-25) subclass gets a `mipsMe(MipsGenerator gen, Map<Temp, String> regMap)` method that emits the corresponding MIPS instruction(s):

| IR Command | MIPS Translation |
|---|---|
| [IRcommandConstInt(t, v)](file:///c:/Users/admin/projects/compilation/ex5/src/ir/IRcommandConstInt.java#15-26) | `li $tX, v` |
| [IrCommandBinopAddIntegers(dst, t1, t2)](file:///c:/Users/admin/projects/compilation/ex5/src/ir/IrCommandBinopAddIntegers.java#15-28) | `add $tD, $tA, $tB` + saturation clamping |
| `IrCommandBinopSubIntegers(dst, t1, t2)` | `sub $tD, $tA, $tB` + saturation clamping |
| `IrCommandBinopMulIntegers(dst, t1, t2)` | `mul $tD, $tA, $tB` + saturation clamping |
| `IrCommandBinopDivIntegers(dst, t1, t2)` | `div $tA, $tB` / `mflo $tD` + saturation clamping |
| `IrCommandBinopEqIntegers(dst, t1, t2)` | `seq $tD, $tA, $tB` |
| `IrCommandBinopLtIntegers(dst, t1, t2)` | `slt $tD, $tA, $tB` |
| `IrCommandBinopGtIntegers(dst, t1, t2)` | `sgt $tD, $tA, $tB` |
| [IrCommandPrintInt(t)](file:///c:/Users/admin/projects/compilation/ex5/src/ir/IrCommandPrintInt.java#15-24) | `move $a0, $tX` / `li $v0, 1` / `syscall` + print trailing space |
| `IrCommandPrintString(t)` | `move $a0, $tX` / `li $v0, 4` / `syscall` |
| `IrCommandLabel(l)` | `l:` |
| `IrCommandJumpLabel(l)` | `j l` |
| `IrCommandJumpIfEqToZero(t, l)` | `beq $tX, $zero, l` |
| [IrCommandLoad(dst, var)](file:///c:/Users/admin/projects/compilation/ex5/src/ir/IrCommandLoad.java#15-36) | `lw $tD, offset($fp)` or `lw $tD, globalLabel` |
| `IrCommandStore(var, src)` | `sw $tS, offset($fp)` or `sw $tS, globalLabel` |
| `IrCommandNewObject(...)` | `li $a0, size` / `li $v0, 9` / `syscall` / `move $tD, $v0` |
| `IrCommandNewArray(...)` | compute size / `li $v0, 9` / `syscall` / store length / `move $tD, $v0` |
| `IrCommandCallFunc(...)` | Push args / `jal label` or indirect via vtable / retrieve `$v0` |
| `IrCommandReturn(...)` | `move $v0, $tX` / epilogue / `jr $ra` |

### Step 3.3 — Saturation Arithmetic

All integer arithmetic results must be clamped to the range `[-2^15, 2^15-1]` = `[-32768, 32767]`.

After each arithmetic operation, emit MIPS instructions to:
```mips
# Clamp to max
li   $s0, 32767
slt  $s1, $s0, $tD      # $s1 = 1 if result > 32767
movn $tD, $s0, $s1      # if overflow, $tD = 32767

# Clamp to min
li   $s0, -32768
slt  $s1, $tD, $s0      # $s1 = 1 if result < -32768
movn $tD, $s0, $s1      # if underflow, $tD = -32768
```

> [!NOTE]
> Use `$s0`–`$s9` as scratch registers for MIPS generation when needed (as permitted by the assignment).

### Step 3.4 — Runtime Error Handlers

Emit global error-handling routines in the `.text` section:

```mips
Label_division_by_zero:
    la $a0, msg_div_zero
    li $v0, 4
    syscall
    li $v0, 10
    syscall

Label_invalid_ptr_deref:
    la $a0, msg_invalid_ptr
    li $v0, 4
    syscall
    li $v0, 10
    syscall

Label_access_violation:
    la $a0, msg_access_violation
    li $v0, 4
    syscall
    li $v0, 10
    syscall
```

And in `.data`:
```mips
msg_div_zero:        .asciiz "Illegal Division By Zero"
msg_invalid_ptr:     .asciiz "Invalid Pointer Dereference"
msg_access_violation: .asciiz "Access Violation"
```

### Step 3.5 — Library Functions ([PrintInt](file:///c:/Users/admin/projects/compilation/ex5/src/ir/IrCommandPrintInt.java#15-24), `PrintString`)

- [PrintInt(n)](file:///c:/Users/admin/projects/compilation/ex5/src/ir/IrCommandPrintInt.java#15-24): `li $v0, 1` / `syscall` then print a space (`li $a0, 32` / `li $v0, 11` / `syscall`)
- `PrintString(s)`: `li $v0, 4` / `syscall`

### Step 3.6 — Program Structure

The final MIPS output file should have this structure:

```mips
.data
# String constants (.asciiz)
# Global variables (.word)
# Error message strings
# Virtual method tables

.text

# Error handler routines

# Global variable initialization code
main:
    # Initialize globals (in source order)
    # Jump to user's main function
    jal user_main
    # Exit
    li $v0, 10
    syscall

# All function bodies (translated from IR)
```

---

## Part 4 — Update [Main.java](file:///c:/Users/admin/projects/compilation/ex5/src/Main.java) (Compiler Pipeline)

### Step 4.1 — Modify the Compilation Pipeline

Replace the ex4 logic (uninitialized-var analysis + output) with:

```
1. Lex + Parse → AST
2. Semantic analysis → Type-checked AST
3. IR generation (irMe()) → List<IrCommand>
4. Liveness analysis (per function)
5. Interference graph construction
6. Register allocation (graph coloring, K=10)
   - If fails → print "Register Allocation Failed" and exit
7. MIPS code generation (mipsMe() on each IR command with register map)
8. Write MIPS output to file
```

### Step 4.2 — Error Handling

The output should be **exactly one** of:
- `ERROR` — lexical error
- `ERROR(n)` — syntax or semantic error at line `n`
- `Register Allocation Failed` — coloring failed
- The MIPS assembly code — if everything succeeded

---

## Part 5 — New Source Files Summary

| New File | Package | Purpose |
|---|---|---|
| `src/mips/MipsGenerator.java` | `mips` | Output MIPS instructions and `.data` declarations |
| `src/regalloc/InterferenceGraph.java` | `regalloc` | Build interference graph from liveness info |
| `src/regalloc/RegisterAllocator.java` | `regalloc` | Simplification-based graph coloring with K=10 |
| `src/regalloc/LivenessAnalysis.java` | `regalloc` | Per-function liveness analysis on IR |
| Various new `IrCommand*.java` | [ir](file:///c:/Users/admin/projects/compilation/ex5/src/ast/AstDecFunc.java#140-149) | New IR command types (see Step 1.1) |

---

## Part 6 — Implementation Order (Recommended)

> [!TIP]
> Follow this order to build incrementally and test at each stage.

1. **Start with a trivial program** — e.g., `void main() { PrintInt(42); }`. Get this compiling to MIPS and running in SPIM.
2. **Add integer arithmetic** with saturation — test binops.
3. **Add control flow** (if/else, while) — test conditionals and loops.
4. **Add function calls** — define calling convention, test with simple functions.
5. **Add global variables** — initialization before main.
6. **Add classes** — object allocation, field access, vtables.
7. **Add arrays** — allocation, subscript, bounds checking.
8. **Add strings** — constants, concatenation, printing, equality.
9. **Add runtime checks** — division by zero, null pointer, out-of-bounds.
10. **Implement liveness analysis** on the IR.
11. **Build interference graph + register allocation.**
12. **End-to-end testing** with provided test cases.

---

## Verification Plan

### Automated Tests

Run the existing test suite from the `input/` and `expected_output/` directories:

```bash
cd ex5
make compile
# For each test:
java -jar ANALYZER input/TEST_1.txt output/TEST_1_OUTPUT.txt
# Then run in SPIM:
spim -file output/TEST_1_OUTPUT.txt
# Compare SPIM output with expected_output/TEST_1_OUTPUT.txt
```

### Manual Verification

1. Write simple L programs by hand, compile, and run in SPIM
2. Verify saturation arithmetic edge cases (`32767 + 1 → 32767`, `-32768 - 1 → -32768`)
3. Verify all three runtime errors produce the correct messages
4. Verify register allocation fails gracefully on programs with too many live variables
5. Test class inheritance with overridden methods
6. Test the provided `Figure 1` example from the assignment (should print `32766`)
