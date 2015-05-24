/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package symbolTable;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Table of SymbolTableEntry objects, with relevant routines for 
 * inserting and searching for entries.
 * @author elcowley
 */
public class SymbolTable {
    
    public Hashtable<String, SymbolTableEntry> table;
    
    /** Constructor for SymbolTable class.
     * 
     * @param size Size of symbol table to be created.
     */
    public SymbolTable(int size) {
        this.table = new Hashtable<String, SymbolTableEntry>(size);
    }
    
    /** Inserts an entry into the SymbolTable.
     * 
     * @param entry Entry to be inserted into SymbolTable.
     * @return Entry that was inserted into SymbolTable.
     */
    public SymbolTableEntry insert(SymbolTableEntry entry) {
        if (table.containsKey(entry.name.toLowerCase())) {
            return table.get(entry.name.toLowerCase());
        }
        else {
            return table.put(entry.name.toLowerCase(), entry);
        }
    }

    /** Prints out the contents of the SymbolTable. */
    public void dumpTable() {
        Enumeration<String> e = table.keys();
        while (e.hasMoreElements()) {
            Object element = e.nextElement();
            System.out.print(element);
            if (element instanceof SymbolTableEntry) {
                System.out.print(" " + ((SymbolTableEntry)element).getAddress());
            }
            System.out.print("\n");
        }
    }
    
    /** Searches for an entry in the SymbolTable. If not found, inserts the 
     *  entry into the SymbolTable.
     * 
     *  @param entry Entry to be looked up in SymbolTable.
     *  @return Entry that was looked up in SymbolTable.
     */
    public SymbolTableEntry lookup(SymbolTableEntry entry) {
        if (table.containsKey(entry.name.toLowerCase())) {
            return table.get(entry.name.toLowerCase());
        }
        else {
            return null;
        }
    }
    
    public SymbolTableEntry lookup(String name) {
        if (table.containsKey(name.toLowerCase())) {
            return table.get(name.toLowerCase());
        }
        else {
            return null;
        }
    }
    
    /**
     * Delete the SymbolTable.
     */
    public void delete() {
        table.clear();
    }
    
}
