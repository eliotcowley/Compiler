/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lex;

import errors.LexicalError;
import org.apache.commons.lang3.StringUtils;
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
    
    /* Last recognized token */
    private Token previousToken = new Token(TokenType.ENDOFFILE);
    private TokenType previousType = previousToken.getType();
    
    /* Characters being kept track of */
    private char chPrev = 0;
    private char ch = 0;
    private char next = 0;
    private char nextNext = 0;

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
        updateChars();
//        System.out.println("Current: " + ch);
//        System.out.println("Next: " + next);
//        System.out.println("Next Next: " + nextNext);
        /* Skip over initial whitespace */
        while (Character.isWhitespace(ch)) {
            updateChars();
        }
        /* Add characters to StringBuilder until it hits a blank, EOF, endmarker,
         plus, or minus */
        while (!(Character.isWhitespace(ch)) && (ch != CharStream.EOF) && (ch != CharStream.ENDMARKER)
                && (ch != CharStream.PLUS) && (ch != CharStream.MINUS) && (ch != '*')
                && (ch != '/') && (ch != '(') && (ch != ')') && (ch != '[') && (ch != ']')) {
            sb.append(ch);
            if (sb.length() > MAX) {
                throw LexicalError.IdentifierTooLong(charStream.tempLineNumber);
            }
            if ((next == CharStream.ENDMARKER) || (next == CharStream.PLUS) || (next == CharStream.MINUS) 
                    || (next == '*') || (next == '/')) {
                break;
            }
            /* to account for integers that end with 'e', so we count 'e' as a 
             * separate identifier */
            if ((Character.isDigit(ch)) && (Character.toLowerCase(next) == 'e') && (!Character.isDigit(nextNext))) {
                    break;
            }
            updateChars();
        }
        /* catch doubledots */
        if (ch == CharStream.ENDMARKER) {
                if (next == CharStream.ENDMARKER) {
                    if (sb.length() == 0) {
                        sb.append(CharStream.DOUBLEDOT);
                        updateChars();
                    }
                }
                /* check for float */
                else if (Character.isDigit(next)) {
                    if (NumberUtils.isDigits(sb.toString())) {
                        sb.append(CharStream.ENDMARKER);
                        updateChars();
                        while ((Character.isDigit(ch)) || 
                                ((ch == 'e') && (Character.isDigit(next)))) {
                            sb.append(ch);
                            updateChars();
                        }
                    }
                    /* a simple end marker */
                    else {
                        sb.append(ch);
                    }
                }
                /* a simple end marker */
                else {
                    sb.append(ch);
                }
        }
        /* catch pluses */
        else if (ch == CharStream.PLUS) {
            if (sb.length() == 0) {
                sb.append(ch);
            }
        }
        /* catch minuses */
        else if (ch == CharStream.MINUS) {
            if (sb.length() == 0) {
                sb.append(ch);
            }
        }
        /* catch multiplication operators */
        else if (ch == '*') {
            if (sb.length() == 0) {
                sb.append(ch);
            }
        }
        /* catch division operators */
        else if (ch == '/') {
            if (sb.length() == 0) {
                sb.append(ch);
            }
        }
        /* catch left parentheses */
        else if (ch == '(') {
            if (sb.length() == 0) {
                sb.append(ch);
            }
        }
        /* catch right parentheses */
        else if (ch == ')') {
            if (sb.length() == 0) {
                sb.append(ch);
            }
        }
        /* catch left bracket */
        else if (ch == '[') {
            if (sb.length() == 0) {
                sb.append(ch);
            }
        }
        /* catch right bracket */
        else if (ch == ']') {
            if (sb.length() == 0) {
                sb.append(ch);
            }
        }
        /* catch floats - StringBuilder currently holding series of digits, next 
         * char is '.' */
        else if (Character.isDigit(ch)) {
            if (next == '.') {
            if (Character.isDigit(nextNext)) {
                updateChars();
                sb.append(ch); // append '.'
                updateChars(); // in series of digits after decimal point
                while (Character.isDigit(next)) {
                    sb.append(ch); // append digit
                    updateChars();
                }
                sb.append(ch); // append last digit
            }
        }
        }
        String tokenString = sb.toString();
        String tokenStringLower = tokenString.toLowerCase();
        /* test cases and assign type */
        switch (tokenStringLower) {
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
                if ((previousType == TokenType.RIGHTPAREN) || (previousType == TokenType.RIGHTBRACKET)
                        || (previousType == TokenType.IDENTIFIER) || (previousType == TokenType.INTCONSTANT)
                        || (previousType == TokenType.REALCONSTANT)) {
                    token.setType(TokenType.ADDOP);
                    token.setValue(OpValue.PLUS.getIndex());
                }
                else {
                    token.setType(TokenType.UNARYPLUS);
                }
                break;
            case "-":
                if ((previousType == TokenType.RIGHTPAREN) || (previousType == TokenType.RIGHTBRACKET)
                        || (previousType == TokenType.IDENTIFIER) || (previousType == TokenType.INTCONSTANT)
                        || (previousType == TokenType.REALCONSTANT)) {
                    token.setType(TokenType.ADDOP);
                    token.setValue(OpValue.MINUS.getIndex());
                }
                else {
                    token.setType(TokenType.UNARYMINUS);
                }
                break;
            case "or":
                token.setType(TokenType.ADDOP);
                token.setValue(OpValue.OR.getIndex());
                break;
            case "=":
                token.setType(TokenType.RELOP);
                token.setValue(OpValue.EQUAL.getIndex());
                break;
            case "<>":
                token.setType(TokenType.RELOP);
                token.setValue(OpValue.NOTEQUAL.getIndex());
                break;
            case "<":
                token.setType(TokenType.RELOP);
                token.setValue(OpValue.LESSTHAN.getIndex());
                break;
            case ">":
                token.setType(TokenType.RELOP);
                token.setValue(OpValue.GREATERTHAN.getIndex());
                break;
            case "<=":
                token.setType(TokenType.RELOP);
                token.setValue(OpValue.LESSTHANOREQUAL.getIndex());
                break;
            case ">=":
                token.setType(TokenType.RELOP);
                token.setValue(OpValue.GREATERTHANOREQUAL.getIndex());
                break;
            case "*":
                token.setType(TokenType.MULOP);
                token.setValue(OpValue.MULTIPLICATION.getIndex());
                break;
            case "/":
                token.setType(TokenType.MULOP);
                token.setValue(OpValue.DIVISION.getIndex());
                break;
            case "div":
                token.setType(TokenType.MULOP);
                token.setValue(OpValue.DIV.getIndex());
                break;
            case "mod":
                token.setType(TokenType.MULOP);
                token.setValue(OpValue.MOD.getIndex());
                break;
            case "and":
                token.setType(TokenType.MULOP);
                token.setValue(OpValue.AND.getIndex());
                break;
            case ":=":
                token.setType(TokenType.ASSIGNOP);
                break;
            case ",":
                token.setType(TokenType.COMMA);
                break;
            case ";":
                token.setType(TokenType.SEMICOLON);
                break;
            case ":":
                token.setType(TokenType.COLON);
                break;
            case ")":
                token.setType(TokenType.RIGHTPAREN);
                break;
            case "(":
                token.setType(TokenType.LEFTPAREN);
                break;
            case "]":
                token.setType(TokenType.RIGHTBRACKET);
                break;
            case "[":
                token.setType(TokenType.LEFTBRACKET);
                break;
            case "..":
                token.setType(TokenType.DOUBLEDOT);
                break;
            case ".":
                token.setType(TokenType.ENDMARKER);
                break;
            default:
                /* positive integer, no signs */
                if (NumberUtils.isDigits(tokenString)) {
                    token.setType(TokenType.INTCONSTANT);
                }
                /* end of file */
                else if (ch == CharStream.EOF) {
                    token.setType(TokenType.ENDOFFILE);
                }
                /* floating point constant */
                else if (StringUtils.containsOnly(tokenString, "0123456789.e")) {
                    token.setType(TokenType.REALCONSTANT);
                }
                else {
                    if (Character.isDigit(tokenString.charAt(0))) {
                        System.out.println(tokenString);
                        throw LexicalError.IdentifierStartsWithNumber(charStream.tempLineNumber);
                    }
                    else {
                        token.setType(TokenType.IDENTIFIER);
                    }
                }
                break;
        }
        this.previousToken = token;
        previousType = previousToken.getType();
        if (token.getType() == TokenType.IDENTIFIER) {
            token.lexeme = tokenString;
        }
        else {
            token.lexeme = tokenStringLower;
        }
//        System.out.println("SB: " + sb.toString());
//        System.out.println("Got here!");
        return token;
    }
    
    /** getChar: Returns the next character in the input file. */
    private char getChar() throws LexicalError {
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
    
    /** updateChars: Reads next character and stores it in ch, and updates next 
     * and nextNext. */
    private void updateChars() throws LexicalError {
        chPrev = ch;
        try {
            ch = getChar();
            next = charStream.getNextChar();
            nextNext = charStream.getNextNextChar();
        }
        catch(LexicalError ex) {
            System.out.println(ex.string);
        }
    }
    
}
