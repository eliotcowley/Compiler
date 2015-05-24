/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package symbolTable;

import lex.TokenType;

/**
 * Array entry in SymbolTable.
 * @author elcowley
 */
public class ArrayEntry extends SymbolTableEntry {
    
    private int address;
    private int upperBound;
    private int lowerBound;
    
    /**
     * Default constructor for ArrayEntry class.
     */
    public ArrayEntry() {
        
    }
    
    public ArrayEntry(String name) {
        this.name = name;
    }
    
    /** 
     * Constructor for ArrayEntry class.
     * 
     * @param name Actual string passed by parser.
     * @param type Type of token.
     * @param upperBound Upper bound of array.
     * @param lowerBound Lower bound of array.
     */
    public ArrayEntry(String name, TokenType type, int upperBound, 
            int lowerBound) {
        this.name = name;
        this.type = type;
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }
    
    /** Constructor for ArrayEntry class.
     * 
     * @param name Actual string passed by parser.
     * @param address Address offset where entry will be stored.
     * @param type Type of token.
     * @param upperBound Upper bound of array.
     * @param lowerBound Lower bound of array.
     */
    public ArrayEntry(String name, int address, TokenType type, int upperBound, 
            int lowerBound) {
        this.name = name;
        this.address = address;
        this.type = type;
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }
    
    public int getAddress() {
        return this.address;
    }
    
    public int getUpperBound() {
        return this.upperBound;
    }
    
    public int getLowerBound() {
        return this.lowerBound;
    }
    
    public void setAddress(int address) {
        this.address = address;
    }
    
    public void setUpperBound(int ub) {
        this.upperBound = ub;
    }
    
    public void setLowerBound(int lb) {
        this.lowerBound = lb;
    }
}
