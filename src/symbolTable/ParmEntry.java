/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package symbolTable;

/**
 *
 * @author elcowley
 */
public class ParmEntry extends SymbolTableEntry {
    
    private boolean parm;
    private int upperBound;
    private int lowerBound;
    private boolean isArray;
    
    public ParmEntry() {
        parm = true;
    }
    
    public ParmEntry(String name) {
        this.name = name;
    }
    
    public boolean isParameter() {
        return true;
    }
    
    public void setUpperBound(int ub) {
        this.upperBound = ub;
    }
    
    public int getUpperBound() {
        return this.upperBound;
    }
    
    public void setLowerBound(int lb) {
        this.lowerBound = lb;
    }
    
    public int getLowerBound() {
        return this.lowerBound;
    }
    
    public void setArray(boolean array) {
        this.isArray = array;
    }
    
    public boolean isArray() {
        return this.isArray;
    }
    
}
