/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lex;

/**
 *
 * @author Eliot
 */
interface GrammarSymbol {
    /** Return index in TokenType enum of GrammarSymbol. */
    public int getIndex ();
    
    /** Return true if GrammarSymbol is a Token. */
    public boolean isToken ();
}
