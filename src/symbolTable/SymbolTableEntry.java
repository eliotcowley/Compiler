/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package symbolTable;

import java.util.LinkedList;
import lex.TokenType;

/**
 * Base class for entry in SymbolTable.
 * @author elcowley
 */
 public class SymbolTableEntry {
    
    String name;
    TokenType type;
    int address;
    private SymbolTableEntry result;
    private int numberOfParameters;
    private boolean parm;
    private LinkedList<ParmEntry> parameterInfo;
    private boolean functionResult;
    private int upperBound;
    private int lowerBound;
    
    /** Default constructor for SymbolTableEntry class. */
    public SymbolTableEntry() {
        
    }
    
    public SymbolTableEntry(String name) {
        this.name = name;
        this.type = null;
    }
    
    public SymbolTableEntry(String name, TokenType type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() {
        return this.name;
    }
    
    public TokenType getType() {
        return this.type;
    }
    
    public int getAddress() {
        return this.address;
    }
    
    public void setAddress(int address) {
        this.address = address;
    }
    
    public void setType(TokenType type) {
        this.type = type;
    }
    
    public void setResult(VariableEntry result) {
        this.result = result;
    }
    
    public SymbolTableEntry getResult() {
        return this.result;
    }
    
    public void setNumberOfParameters(int i) {
        this.numberOfParameters = i;
    }
    
    public int getNumberOfParameters() {
        return this.numberOfParameters;
    }
    
    public void setParm(boolean parm) {
        this.parm = parm;
    }
    
    public boolean isParameter() {
        return parm;
    }
    
    public LinkedList<ParmEntry> getParameterInfo() {
        return this.parameterInfo;
        
    }
    
    // A function result will be stored as a variable entry 
    public boolean isFunctionResult() {
            return functionResult;
    }
    
    public int getUpperBound() {
        return this.upperBound;
    }
    
    public int getLowerBound() {
        return this.lowerBound;
    }
    
}
