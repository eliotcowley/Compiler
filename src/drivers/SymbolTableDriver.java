package drivers;

import errors.LexicalError;
import lex.*;
import symbolTable.*;


public class SymbolTableDriver {
    
    private SymbolTable KeywordTable;

    public SymbolTableDriver() {
        super();
    }

    protected void run(String filename) {
        KeywordTable = new SymbolTable(17);
        SymbolTable GlobalTable = new SymbolTable(37);
        SymbolTable ConstantTable = new SymbolTable(37);

        GlobalTable.insert(new FunctionEntry("read"));
        GlobalTable.insert(new FunctionEntry("write"));
        GlobalTable.insert(new FunctionEntry("main"));

        // Develop and use a routine to fill the KeywordTable, if appropriate
        fillOutKeywordTable();
        
        Tokenizer tokenizer =
                new Tokenizer(filename);

        Token token;

        try {
            token = tokenizer.GetNextToken();

            while (!(token.getType() == TokenType.ENDOFFILE)) {

                if ((token.getType() == TokenType.INTCONSTANT) || (token.getType() == TokenType.REALCONSTANT)) {
                    // If the token is a constant, add it to constantTable
                    ConstantTable.insert(new ConstantEntry(token.getValue(), token.getType()));
                } else if (token.getType() == TokenType.IDENTIFIER) {

                    //  If it is an identifier add it to Global table
                    // as a variable entry
                    GlobalTable.insert(new VariableEntry(token.getValue(), token.getType()));

                }
                token = tokenizer.GetNextToken();
            }
        } catch (LexicalError ex) {
            System.err.println(ex);
        }

        System.out.println("Dumping KeywordTable...");
	KeywordTable.dumpTable();
        System.out.println("Dumping GlobalTable...");
        GlobalTable.dumpTable();
        System.out.println("Dumping ConstantTable...");
        ConstantTable.dumpTable();
    }
    
    /** Fills out keyword table with keywords from language, such as 
     * "program", "begin", and "end".
     */
    private void fillOutKeywordTable() {
        KeywordTable.insert(new KeywordEntry("program", TokenType.PROGRAM));
        KeywordTable.insert(new KeywordEntry("begin", TokenType.BEGIN));
        KeywordTable.insert(new KeywordEntry("end", TokenType.END));
        KeywordTable.insert(new KeywordEntry("var", TokenType.VAR));
        KeywordTable.insert(new KeywordEntry("function", 
                TokenType.FUNCTION));
        KeywordTable.insert(new KeywordEntry("procedure", 
                TokenType.PROCEDURE));
        KeywordTable.insert(new KeywordEntry("result", TokenType.RESULT));
        KeywordTable.insert(new KeywordEntry("integer", TokenType.INTEGER));
        KeywordTable.insert(new KeywordEntry("real", TokenType.REAL));
        KeywordTable.insert(new KeywordEntry("array", TokenType.ARRAY));
        KeywordTable.insert(new KeywordEntry("of", TokenType.OF));
        KeywordTable.insert(new KeywordEntry("if", TokenType.IF));
        KeywordTable.insert(new KeywordEntry("then", TokenType.THEN));
        KeywordTable.insert(new KeywordEntry("else", TokenType.ELSE));
        KeywordTable.insert(new KeywordEntry("while", TokenType.WHILE));
        KeywordTable.insert(new KeywordEntry("do", TokenType.DO));
        KeywordTable.insert(new KeywordEntry("not", TokenType.NOT));
    }


    public static void main(String[] args) {
        SymbolTableDriver test = new SymbolTableDriver();
        test.run("symtabtest.dat");
    }


}