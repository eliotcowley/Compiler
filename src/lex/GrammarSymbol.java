/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lex;

/**
 *
 * @author Eliot
 */
public interface GrammarSymbol {
    /** Return index in TokenType enum of GrammarSymbol. */
    public int getIndex ();
    
    /** Return true if GrammarSymbol is a Token. */
    public boolean isToken ();
    
    /** Returns true if GrammarSymbol is a NonTerminal. */
    public boolean isNonTerminal();
    
    /**
     * Returns true if GrammarSymbol is a SemanticAction.
     * @return boolean--true if SemanticAction, false otherwise.
     */
    public boolean isAction();
}
