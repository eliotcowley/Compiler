/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package semanticActions;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Vector;

/**
 * Quadruples class holding array of Quadruples, in which TVI code is stored as
 * generated.
 * @author elcowley
 */
public class Quadruples {
    
    private Vector<String[]> Quadruple;
    private int nextQuad;
    
    /**
     * Constructor for Quadruples class.
     */
    public Quadruples() {
        Quadruple = new Vector<String[]>();
    }
    
    /**
     * Get contents of field in Quad at location index.
     * @param quadIndex Index of Quad in Quadruple array to access.
     * @param field Field in Quad to access.
     * @return 
     */
    public String getField(int quadIndex, int field) {
        String[] temp = Quadruple.get(quadIndex);
        return temp[field];
    }
    
    /**
     * Set contents of field in Quad at location index.
     * @param quadIndex Index of Quad in Quadruple array to access.
     * @param index Index of field in Quad to set.
     * @param field New String to set as field.
     */
    public void setField(int quadIndex, int index, String field) {
        String temp[] = Quadruple.get(quadIndex);
        temp[index] = field;
        Quadruple.set(quadIndex, temp);
    }
    
    /**
     * Return index of next available quadruple.
     * @return Index of next available quadruple.
     */
    public int getNextQuad() {
        return nextQuad;
    }
    
    /**
     * Increment the quad index variable.
     */
    public void incrementNextQuad() {
        nextQuad++;
    }
    
    /**
     * Return the quadruple at index.
     * @param index Index of quad in Quadruple array to access.
     * @return Quad at index.
     */
    public String[] getQuad(int index) {
        return Quadruple.get(index);
    }
    
    /**
     * Add a quadruple to the Quad array.
     * @param quad Quad to add to Quadruple array.
     */
    public void addQuad(String[] quad) {
        Quadruple.add(quad);
    }
    
    /**
     * Print the Quadruple array.
     */
    public void print() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("out.tvi", "UTF-8");
        System.out.println("Quadruples:");
        Iterator<String[]> iterator = Quadruple.iterator();
        int lineNumber = 1;
        while (iterator.hasNext()) {
            String[] array = iterator.next();
            if (array[0] != "CODE") {
                System.out.print(lineNumber + ": ");
                writer.print(lineNumber + ": ");
                lineNumber++;
            }
            for (int i = 0; i < array.length; i++) {
                System.out.print(array[i]);
                writer.print(array[i]);
                if ((i != 0) && (i != (array.length-1))) {
                    System.out.print(",");
                    writer.print(",");
                }
                System.out.print(" ");
                writer.print(" ");
            }
            System.out.print("\n");
            writer.print("\n");
        }
        System.out.print("\n");
        writer.close();
    }
    
}
