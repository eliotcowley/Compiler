/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import errors.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import lex.*;
import semanticActions.SemanticActions;

/**
 *Parser: The main parser class. Handles the reading in of tokens, pushing them
 * onto the parse stack, and matching them with the parse table.
 * @author elcowley
 */
public class Parser {
    
    private Tokenizer tokenizer;
    private String file;
    private TokenType currentToken;
    private Stack<GrammarSymbol> stack; // Should this be a stack of Tokens or GrammarSymbols?
    private GrammarSymbol predicted;
    private ParseTable parseTable;
    private RHSTable rhsTable;
    private boolean dump;
    private Token token;
    private Token previousToken; // for semantic actions
    
    public SemanticActions semanticActions;
    
    /** public Parser(String filename)
     * Constructor for the Parser class.
     * @param filename String representing file to read in.
     * @throws FileNotFoundException
     * @throws LexicalError 
     */
    public Parser(String filename) throws FileNotFoundException, LexicalError {
        this.stack = new Stack<GrammarSymbol>();
        this.file = filename;
        this.tokenizer = new Tokenizer(this.file);
        this.rhsTable = new RHSTable();
        /* whether to dump the stack or not--turn off for final version */
        this.dump = false; 
        this.parseTable = new ParseTable();
        this.stack.push(TokenType.ENDOFFILE);
        this.stack.push(NonTerminal.Goal);
        this.semanticActions = new SemanticActions();
    }
    
    /** public void parse()
     * The main parse method. Reads in file, pushes symbols onto the parse stack,
     * and tries to match them up in the parse table.
     * @throws CompilerError 
     */
    public void parse() throws CompilerError {
        this.token = this.tokenizer.GetNextToken();
        this.currentToken = this.token.getType();
        previousToken = token;
        /* for debugging */
        //int count = 0; // count how many times parser loops
        while (!stack.isEmpty() && currentToken != TokenType.ENDOFFILE) {
            //count++;
            if (dump) dumpStack();
            predicted = stack.pop(); // get the top symbol off the stack
            if (predicted.isToken()) {
                if (predicted == currentToken) { // matched
                    previousToken = token;
                    token = tokenizer.GetNextToken();
                    currentToken = token.getType(); 
                }
                else { // didn't match
                    /* print error message and continue parsing, pushing back 
                     * the predicted symbol so we don't empty the stack (heuristic
                     * #5, pg. 229: takes the synchronizing set of a token to consist
                     * of all other tokens) */
                    ParserError.tokenMismatch(predicted, currentToken, 
                            tokenizer.charStream.tempLineNumber);
                    panicMode();
                }
            }
            else if (predicted.isNonTerminal()) {
                int column = predicted.getIndex();
                int row = currentToken.getIndex();
                int entry = parseTable.getEntry(row, column);
                /* if we get an error */
                if (entry == 999) {
                    ParserError.parseTable999Error(currentToken,
                            tokenizer.charStream.tempLineNumber);
                    /* attempt to recover from error */
                    panicMode();
                }
                else {
                    /* push symbols in RHS table onto parse stack */
                    GrammarSymbol[] rhsSymbols = rhsTable.getRule(Math.abs(entry));
                    for (int i = rhsSymbols.length-1; i >= 0; i--) {
                        stack.push(rhsSymbols[i]);
                    }
                }
            }
            /* handle semantic actions */
            else if (predicted.isAction()) {
                semanticActions.lineNumber = getLineNumber();
                semanticActions.Execute((SemanticAction) predicted, previousToken);
            }            
        }
        /* for debugging */
//        System.out.println("Loops: " + count);
    }
    
    /** public void dumpStack() 
     * Prints the contents of the parse stack to the console.
     */
    public void dumpStack() {
        System.out.println("Parse Stack:");
        /* print out backwards to reflect actual stack structure */
        for (int j = stack.size()-1; j >= 0; j--) {
            System.out.println(stack.get(j));
        }
        System.out.println("");
    }
    
    /** public void panicMode()
     * Puts Parser into Panic Mode, recovering from errors.
     */ 
    public void panicMode() throws LexicalError {
        /* Heuristic #5 from pg. 229: if current token is a terminal, skip over it */
        if (currentToken.isToken()) {
            currentToken = tokenizer.GetNextToken().getType();
        }
        /* If current token is a nonterminal */
        else {
            /* For nonterminal A, the synchronizing set will consist of all symbols 
            * in FOLLOW(A), all symbols in FIRST(A), and delimiters */
            ArrayList<GrammarSymbol> syncSet = new ArrayList<GrammarSymbol>();
            /* Delimiters */
            syncSet.add(TokenType.SEMICOLON);
            /* Will add FIRST and FOLLOW sets later */
            /* Keep skipping input symbols until we find one in the synchronizing 
             *  set */
            while (!syncSet.contains(currentToken) && (currentToken != TokenType.ENDOFFILE)) {
                int column = predicted.getIndex();
                int row = currentToken.getIndex();
                int entry = parseTable.getEntry(row, column);
                /* Check if nonterminal can generate empty string. Negative table values
                 * indicate empty productions. If nonterminal can generate empty string,
                 * production deriving empty string can be used as default. (Heuristic
                 * #4, pg. 229)
                 */
                if (entry < 0) {
                    /* Use production 4 as default (empty string ::= empty string) */
                    GrammarSymbol[] emptyRule = rhsTable.getRule(4);
                    for (int i = 0; i < emptyRule.length; i++) {
                        stack.push(emptyRule[i]);
                        System.out.println("Symbol produces empty string");
                        currentToken = tokenizer.GetNextToken().getType();
                        break;
                    }
                }
                System.out.println("Skipping token...");
                currentToken = tokenizer.GetNextToken().getType();
            }
            /* Pop symbols off the stack until semicolon grammar symbol is at the top */
            while (predicted != TokenType.SEMICOLON) {
                predicted = stack.pop();
            }
        }
        
    }
    
    public int getLineNumber() {
        return tokenizer.charStream.tempLineNumber;
    }
    
}
