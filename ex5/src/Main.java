import java.io.*;
import java.io.PrintWriter;
import java.util.Set;
import java_cup.runtime.Symbol;
import ast.*;
import ir.*;
import cfg.*;
import dataflow.*;

public class Main {
	static public void main(String argv[]) {
		Lexer l;
		Parser p;
		Symbol s;
		AstDecList ast;
		FileReader fileReader;
		PrintWriter fileWriter;
		String inputFileName = argv[0];
		String outputFileName = argv[1];

		try {
			/********************************/
			/* [1] Initialize a file reader */
			/********************************/
			fileReader = new FileReader(inputFileName);

			/********************************/
			/* [2] Initialize a file writer */
			/********************************/
			fileWriter = new PrintWriter(outputFileName);

			/******************************/
			/* [3] Initialize a new lexer */
			/******************************/
			l = new Lexer(fileReader);

			/*******************************/
			/* [4] Initialize a new parser */
			/*******************************/
			p = new Parser(l);

			/***********************************/
			/* [5] 3 ... 2 ... 1 ... Parse !!! */
			/***********************************/
			ast = (AstDecList) p.parse().value;

			/*************************/
			/* [6] Print the AST ... */
			/*************************/
			ast.printMe();

			/**************************/
			/* [7] Semant the AST ... */
			/**************************/
			ast.semantMe();

			/**********************/
			/* [8] IR the AST ... */
			/**********************/
			ast.irMe();

			/****************************/
			/* [9] Build the CFG ... */
			/****************************/
			CFG cfg = new CFG(Ir.getInstance().getCommands());

			/****************************************/
			/* [10] Run Liveness Analysis           */
			/****************************************/
			regalloc.LivenessAnalysis liveness = new regalloc.LivenessAnalysis(cfg);
			liveness.analyze();

			/****************************************/
			/* [11] Build Interference Graph        */
			/****************************************/
			regalloc.InterferenceGraph graph = new regalloc.InterferenceGraph();
			graph.build(liveness, cfg);

			/****************************************/
			/* [12] Register Allocation             */
			/****************************************/
			regalloc.RegisterAllocator allocator = new regalloc.RegisterAllocator();
			try {
				allocator.allocate(graph);
			} catch (RuntimeException e) {
				if ("Register Allocation Failed".equals(e.getMessage())) {
					fileWriter.print("Register Allocation Failed\n");
					fileWriter.close();
					return;
				}
				throw e;
			}

			/****************************************/
			/* [13] Generate MIPS Assembly          */
			/****************************************/
			mips.MipsGenerator mipsGen = new mips.MipsGenerator();
			for (IrCommand cmd : Ir.getInstance().getCommands()) {
				cmd.mipsMe(mipsGen, allocator.getRegisterMap());
			}

			/****************************************/
			/* [14] Write MIPS Assembly to Output   */
			/****************************************/
			fileWriter.close();
			mipsGen.writeToFile(outputFileName);
			
			AstGraphviz.getInstance().finalizeFile();
		} catch (SemanticException e) {
			try (PrintWriter pw = new PrintWriter(outputFileName)) {
				pw.printf("ERROR(%d)\n", e.getLineNumber());
			} catch (Exception ex) {}
		} catch (Exception e) {
			try (PrintWriter pw = new PrintWriter(outputFileName)) {
				pw.print("ERROR\n");
			} catch (Exception ex) {}
		} catch (Error e) {
			// Catch lexical errors which sometimes are thrown as Error in JFlex
			try (PrintWriter pw = new PrintWriter(outputFileName)) {
				pw.print("ERROR\n");
			} catch (Exception ex) {}
		}
	}
}