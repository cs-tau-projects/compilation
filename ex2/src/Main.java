import java.io.*;
import java.io.PrintWriter;
import java_cup.runtime.Symbol;
import ast.*;

public class Main
{
	static public void main(String argv[])
	{
		Lexer l = null;
		Parser p;
		Symbol s;
		AstNode ast;
		FileReader fileReader;
		PrintWriter fileWriter = null;
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
			/* [5] Parse the input file */
			/***********************************/
			ast = (AstNode) p.parse().value;

			/*************************/
			/* [6] Print the AST ... */
			/*************************/
			ast.printMe();

			/********************************/
			/* [7] Write OK to output file  */
			/********************************/
			fileWriter.print("OK");

			/*************************/
			/* [8] Close output file */
			/*************************/
			fileWriter.flush();
			fileWriter.close();

			/*************************************/
			/* [9] Finalize AST GRAPHIZ DOT file */
			/*************************************/
			AstGraphviz.getInstance().finalizeFile();
    	}

		catch (Error e)
		{
			// Lexical errors throw Error
			e.printStackTrace();
			try {
				if (fileWriter != null) {
					fileWriter.close();
					fileWriter = new PrintWriter(outputFileName);
					fileWriter.print("ERROR");
					fileWriter.close();
				}
			} catch (Exception ex) {
				// ignore
			}
		}
		catch (Exception e)
		{
			// Syntax errors throw RuntimeException
			e.printStackTrace();
			try {
				if (fileWriter != null) {
					fileWriter.close();
					fileWriter = new PrintWriter(outputFileName);
					fileWriter.print("ERROR(" + l.getLine() + ")");
					fileWriter.close();
				}
			} catch (Exception ex) {
				// ignore
			}
		}
	}
}


