import java.io.*;
import java.io.PrintWriter;
import java.util.Set;
import java_cup.runtime.Symbol;
import ast.*;
import ir.*;
import cfg.*;
import dataflow.*;

public class Main
{
	static public void main(String argv[])
	{
		Lexer l;
		Parser p;
		Symbol s;
		AstDecList ast;
		FileReader fileReader;
		PrintWriter fileWriter;
		String inputFileName = argv[0];
		String outputFileName = argv[1];

		try
		{
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
			/* [9] Build the CFG ...    */
			/****************************/
			CFG cfg = new CFG(Ir.getInstance().getCommands());

			/****************************************/
			/* [10] Run uninitialized var analysis  */
			/****************************************/
			UninitializedVarAnalysis analysis = new UninitializedVarAnalysis(cfg);
			analysis.analyze();

			/****************************************/
			/* [11] Write results to output file    */
			/****************************************/
			Set<String> uninitVars = analysis.getUninitializedUses();
			for (String varName : uninitVars)
			{
				fileWriter.println(varName);
			}

			/**************************/
			/* [12] Close output file */
			/**************************/
			fileWriter.close();

			/*************************************/
			/* [13] Finalize AST GRAPHIZ DOT file */
			/*************************************/
			AstGraphviz.getInstance().finalizeFile();
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}


