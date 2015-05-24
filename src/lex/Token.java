/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lex;

import static lex.OpValue.GREATERTHAN;
import static lex.OpValue.GREATERTHANOREQUAL;
import static lex.OpValue.LESSTHAN;
import static lex.OpValue.LESSTHANOREQUAL;

/**
 *
 * @author Eliot
 */

/** Valid token recognized by Tokenizer.
 */
public class Token {
    
    /** Type of token. */
    private TokenType tokenType;
    private OpValue opType;
    
    /** String contained in token. */
    public String lexeme;
    
    /** Integer value associated with token. */
    public int value;
    
    /** Default constructor for Token.
     */
    public Token() {
        super();
    }
    
    /** Constructor for Token.
     * 
     * @param tokenType Type of Token.
     */
    public Token(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    /** Return type of token.
     */
    public TokenType getType() {
        return this.tokenType;
    }

    /** Return value of token.
     */
    public String getValue() {
        return this.lexeme;
    }
    
    /** Return operation type of token. */
    public OpValue getOpType() {
        return this.opType;
    }
    
    public void setOpType(OpValue opValue) {
        this.opType = opValue;
    }
    
    /** Set type of token.
     * @param type Type to set token.
     */
    public void setType(TokenType type) {
        this.tokenType = type;
    }
    
    /** Set value of token. 
     * @param value Integer to set as value of token.
     */
    public void setValue(int value) {
        this.value = value;
    }
    
    /** Return integer value of operation (addops, mulops, and relops). */
    public int getOpValue() {
        return this.value;
    }
    
    public String getTVICode() {
        switch (opType) {
            case LESSTHAN: {
                return "blt";
            }
            case LESSTHANOREQUAL: {
                return "ble";
            }
            case GREATERTHAN: {
                return "bgt";
            }
            case GREATERTHANOREQUAL: {
                return "bge";
            }
            case EQUAL: {
                return "beq";
            }
            case NOTEQUAL: {
                return "bne";
            }
            case PLUS: {
                return "add";
            }
            case MINUS: {
                return "sub";
            }
            case MULTIPLICATION: {
                return "mul";
            } 
            case DIVISION: {
                return "/";
            }
            case DIV: {
                return "div";
            }
            case MOD: {
                return "mod";
            }
            case OR: {
                return "or";
            }
            case AND: {
                return "and";
            }
            default: {
                return "null";
            }
        }
    }
    
}
