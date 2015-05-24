/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drivers;

import errors.CompilerError;
import errors.LexicalError;
import java.io.FileNotFoundException;
import lex.Token;
import lex.TokenType;
import lex.Tokenizer;
import parser.Parser;
import semanticActions.SemanticActions;

/**
 *
 * @author elcowley
 */
public class SemanticActionsDriver {
    
    private SemanticActions semanticActions;
    
    public SemanticActionsDriver() {
        this.semanticActions = new SemanticActions();
    }
    
    public void run(String filename) {
        
    }
    
    public static void main(String[] args) {
        try {
            Parser parseTest = new Parser("simpleTest.dat");
            try {
                parseTest.parse();
            } catch (CompilerError ex) {
                ex.printStackTrace();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (LexicalError ex) {
            ex.printStackTrace();
        }
//        SemanticActionsDriver test = new SemanticActionsDriver();
//        test.run(null);
    }
    
}
