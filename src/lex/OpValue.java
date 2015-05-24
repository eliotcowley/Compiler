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
    PLUS(0), MINUS(1), OR(2), EQUAL(3), NOTEQUAL(4), LESSTHAN(5), GREATERTHAN(6),
    LESSTHANOREQUAL(7), GREATERTHANOREQUAL(8), MULTIPLICATION(9), DIVISION(10), DIV(11),
    MOD(12), AND(13);
    
    private int n;
    private int index;
    
    private OpValue(int i) {
        this.n = i;
        switch (n) {
            case 0: 
                index = 1;
                break;
            case 1: 
                index = 2;
                break;
            case 2:
                index = 3;
                break;
            case 3:
                index = 1;
                break;
            case 4:
                index = 2;
                break;
            case 5:
                index = 3;
                break;
            case 6:
                index = 4;
                break;
            case 7:
                index = 5;
                break;
            case 8:
                index = 5;
                break;
            case 9:
                index = 1;
                break;
            case 10:
                index = 2;
                break;
            case 11:
                index = 3;
                break;
            case 12:
                index = 4;
                break;
            case 13:
                index = 5;
                break;
        }
    }
    
    @Override
    public int getIndex() {
        return this.index;
    }
    
    @Override
    public boolean isToken() {
        return true;
    }
    
    @Override
    public boolean isNonTerminal() {
        return false;
    }
    
    @Override
    public boolean isAction() {
        return false;
    }
    
}
