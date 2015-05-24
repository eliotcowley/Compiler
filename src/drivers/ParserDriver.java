package drivers;

import parser.*;
import errors.*;
import java.io.FileNotFoundException;

public class ParserDriver {
	
	Parser parser;
	
	public ParserDriver(String filename) throws FileNotFoundException, LexicalError
	{
		parser = new Parser(filename);
	}

        /* Changed method to public so it can be run from Compiler class */
	/*protected*/ public void run()   
	{
		try
		{
			parser.parse();
		}
		
		catch (CompilerError ex)
		{
			System.out.println(ex);
		}
		parser.semanticActions.quads.print();
		System.out.println("Compilation successful.");
	}


	public static void main(String[] args) throws LexicalError, FileNotFoundException
	{
            ParserDriver test = new ParserDriver("expressionTest.dat");
            test.run();
	}
}