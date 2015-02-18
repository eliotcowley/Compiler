/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import drivers.TokenizerDriver;
import errors.LexicalError;

/**
 *
 * @author Eliot
 */
public class Compiler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws LexicalError {
        // TODO code application logic here
        TokenizerDriver td = new TokenizerDriver();
        td.run();
    }
}
