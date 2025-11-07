import java.io.*;
import java.io.PrintWriter;

import java_cup.runtime.Symbol;

public class Main
{
	static public void main(String argv[])
	{
		Lexer l;
		Symbol s;
		FileReader fileReader;
		PrintWriter fileWriter;

        if (argv.length != 2) {
            System.out.println("Usage: java Main <input_file> <output_file>");
            return;
        }

		String inputFileName = argv[0];
		String outputFileName = argv[1];

        // Setup Logic
		try {
            /********************************/
            /* [1] Initialize a file reader */
            /********************************/
            fileReader = new FileReader(inputFileName);

            /********************************/
            /* [2] Initialize a file writer */
            /********************************/
            fileWriter = new PrintWriter(outputFileName);
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Parser Logic
        try {
            /******************************/
            /* [3] Initialize a new lexer */
            /******************************/
            l = new Lexer(fileReader);

            /***********************/
            /* [4] Read next token */
            /***********************/
            s = l.next_token();

			/********************************/
			/* [5] Main reading tokens loop */
			/********************************/
			boolean firstToken = true;
			while (s.sym != TokenNames.EOF)
			{
				/************************/
				/* [6] Print to console */
				/************************/
				String tokenName = getTokenName(s.sym);
				if (!firstToken) {
					System.out.print("\n");
				}
				System.out.print(tokenName);
				if (s.value != null) {
					System.out.print("(");
					System.out.print(s.value);
					System.out.print(")");
				}
				System.out.print("[");
				System.out.print(l.getLine());
				System.out.print(",");
				System.out.print(l.getTokenStartPosition());
				System.out.print("]");

				/*********************/
				/* [7] Print to file */
				/*********************/
				if (!firstToken) {
					fileWriter.print("\n");
				}
				fileWriter.print(tokenName);
				if (s.value != null) {
					fileWriter.print("(");
					fileWriter.print(s.value);
					fileWriter.print(")");
				}
				fileWriter.print("[");
				fileWriter.print(l.getLine());
				fileWriter.print(",");
				fileWriter.print(l.getTokenStartPosition());
				fileWriter.print("]");

				/***********************/
				/* [8] Read next token */
				/***********************/
				firstToken = false;
				s = l.next_token();
			}

			/******************************/
			/* [9] Close lexer input file */
			/******************************/
			l.yyclose();
    	}
		catch (Error | Exception e)
		{
            fileWriter.close();
            try { fileWriter = new PrintWriter(outputFileName);
            } catch (Exception e2) {
                e.printStackTrace();
                return;}
            fileWriter.print("ERROR");
            System.out.println(e.getMessage());
		}
        finally {
            /**************************/
            /* [10] Close output file */
            /**************************/
            fileWriter.close();
        }
	}

    private static String getTokenName(int tokenType) {
        switch (tokenType) {
            case TokenNames.EOF: return "EOF";
            case TokenNames.PLUS: return "PLUS";
            case TokenNames.MINUS: return "MINUS";
            case TokenNames.TIMES: return "TIMES";
            case TokenNames.DIVIDE: return "DIVIDE";
            case TokenNames.LPAREN: return "LPAREN";
            case TokenNames.RPAREN: return "RPAREN";
            case TokenNames.LBRACK: return "LBRACK";
            case TokenNames.RBRACK: return "RBRACK";
            case TokenNames.LBRACE: return "LBRACE";
            case TokenNames.RBRACE: return "RBRACE";
            case TokenNames.COMMA: return "COMMA";
            case TokenNames.DOT: return "DOT";
            case TokenNames.SEMICOLON: return "SEMICOLON";
            case TokenNames.ASSIGN: return "ASSIGN";
            case TokenNames.EQ: return "EQ";
            case TokenNames.LT: return "LT";
            case TokenNames.GT: return "GT";
            case TokenNames.ARRAY: return "ARRAY";
            case TokenNames.CLASS: return "CLASS";
            case TokenNames.RETURN: return "RETURN";
            case TokenNames.WHILE: return "WHILE";
            case TokenNames.IF: return "IF";
            case TokenNames.ELSE: return "ELSE";
            case TokenNames.NEW: return "NEW";
            case TokenNames.EXTENDS: return "EXTENDS";
            case TokenNames.NIL: return "NIL";
            case TokenNames.TYPE_INT: return "TYPE_INT";
            case TokenNames.TYPE_STRING: return "TYPE_STRING";
            case TokenNames.TYPE_VOID: return "TYPE_VOID";
            case TokenNames.INT: return "INT";
            case TokenNames.STRING: return "STRING";
            case TokenNames.ID: return "ID";
            default: return "UNKNOWN";
        }
    }
}


