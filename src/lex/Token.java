/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lex;

/**
 *
 * @author Eliot
 */

/** Valid token recognized by Tokenizer.
 */
public class Token {
    
    /** Type of token. */
    private TokenType tokenType;
    
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
    public String getOpType() {
        return this.lexeme;
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
    
}
