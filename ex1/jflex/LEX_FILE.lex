/***************************/
/* FILE NAME: LEX_FILE.lex */
/***************************/

/*************/
/* USER CODE */
/*************/

import java_cup.runtime.*;

/******************************/
/* DOLLAR DOLLAR - DON'T TOUCH! */
/******************************/

%%

/************************************/
/* OPTIONS AND DECLARATIONS SECTION */
/************************************/
   
/*****************************************************/ 
/* Lexer is the name of the class JFlex will create. */
/* The code will be written to the file Lexer.java.  */
/*****************************************************/ 
%class Lexer

/********************************************************************/
/* The current line number can be accessed with the variable yyline */
/* and the current column number with the variable yycolumn.        */
/********************************************************************/
%line
%column

/*******************************************************************************/
/* Note that this has to be the EXACT same name of the class the CUP generates */
/*******************************************************************************/
%cupsym TokenNames

/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/
%cup

/* States */
%state BLOCK_COMMENT

/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/   
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied verbatim (letter to letter) into the Lexer class code.     */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */  
/*****************************************************************************/   
%{
	/*********************************************************************************/
	/* Create a new java_cup.runtime.Symbol with information about the current token */
	/*********************************************************************************/
	private Symbol symbol(int type)               {return new Symbol(type, yyline, yycolumn);}
	private Symbol symbol(int type, Object value) {return new Symbol(type, yyline, yycolumn, value);}

	/*******************************************/
	/* Enable line number extraction from main */
	/*******************************************/
	public int getLine() { return yyline + 1; } 

	/**********************************************/
	/* Enable token position extraction from main */
	/**********************************************/
	public int getTokenStartPosition() { return yycolumn + 1; } 
%}

/***********************/
/* MACRO DECLARATIONS */
/***********************/
LineTerminator	    = \r|\n|\r\n
WhiteSpace		    = {LineTerminator} | [ \t\f]
INTEGER			    = 0 | [1-9][0-9]*
STRING			    = \"[a-zA-Z]*\"
ID				    = [a-zA-Z][a-zA-Z0-9]*
LineCommentRegex    = [a-zA-Z0-9 \t\f()\[\]{}?!+\-*/.;]*
BlockCommentChar    = [a-zA-Z0-9 \t\f\r\n()\[\]{}?!+\-/.;]

/******************************/
/* DOLLAR DOLLAR - DON'T TOUCH! */
/******************************/

%%

/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/

/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only be matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/

<YYINITIAL> {

/* Operators */
"+"					{ return symbol(TokenNames.PLUS);}
"-"					{ return symbol(TokenNames.MINUS);}
"*"					{ return symbol(TokenNames.TIMES);}
"/"					{ return symbol(TokenNames.DIVIDE);}

/* Delimiters */
"("					{ return symbol(TokenNames.LPAREN);}
")"					{ return symbol(TokenNames.RPAREN);}
"["					{ return symbol(TokenNames.LBRACK);}
"]"					{ return symbol(TokenNames.RBRACK);}
"{"					{ return symbol(TokenNames.LBRACE);}
"}"					{ return symbol(TokenNames.RBRACE);}
","					{ return symbol(TokenNames.COMMA);}
"."					{ return symbol(TokenNames.DOT);}
";"					{ return symbol(TokenNames.SEMICOLON);}

/* Assignment and Comparison */
":="				{ return symbol(TokenNames.ASSIGN);}
"="					{ return symbol(TokenNames.EQ);}
"<"					{ return symbol(TokenNames.LT);}
">"					{ return symbol(TokenNames.GT);}

/* Keywords */
"array"				{ return symbol(TokenNames.ARRAY);}
"class"				{ return symbol(TokenNames.CLASS);}
"return"			{ return symbol(TokenNames.RETURN);}
"while"				{ return symbol(TokenNames.WHILE);}
"if"				{ return symbol(TokenNames.IF);}
"else"				{ return symbol(TokenNames.ELSE);}
"new"				{ return symbol(TokenNames.NEW);}
"extends"			{ return symbol(TokenNames.EXTENDS);}
"nil"				{ return symbol(TokenNames.NIL);}

/* Type Keywords */
"int"				{ return symbol(TokenNames.TYPE_INT);}
"string"			{ return symbol(TokenNames.TYPE_STRING);}
"void"				{ return symbol(TokenNames.TYPE_VOID);}

/* Literals and Identifiers */
{INTEGER}			{
						int value = Integer.valueOf(yytext());
						if (value > 32767) {
							throw new Error("Lexical error: integer out of range at line " + (yyline+1));
						}

						else if (yytext().startsWith("0")){
							throw new Error("Lexical error: trailing zeros are not permitted " + (yyline+1));
						}
						return symbol(TokenNames.INT, value);
					}
{STRING}			{ return symbol(TokenNames.STRING, yytext());}
{ID}				{ return symbol(TokenNames.ID, yytext());}

/* Whitespace */
{WhiteSpace}		{ /* just skip what was found, do nothing */ }

/* Comments */
"//"{LineCommentRegex}{LineTerminator}      { /* Ignore Line Comments */ }
"/*"                                        { yybegin(BLOCK_COMMENT); }

/* End of File */
<<EOF>>				{ return symbol(TokenNames.EOF);}

/* Catch-all for invalid tokens */
[^]					{ throw new Error("Lexical error: invalid character '" + yytext() + "' at line " + (yyline+1)); }
}

<BLOCK_COMMENT> {
"*/"                { yybegin(YYINITIAL); }
{BlockCommentChar}  { /* just skip what was found, do nothing */ }
"*"                 { /* just skip what was found, do nothing */ }
<<EOF>>             { throw new Error("Lexical error: block comment not closed at line " + (yyline+1)); }
[^]                 { throw new Error("Lexical error: invalid character '" + yytext() + "' at line " + (yyline+1)); }
}