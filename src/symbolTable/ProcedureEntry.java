/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package symbolTable;

import java.util.LinkedList;
import lex.TokenType;

/**
 * Procedure entry in SymbolTable.
 * @author elcowley
 */
public class ProcedureEntry extends SymbolTableEntry {
    
    private int numberOfParameters;
    private LinkedList<ParmEntry> parameterInfo;
    
    public ProcedureEntry(String name) {
        super(name);
        parameterInfo = new LinkedList<ParmEntry>();
        numberOfParameters = 0;
    }
    
    public ProcedureEntry(String name, int numberOfParameters) {
        super(name);
        this.numberOfParameters = numberOfParameters;
        parameterInfo = new LinkedList<ParmEntry>();
    }
    
    /** Constructor for ProcedureEntry class.
     * 
     * @param name Actual string passed by parser.
     * @param numberOfParameters Number of parameters in procedure.
     * @param parameterInfo 
     */
    public ProcedureEntry(String name, int numberOfParameters, 
            LinkedList<ParmEntry> parameterInfo) {
        this.name = name;
        this.numberOfParameters = numberOfParameters;
        this.parameterInfo = parameterInfo;
    }
    
    public ProcedureEntry(String name, LinkedList<ParmEntry> parameterInfo) {
        this.name = name;
        this.parameterInfo = parameterInfo;
        numberOfParameters = 0;
    }
    
    public int getNumberOfParameters() {
        return this.numberOfParameters;
    }
    
    public LinkedList<ParmEntry> getParameterInfo() {
        return this.parameterInfo;
    }
    
    public void setNumberofParameters(int n) {
        this.numberOfParameters = n;
    }
    
    public void addParameter(ParmEntry parm) {
        parameterInfo.add(parm);
        numberOfParameters++;
    }
    
}
