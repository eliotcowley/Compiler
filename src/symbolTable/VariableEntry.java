package symbolTable;

import lex.*;

public class VariableEntry extends SymbolTableEntry {

	int address;
        boolean reserved = false;  
	boolean parm = false; 
        boolean functionResult = false;
	
	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public VariableEntry() {
            super();
	}

	public VariableEntry(String Name) {
		super(Name);
	}
	
	public VariableEntry(String Name, TokenType type) {
		super(Name, type);	
	} 
        
        public VariableEntry(String name, int address, TokenType type) {
            this.name = name;
            this.address = address;
            this.type = type;
        }

	public boolean isVariable() { 
		return true; 
	}
	
	public void print () {
		System.out.println("Variable Entry:");
		System.out.println("   Name    : " + this.getName());
		System.out.println("   Type    : " + this.getType());
		System.out.println("   Address : " + this.getAddress());
		System.out.println();
	}

        // A function result will be stored as a variable entry 
	public boolean isFunctionResult() {
		return functionResult;
	}

	public void setFunctionResult() {
		this.functionResult = true;
	}

        // this flag indicates if the variable is a parameter to a procedure or function
	public boolean isParameter() {
		return parm;
	}

        // read, write, and main are reserved	
	public boolean isReserved() {
		return reserved;
	}
	public void setParameter (boolean parm) {
		this.parm = parm;
	}
	
	public void setParm() {
		this.parm = true;
	}
        
	public void makeReserved() {
		this.reserved = true;
	}
	
}