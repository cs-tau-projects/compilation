# Exercise 5 Code Generation
## Compilation 0368-3133
[cite_start]**Due 15/3/2026 before 23:59** [cite: 2]

---

## 1 Assignment Overview
Congratulations! [cite_start]You have reached the final step in building a complete compiler for L programs[cite: 4]. [cite_start]The fifth and final exercise implements the code generation phase for L programs[cite: 5]. [cite_start]The chosen destination language is MIPS assembly, favored for its straightforward syntax, complete toolchain and available tutorials[cite: 6].

The exercise can be roughly divided into three parts as follows:
* [cite_start]Recursively traverse the AST to create an intermediate representation (IR) of the program[cite: 8].
* [cite_start]Perform liveness analysis, build the interference graph, and allocate those hundreds (or so) temporaries into 10 physical registers[cite: 9].
* [cite_start]Translate the IR into MIPS instructions[cite: 10].

[cite_start]The input for this final exercise is a single text file containing a L program[cite: 11]. [cite_start]The output is a single text file containing the corresponding MIPS assembly translation[cite: 12].

---

## 2 The L Runtime Behavior
[cite_start]This section describes how L programs execute[cite: 14].

### 2.1 Binary Operations
[cite_start]**Integers:** Integers in L are artificially bounded between $-2^{15}$ and $2^{15}$[cite: 16]. [cite_start]The semantics of integer binary operations in L is therefore somewhat different than that of standard programming languages[cite: 17]. [cite_start]L uses saturation arithmetic: results exceeding the range are "clamped" to the nearest boundary[cite: 18]. [cite_start]For any operator $\odot \in \{+, -, *, /\}$ (where $/$ denotes integer floor division $\lfloor a/b \rfloor$), let $\odot_{[L]}$ denote the corresponding operator in L[cite: 19]. The operation is defined as follows:

$$a \oplus_{[L]} b = \begin{cases} -2^{15} & \text{if } (a \otimes b) \le -2^{15} \\ 2^{15}-1 & \text{if } (a \otimes b) \ge 2^{15}-1 \\ a \ominus b & \text{otherwise} \end{cases}$$ [cite: 20]

**Strings:** Strings can be concatenated with binary operation, and tested for (contents) equality with binary operator[cite: 21]. When concatenating two (null terminated) strings $\{s_i\}_{i=1}^2,$ the resulting string $s_1s_2$ is allocated on the heap, and should be null terminated[cite: 22]. The result of testing contents equality is either 1 when they are equal, or 0 otherwise[cite: 23].

**Arrays:** Testing equality of arrays should be done by comparing the address values of the two arrays[cite: 24]. The result is 1 if they are equal and otherwise 0[cite: 25].

**Classes:** Testing equality of class objects should be done by comparing the address values of the two objects[cite: 27]. The result is 1 if they are equal and otherwise 0[cite: 28].

### 2.2 If and While Statements
If and while statements in L behave similarly to (practically) all programming languages[cite: 30].
* [cite_start]**Control Flow:** Both constructs evaluate an integer condition, where 0 represents false and any non-zero value represents true[cite: 31].
* [cite_start]**While statements:** The condition is evaluated before every iteration[cite: 32]. [cite_start]If the condition is non-zero, the body is executed, and the process repeats[cite: 32]. [cite_start]If the condition is 0, the loop terminates, and control passes to the statement immediately following the loop body[cite: 33].
* [cite_start]**If statements:** The condition is evaluated first[cite: 34]. [cite_start]If the result is non-zero, the body of the if is executed[cite: 34]. [cite_start]If the result is 0, the body of the else is executed (if one is present)[cite: 35]. [cite_start]Upon completion of either path, control continues to the statement immediately following the entire structure[cite: 36].

### 2.3 Program Execution and Evaluation Order
[cite_start]**Program entry point:** Every (valid) L program has a main function with signature: `void main()`[cite: 38]. [cite_start]This function is the entry point of execution[cite: 39].

[cite_start]**Function argument evaluation:** When calling a function with more than one input parameter, the evaluation order matters[cite: 40]. [cite_start]You should evaluate the sent parameters from left to right, so for example, the code in Figure 1 should print 32766[cite: 41].

    class counter { int i := 32767; }
    counter c := nil;
    int inc(){ c.i := c.i + 1; return 0; }
    int dec(){ c.i := c.i - 1; return 9; }
    int foo(int m, int n){ return c.i; }
    void main()
    {
        c := new counter;
        PrintInt(foo(inc(),dec()));
    }
**Figure 1: Evaluation order of a called function's parameters matters** [cite: 50]

[cite_start]**Global variables initialization:** When initializing global variables, the order matters and should match their order of appearance in the original program[cite: 51]. [cite_start]Before entering main, all global variables with initialized values should be evaluated[cite: 52]. [cite_start]Note that global variables may be initialized using arbitrary non-constant expressions[cite: 53].

[cite_start]**Binary operations and assignments:** For all binary operations (including assignment), the left-hand side should be evaluated first[cite: 54]. [cite_start]Recall that integer operations use saturation arithmetic, and that the maximum value is $2^{15}-1=32767$[cite: 55].

[cite_start]**Class data members initialization:** Occurs during object construction[cite: 57]. [cite_start]The specific order in which data members are initialized is irrelevant[cite: 57]. [cite_start]You may assume that data members may be initialized only with constant literals (specifically integers, strings, and nil)[cite: 58].

### 2.4 Library Functions
[cite_start]You can assume that the names `PrintInt` and `PrintString` are never redefined[cite: 60]. [cite_start]Consequently, calling these functions always invokes the corresponding L library functions[cite: 61]. [cite_start]Both functions must be implemented using the corresponding MIPS system calls[cite: 62]. Specifically:
* [cite_start]`PrintInt` prints the integer argument followed by a single space[cite: 63].
* [cite_start]`PrintString` prints the string argument[cite: 63].
[cite_start](Note: The implementation of `PrintInt`, including the trailing space, is provided in the starter code [cite: 64]).

### 2.5 Runtime Checks
[cite_start]L enforces three kinds of runtime checks: division by zero, invalid pointer dereference, and out-of-bounds array access[cite: 67]. [cite_start]In all cases, the program must print a specific error message and exit gracefully (using the exit system call)[cite: 68].
* [cite_start]**Division by zero:** Must be detected when a denominator evaluates to 0[cite: 69].
    * [cite_start]Error message: `Illegal Division By Zero` [cite: 70]
* [cite_start]**Invalid pointer dereference:** Occurs when attempting to access a field, method, or array element of a class or array variable that is uninitialized or holding the value nil[cite: 73].
    * [cite_start]Error message: `Invalid Pointer Dereference` [cite: 74]
* [cite_start]**Out of bound array access:** Occurs when the accessed index falls outside the valid boundaries of the array[cite: 78]. [cite_start]That is, when the index is negative or greater than or equal to the length of the array[cite: 79].
    * [cite_start]Error message: `Access Violation` [cite: 80]

### 2.6 System Calls
[cite_start]MIPS supports a limited set of system calls, out of which we will need only four: printing an integer, printing a string, allocating heap memory and exit the program[cite: 84].

| System call example | MIPS code | Remarks |
| :--- | :--- | :--- |
| `PrintInt(17)` | [cite_start]`li $a0,17` <br> `li $v0,1` <br> `syscall` | [cite: 87] |
| `string s:="abc";` <br> `PrintString(s)` | `.data` <br> `myLovelyStr: .asciiz "abc"` <br> `.text` <br> `main:` <br> `la $a0, myLovelyStr` <br> `li $v0,4` <br> `syscall` | Printed string must be null terminated. [cite_start]It can be allocated inside the text section, or in the heap. [cite: 87] |
| `Malloc(17)` | `li $a0,17` <br> `li $v0,9` <br> `syscall` | The allocated address is returned in `$v0$. [cite_start]Note that the allocation size is specified in bytes. [cite: 87] |
| `Exit()` | `li $v0,10` <br> `syscall` | [cite_start]Make sure every MIPS program ends with exit. [cite: 87] |

---

## 3 Additional Guidelines
* [cite_start]**Uninitialized variables:** You are not required to perform the uninitialized variable analysis from the previous exercise[cite: 90]. [cite_start]Accessing an uninitialized variable results in undefined behavior[cite: 91].
* [cite_start]**Register allocation:** Allocation is performed on the IR[cite: 92]. [cite_start]Since the IR is low-level, register allocation applies strictly to temporaries[cite: 93]. [cite_start]You should only allocate registers `$t0-$t9$[cite: 94]. Implement only simplification-based register allocation; spilling or MOV instruction coalescing are not required[cite: 95]. If the allocator fails to color the graph, the compiler must print `Register Allocation Failed` and terminate[cite: 96].
* [cite_start]**MIPS code generation:** The translation from IR commands to MIPS instructions is generally straightforward[cite: 100]. [cite_start]If the translation requires the use of registers beyond those allocated to the temporaries (as may happen in array accesses and method calls), you can use registers `$s0-$s9$[cite: 101].

---

## 4 Input and Output
* [cite_start]**Input:** A single text file, the input L program[cite: 103]. [cite_start]You should handle the entire L programming language[cite: 104].
* [cite_start]**Output:** A single text file containing exactly one of the following[cite: 106]:
    * [cite_start]`ERROR`: If a lexical error is detected[cite: 108, 109].
    * `ERROR (location)`: If a syntax or semantic error is detected. [cite_start]The location is the line number of the first error encountered[cite: 110, 111].
    * [cite_start]`Register Allocation Failed`: If the register allocation fails[cite: 112, 113].
    * [cite_start]The MIPS assembly translation of the input program: If no errors occurred[cite: 114, 115].

---

## 5 SPIM
[cite_start]The project uses the SPIM 8.0 simulator[cite: 116, 117]. [cite_start]Your compiler must output a text file containing the translated MIPS instructions[cite: 118]. [cite_start]We will grade the assignment by running this output file directly in SPIM[cite: 119].

---

## 6 Submission Guidelines
[cite_start]Each group should have only one student submit the solution via the course Moodle[cite: 121]. [cite_start]Submit all your code in a single zip file named `<ID>.zip`, where `<ID>` is the ID of the submitting student[cite: 122].
[cite_start]The zip file must have the following structure at the top level[cite: 123]:
1. [cite_start]A text file named `ids.txt` containing the IDs of all team members (one ID per line)[cite: 124].
2. [cite_start]A folder named `ex5/` containing all your source code[cite: 125].
3. A makefile at `ex5/Makefile`. [cite_start]This makefile must build your source files into the compiler, which should be a runnable jar file located at `ex5/COMPILER`[cite: 126].

[cite_start]Command-line usage: `COMPILER receives 2 parameters (file paths): input (input file path) and output (output file path containing the expected output)`[cite: 128, 129].
[cite_start]Before submitting, you must run the self-check script provided with this exercise[cite: 130]. [cite_start]You must execute the self-check on your submission zip file within the school server (`nova.cs.tau.ac.il`)[cite: 132].

---

## 7 Starter Code
[cite_start]The skeleton provides a basic translation of the IR to MIPS instructions[cite: 145, 146].
Relevant files:
* [cite_start]`src/ir/*.java`: Classes for the IR commands with an additional `mipsMe` method that contains the translation to MIPS[cite: 148].
* [cite_start]`src/mips/MipsGenerator.java`: Contains methods to print MIPS instructions to the output[cite: 149].
Building and running:
* [cite_start]From the `ex5` directory, run `make` to compile the modules into `COMPILER`[cite: 154, 158].
* [cite_start]To also run `COMPILER` on `input/Input.txt` and execute the program using SPIM, run `make everything`[cite: 159, 160].