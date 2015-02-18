/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lex;

/**
 *
 * @author Eliot
 */
public enum OpValue implements GrammarSymbol {
    PLUS(1), MINUS(2), OR(3), EQUAL(1), NOTEQUAL(2), LESSTHAN(3), GREATERTHAN(4),
    LESSTHANOREQUAL(5), GREATERTHANOREQUAL(5), MULTIPLICATION(1), DIVISION(2), DIV(3),
    MOD(4), AND(5);
    
    private int n;
    
    private OpValue(int i) {
        this.n = i;
    }
    
    @Override
    public int getIndex() {
        return this.n;
    }
    
    @Override
    public boolean isToken() {
        return true;
    }
}
