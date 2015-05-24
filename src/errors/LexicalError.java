/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package errors;

import lex.CharStream;
import lex.Tokenizer;

/**
 *
 * @author Eliot Cowley
 */

/** Outputs errors in lexical analysis.
 */
public class LexicalError extends CompilerError {
    
    public String string;
    
    /** Constructor for LexicalError class. 
     * @param string Error message to display.
     */
    public LexicalError(String string) {
        //super(string);
        this.string = string;
        //System.out.println(string);
    }

    /** Tokenizer found an illegal character.
     * @param ch Illegal character.
     * @param lineNumber Line number that illegal character was found on.
     */
    public static LexicalError IllegalCharacter(char ch, int lineNumber) {
        //System.err.println("Illegal character " + ch + " found at " + lineNumber);
        return new LexicalError("ERROR! Illegal character " + ch + " found on line " + lineNumber);
    }

    /** Tokenizer found a bad comment.
     * @param lineNumber Line number at which bad comment was found.
     */
    public static LexicalError BadComment(int lineNumber) {
        return new LexicalError("ERROR! Bad comment found on line " + lineNumber);
    }

    /** Tokenizer found an unterminated comment.
     * @param lineNumber Line number at which unterminated comment was found.
     */
    public static LexicalError UnterminatedComment(int lineNumber) {
        return new LexicalError("ERROR! Unterminated comment found on line " + lineNumber);
    }
    
    /** Tokenizer found an identifier that is too long.
     * @param lineNumber Line number at which identifier was found.
     */
    public static LexicalError IdentifierTooLong(int lineNumber) {
        return new LexicalError("ERROR! Found an identifier with a character count "
                + "exceeding the maximum allowable length of " + Tokenizer.MAX 
                + " on line " + lineNumber);
    }
    
    /** Tokenizer found an identifier that starts with a number.
     * @param lineNumber Line number at which identifier was found.
     */
    public static LexicalError IdentifierStartsWithNumber(int lineNumber, String token) {
        return new LexicalError("ERROR! Found an identifier " + token + " that starts with a "
                + "number on line " + lineNumber);
    }
    
}
