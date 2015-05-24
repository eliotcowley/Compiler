/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package symbolTable;

import java.util.LinkedList;
import lex.TokenType;

/**
 * Function entry in SymbolTable.
 * @author elcowley
 */
public class FunctionEntry extends SymbolTableEntry {
    
    private int numberOfParameters;
    /* TO-DO: parameterInfo will eventually be a ParmEntry--int for now as 
     * placeholder */
    private LinkedList<ParmEntry> parameterInfo; 
    private SymbolTableEntry result;
    
    /** Default constructor for FunctionEntry class.
     * 
     * @param name Actual name of function passed by parser.
     */
    public FunctionEntry(String name) {
        super(name);
        parameterInfo = new LinkedList<ParmEntry>();
    }
    
    /** Constructor for FunctionEntry class.
     * 
     * @param name Actual string passed by parser.
     * @param numberOfParameters Number of parameters in function.
     * @param parameterInfo 
     * @param result Return value of function.
     */
    public FunctionEntry(String name, int numberOfParameters, 
            LinkedList<ParmEntry> parameterInfo, VariableEntry result) {
        this.name = name;
        this.numberOfParameters = numberOfParameters;
        this.parameterInfo = parameterInfo;
        this.result = result;
    }
    
    public int getNumberOfParameters() {
        return this.numberOfParameters;
    }
    
    public void setNumberOfParameters(int i) {
        this.numberOfParameters = i;
    }
    
    public LinkedList<ParmEntry> getParameterInfo() {
        return this.parameterInfo;
    }
    
    public SymbolTableEntry getResult() {
        return this.result;
    }
    
    public void addParameter(ParmEntry parm) {
        parameterInfo.add(parm);
        numberOfParameters++;
    }
    
}
