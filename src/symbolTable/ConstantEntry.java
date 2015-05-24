/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package symbolTable;

import lex.TokenType;

/**
 * Constant found in parsing to be used in SymbolTable.
 * @author elcowley
 */
public class ConstantEntry extends SymbolTableEntry {
    
    /** Default constructor for ConstantEntry class. */
    public ConstantEntry() {
        super();
    }
    
    /** Constructor for ConstantEntry class.
     * 
     * @param name Actual string passed by parser.
     * @param type Type of token.
     */
    public ConstantEntry(String name, TokenType type) {
        super(name, type);
    }
    
}
