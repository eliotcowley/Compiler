/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package symbolTable;

import lex.TokenType;

/**
 * Entry in keyword table.
 * @author elcowley
 */
public class KeywordEntry extends SymbolTableEntry {
    
    /** Constructor for KeywordEntry class.
     * 
     * @param name String representing the keyword.
     * @param type TokenType of keyword.
     */
    public KeywordEntry(String name, TokenType type) {
        this.name = name;
        this.type = type;
    }
    
}
