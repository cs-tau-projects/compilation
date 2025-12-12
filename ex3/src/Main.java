import java.io.*;
import java.io.PrintWriter;
import java_cup.runtime.Symbol;
import ast.*;

public class Main
{
	static public void main(String argv[])
	{
		String inputFileName = argv[0];
		String outputFileName = argv[1];

		Lexer l;
		Parser p;
		AstDecList ast;
		PrintWriter fileWriter;

		try
		{
			fileWriter = new PrintWriter(outputFileName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}

		/******************************/
		/* [1] Lexer - errors = ERROR */
		/******************************/
		try
		{
			FileReader fileReader = new FileReader(inputFileName);
			l = new Lexer(fileReader);
		}
		catch (Error | Exception e)
		{
			e.printStackTrace();
			fileWriter.print("ERROR");
			fileWriter.close();
			return;
		}

		/*************************************/
		/* [2] Parser - errors = ERROR(line) */
		/*************************************/
		try
		{
			p = new Parser(l);
			ast = (AstDecList) p.parse().value;
		}
		catch (Error | Exception e)
		{
			e.printStackTrace();
			fileWriter.print("ERROR(" + l.getLine() + ")");
			fileWriter.close();
			return;
		}

		/*************************/
		/* [3] Print the AST ... */
		/*************************/
		ast.printMe();

		/*****************************************/
		/* [4] Semantic - errors = ERROR(line)  */
		/*****************************************/
		try
		{
			ast.semantMe();
		}
		catch (SemanticException e)
		{
			System.err.println("SemanticException: " + e.getMessage() + " at line " + e.getLineNumber());
			e.printStackTrace();
			fileWriter.print("ERROR(" + e.getLineNumber() + ")");
			fileWriter.close();
			return;
		}

		/******************************************/
		/* [5] Success - write OK                 */
		/******************************************/
		fileWriter.print("OK");
		fileWriter.close();

		/*************************************/
		/* [6] Finalize AST GRAPHIZ DOT file */
		/*************************************/
		AstGraphviz.getInstance().finalizeFile();
	}
}


