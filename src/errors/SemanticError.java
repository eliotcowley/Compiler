/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package errors;

/**
 *
 * @author elcowley
 */
public class SemanticError extends CompilerError {
    
    private String s;
    
    public SemanticError(String s) {
        this.s = s;
        System.out.println(this.s);
    }
    
}
