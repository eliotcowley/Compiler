/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import errors.LexicalError;
import java.io.FileNotFoundException;
import lex.CharStream;

/**
 *
 * @author elcowley
 */
/** The parse table, a matrix indexed by stack symbol (non-terminals from the 
 * grammar) and current input symbol (terminal symbol from the grammar) that 
 * contains in each cell either an integer code representing a production 
 * number, an accept action indicator, or an error indicator.
 */
public class ParseTable {
    
    private int[][] parseTable;
    private StringBuilder sb;
    private String filename;
    private CharStream stream;
    private final int rows = 35; // terminals
    private final int columns = 38; // non-terminals
    
    /** Constructor for ParseTable class. */
    public ParseTable() throws FileNotFoundException, LexicalError {
        this.filename = "parsetable-2const.dat";
        this.stream = new CharStream(filename);
        this.sb = new StringBuilder();
        //this.parseTable = new int[rows][columns];
        init();
    }
    
    /* Previously used the following two methods to read in parse table file using 
     * CharStream class, now obselete since I manually define the table in this class.
     */
    /** Reads a character from the CharStream object and handles any
    * LexicalExceptions thrown.
    *
    * @see LexicalError
    */
//   protected char getChar() throws LexicalError
//   {
//      char ch = (char) CharStream.EOF; 
//      boolean done = false;
//      while (!done) {
//            ch = stream.currentChar();
//            done = true;
//      }
//      return ch;
//   }
    
    /** Initializes ParseTable. */
//    public void init() throws FileNotFoundException, LexicalError {
//        char c = getChar();
////        while (c == CharStream.BLANK) {
////            c = stream.getChar();
////        }
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                while (c != CharStream.BLANK) {
//                    sb.append(c);
//                    c = getChar();
//                }
//                String entryString = sb.toString();
//                int entry = Integer.valueOf(entryString);
//                parseTable[i][j] = entry;
//                sb = new StringBuilder();
//                c = getChar();
//            }
//        }
//    }
   
   /** public void init()
    * Initializes Parse Table as two-dimensional array.
    */
   public void init() {
       parseTable = new int[][] {
        
       {  1, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  65, 999},

       {999, 999,  -6, -16,  25, 999, 999, 999,  -9, 999, 999, 999, 999, 999, 999, 999,  26,  29, 999,  35, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, -28, 999, 999,  33,  37,  39,  41, 999, 999, 999, -47, 999, -51, 999, 999, -54,  60,  62, 999, 999},

       {999, 999,   5, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999},

       {999, 999,  -6,  15, 999, 999, 999, 999,  -9, 999, 999,  17,  18, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999},

       {999, 999,  -6,  15, 999, 999, 999, 999,  -9, 999, 999,  17,  19, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999},

       {999, 999, 999, 999, 999, 999, 999,  10, 999,  12, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999},

       {999, 999, 999, 999, 999, 999, 999,  10, 999,  13, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999},

       {999, 999, 999, 999, 999, 999, 999,  11, 999, 999,  14, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  26,  30, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  39, 999, 999, 999, 999, -47, 999, -51, 999, 999, -54,  60,  62, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  32,  37,  39,  41, 999, 999, 999, -47, 999, -51, 999, 999, -54,  60,  62, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  26,  31, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  39, 999, 999, 999, 999, -47, 999, -51, 999, 999, -54,  60,  62, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  45, 999, 999, 999, 999,  42, 999,  48, 999,  52, 999, 999,  58, 999, 999, 999, 999, 999},

       {999,   2, 999, 999, 999, 999,   7, 999,   8, 999, 999, 999, 999, 999,  22, 999,  26,  29, 999,  34,  45, 999, 999, 999, 999,  42, 999,  48, 999,  52, 999, 999,  55, 999, 999, 999, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  45, 999, 999, 999, 999,  42, 999,  48, 999,  52, 999, 999,  56, 999, 999, 999, 999,  66},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  45, 999, 999, 999, 999,  42, 999,  48, 999,  52, 999, 999,  56, 999, 999, 999, 999,  67},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  39, 999, 999, 999, 999,  46, 999, -51, 999, 999, -54,  60,  62, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  39, 999, 999, 999, 999, 999, 999, 999, 999, 999,  53,  60,  62, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  39, 999, 999, 999, 999, 999, 999,  50, 999, 999, -54,  60,  62, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  36,  39, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999},

       {999, 999, 999, 999, 999,   3, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  39, 999, 999,  43, 999, -47, 999, -51, 999, 999, -54,  60,  62, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, -21, 999,  23, 999, 999,  27, 999, 999,  33,  37,  39,  41, 999, 999, 999, -47, 999, -51, 999, 999, -54,  60,  62, 999, 999},

       {999, 999, 999, 999, 999,  -4, 999, 999, 999, 999, 999, 999, 999, -21, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999},

       {999, 999, 999, 999, 999,  -4, 999, 999, 999, 999, 999, 999, 999, 999, 999, -24, 999, 999, 999, 999, 999, 999, 999,  39, 999, 999, -44, 999, -47, 999, -51, 999, 999, -54,  60,  62, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  20, 999, 999, 999, 999, 999, 999,  45, 999,  37, 999,  40,  42, 999,  48, 999,  52, 999, 999,  57, 999,  59,  61, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  39, 999, 999, 999, 999, -47, 999, -51, 999, 999, -54,  60,  62, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  36,  38, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  60, 999, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  45, 999, 999, 999, 999,  42, 999,  49, 999, 999, 999,  64, 999, 999, 999, 999, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999,  45, 999, 999, 999, 999,  42, 999,  49, 999, 999, 999,  63, 999, 999, 999, 999, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999},

       {999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999, 999}
       };
   }
    
    /** Return entry in ParseTable for given terminal and nonterminal. */
    public int getEntry(int row, int column) {
        return parseTable[row][column];
    }
    
    /** Prints the ParseTable. */
    public void dumpTable() {
       for (int i = 0; i < rows; i++) {
          for (int j = 0; j < columns; j++) {
              System.out.print(getEntry(i, j));
              if (j < columns-1) {
                  System.out.print(" ");
              }
              else {
                  System.out.print("\n");
              }
          }
       }
    }
    
    /** Prints entry in ParseTable for given terminal and nonterminal. */
    public void printEntry(int row, int column) {
        System.out.println(getEntry(row, column));
    }
}
