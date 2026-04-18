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
		Lexer l = null;
		Parser p;
		Symbol s;
		AstDecList ast;
		FileReader fileReader;
		PrintWriter fileWriter;
		String inputFileName = argv[0];
		String outputFileName = argv[1];

		try {
			// file reader
			fileReader = new FileReader(inputFileName);

			// file writer
			fileWriter = new PrintWriter(outputFileName);

			// init lexer
			l = new Lexer(fileReader);

			// init parser
			p = new Parser(l);

			// parse
			ast = (AstDecList) p.parse().value;

			// semant
			ast.semantMe();

			// ir
			ast.irMe();

			// build cfg
			CFG cfg = new CFG(Ir.getInstance().getCommands());

			// liveness analysis
			regalloc.LivenessAnalysis liveness = new regalloc.LivenessAnalysis(cfg);
			liveness.analyze();

			// interference graph
			regalloc.InterferenceGraph graph = new regalloc.InterferenceGraph();
			graph.build(liveness, cfg);

			// reg alloc
			regalloc.RegisterAllocator allocator = new regalloc.RegisterAllocator();
			try {
				allocator.allocate(graph);
			} catch (RuntimeException e) {
				if ("Register Allocation Failed".equals(e.getMessage())) {
					fileWriter.print("Register Allocation Failed");
					fileWriter.close();
					return;
				}
				throw e;
			}

			// generate mips
			mips.MipsGenerator mipsGen = new mips.MipsGenerator();
			for (IrCommand cmd : Ir.getInstance().getCommands()) {
				cmd.mipsMe(mipsGen, allocator.getRegisterMap());
			}

			// write to output
			fileWriter.close();
			mipsGen.writeToFile(outputFileName);
			
			AstGraphviz.getInstance().finalizeFile();
		} catch (SemanticException e) {
			try (PrintWriter pw = new PrintWriter(outputFileName)) {
				pw.printf("ERROR(%d)", e.getLineNumber());
			} catch (Exception ex) {}
		} catch (Exception e) {
			try (PrintWriter pw = new PrintWriter(outputFileName)) {
				pw.print("ERROR");
				if (l != null)
				{
					pw.printf("(%d)", l.getLine());
				}
			} catch (Exception ex) {}
		} catch (Error e) {
			try (PrintWriter pw = new PrintWriter(outputFileName)) {
				pw.print("ERROR");
			} catch (Exception ex) {}
		}
	}
}