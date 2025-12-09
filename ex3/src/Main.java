   
import java.io.*;
import java.io.PrintWriter;
import java_cup.runtime.Symbol;
import ast.*;

public class Main
{
	static public void main(String argv[])
	{
		Lexer l;
		Parser p;
		Symbol s;
		AstDecList ast;
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

			/******************************************/
			/* [8] If we got here, semantic analysis */
			/*     was successful - write OK         */
			/******************************************/
			fileWriter.print("OK");
			fileWriter.close();

			/*************************************/
			/* [9] Finalize AST GRAPHIZ DOT file */
			/*************************************/
			AstGraphviz.getInstance().finalizeFile();
    	}
		catch (SemanticException e)
		{
			/********************************************/
			/* Semantic error - write ERROR(line) to   */
			/* output file and exit                     */
			/********************************************/
			try
			{
				if (fileWriter != null)
				{
					fileWriter.print("ERROR(" + e.getLineNumber() + ")");
					fileWriter.close();
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		catch (Exception e)
		{
			/********************************************/
			/* Lexical or syntax error - write ERROR   */
			/* to output file                           */
			/********************************************/
			try
			{
				if (fileWriter != null)
				{
					fileWriter.print("ERROR");
					fileWriter.close();
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
}


