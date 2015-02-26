package drivers;

import errors.LexicalError;
import lex.*;


public class TokenizerDriver
{

	public TokenizerDriver()
	{
		super();
	}

    /** Initializes and runs tokenizer.
     * @throws LexicalError
     */
    public void run() throws LexicalError /* change this function to call 
         * GetNextToken until all tokens are read */
	{
		Tokenizer tokenizer;  
                //tokenizer = new Tokenizer("test_int_constant.txt"); 
                tokenizer = new Tokenizer("lextest.dat");

		Token token = new Token();

		/*try {
                    token = tokenizer.GetNextToken();
                }
                catch(LexicalError ex) {
                    System.out.println(ex.string);
                }*/
                
                

		while (!(token.getType() == TokenType.ENDOFFILE))
		{
                    try {
                        token = tokenizer.GetNextToken();
			System.out.print("Recognized Token:  " + token.getType());
			if ((token.getType() == TokenType.IDENTIFIER) 
                         || (token.getType() == TokenType.REALCONSTANT) 
			 || (token.getType() == TokenType.INTCONSTANT) )
                            System.out.print("  Value: " + token.getValue());
			else if ((token.getType() == TokenType.RELOP)
				|| (token.getType() == TokenType.ADDOP) 
                                || (token.getType() == TokenType.MULOP)) {
                            System.out.print("  OpType: " + token.getOpType());
                            System.out.print("  OpValue: " + token.getOpValue());
                        }   
			System.out.println();
                    }
                        catch(LexicalError ex) {
                            System.out.println(ex.string);
                        }
                        //System.out.println("Got here tooo1");
                    }
	}


	public static void main(String[] args) throws LexicalError
	{
		TokenizerDriver test = new TokenizerDriver();
		test.run();
	}
}
