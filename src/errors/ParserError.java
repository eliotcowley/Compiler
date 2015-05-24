/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package errors;

import lex.*;

/**
 *
 * @author elcowley
 */
/** ParserError: Outputs errors the Parser finds to the console. */
public class ParserError extends CompilerError {
    
    private String string;
    
    /** Constructor for ParserError class. Takes a String as input, which 
     * will be its error message.
     */
    public ParserError(String string) {
        this.string = string;
        System.out.println(this.string);
    }
    
    /** ParserError tokenMismatch(GrammarSymbol predicted, 
     * GrammarSymbol current, int lineNumber)
     * The parser found a symbol different from what it was expecting.
     * @param predicted Predicted symbol on top of parse stack.
     * @param current Current symbol being read in by tokenizer.
     * @param lineNumber Current line number.
     * @return 
     */
    public static ParserError tokenMismatch(GrammarSymbol predicted, GrammarSymbol current, int lineNumber) {
        return new ParserError("ERROR! Expecting " + predicted + ", found " + 
                current + " on line " + lineNumber);
    }
    
    /** parseTableError(GrammarSymbol current, int lineNumber)
     * Parser found 999 entry (error) in Parse Table.
     * @param current Current symbol being read in by tokenizer.
     * @param lineNumber The current line number.
     * @return 
     */
    public static ParserError parseTable999Error(GrammarSymbol current, int lineNumber) {
        return new ParserError("ERROR! Unexpected symbol " + current +
                " on line " + lineNumber);
    }
    
}
