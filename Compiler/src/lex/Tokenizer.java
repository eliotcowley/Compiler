/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lex;

import errors.LexicalError;
import org.apache.commons.lang3.math.NumberUtils;

/**
 *
 * @author Eliot
 */

/** Analyzes the input and divides it into valid tokens. 
 */
public class Tokenizer {
    
    /* Reads in characters from the input. */
    public CharStream charStream;
    
    /* Max length of token */
    public static int MAX = 100; 

    /** Constructor for Tokenizer class.
     * @param file File that Tokenizer will analyze.
     */
    public Tokenizer(String file) {
        this.charStream = new CharStream(file);
    }

    /** Analyze the input and return the next longest valid token. */
    public Token GetNextToken() throws LexicalError {
        Token token = new Token();
        StringBuilder sb = new StringBuilder();
        char ch = getChar();
        /* Skip over initial whitespace */
        while (ch == CharStream.BLANK) {
            ch = getChar();
        }
        /* Add characters to StringBuilder until it hits a blank or EOF */
        while ((ch != CharStream.BLANK) && (ch != CharStream.EOF)) {
            sb.append(ch);
            if (sb.length() > MAX) {
                throw LexicalError.IdentifierTooLong(charStream.lineNumber());
            }
            ch = getChar();
        }
        String tokenString = sb.toString();
        tokenString = tokenString.toLowerCase();
        token.lexeme = tokenString;
        /* test cases and assign type */
        switch (tokenString) {
            case "program":
                token.setType(TokenType.PROGRAM);
                break;
            case "begin":
                token.setType(TokenType.BEGIN);
                break;
            case "end":
                token.setType(TokenType.END);
                break;
            case "var":
                token.setType(TokenType.VAR);
                break;
            case "function":
                token.setType(TokenType.FUNCTION);
                break;
            case "procedure":
                token.setType(TokenType.PROCEDURE);
                break;
            case "result":
                token.setType(TokenType.RESULT);
                break;
            case "integer":
                token.setType(TokenType.INTEGER);
                break;
            case "real":
                token.setType(TokenType.REAL);
                break;
            case "array":
                token.setType(TokenType.ARRAY);
                break;
            case "of":
                token.setType(TokenType.OF);
                break;
            case "if":
                token.setType(TokenType.IF);
                break;
            case "then":
                token.setType(TokenType.THEN);
                break;
            case "else":
                token.setType(TokenType.ELSE);
                break;
            case "while":
                token.setType(TokenType.WHILE);
                break;
            case "do":
                token.setType(TokenType.DO);
                break;
            case "not":
                token.setType(TokenType.NOT);
                break;
            case "+":
                token.setType(TokenType.ADDOP);
                token.setValue(OpValue.PLUS.getIndex());
                break;
            case "-":
                token.setType(TokenType.ADDOP);
                token.setValue(OpValue.MINUS.getIndex());
                break;
            case "or":
                token.setType(TokenType.ADDOP);
                token.setValue(OpValue.OR.getIndex());
                break;
            case "=":
                token.setType(TokenType.RELOP);
                token.setValue(OpValue.EQUAL.getIndex());
                break;
            default:
                /* positive integer, no signs */
                if (NumberUtils.isDigits(tokenString)) {
                    token.setType(TokenType.INTCONSTANT);
                }
                else {
                    token.setType(TokenType.ENDOFFILE);
                }
                break;
        }
        return token;
    }
    
    /** getChar: Returns the next character in the input file. */
    private char getChar() {
        char ch = CharStream.EOF;
        boolean done = false;
        while (!done)
        {
           try
           {
              ch = charStream.currentChar();
              done = true;
           }
           catch (LexicalError ex)
           {
              System.out.println(ex.string);
           }
        }
        return ch;
    }
    
}
