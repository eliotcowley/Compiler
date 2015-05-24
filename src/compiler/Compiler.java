package compiler;

import drivers.ParserDriver;
import drivers.SymbolTableDriver;
import errors.LexicalError;
import java.io.FileNotFoundException;

/**
 *
 * @author Eliot
 */
public class Compiler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws LexicalError, FileNotFoundException {
//        ParserDriver pd = new ParserDriver("simpleTest.pas");
//        ParserDriver pd = new ParserDriver("expressionTest.pas");
//        ParserDriver pd = new ParserDriver("arrayTest.pas");
//        ParserDriver pd = new ParserDriver("arrayRefTest.pas");
//        ParserDriver pd = new ParserDriver("ifTest.pas");
//        ParserDriver pd = new ParserDriver("procTest.pas");
//        ParserDriver pd = new ParserDriver("funcTest.pas");
//        ParserDriver pd = new ParserDriver("fibTest.pas");
//        ParserDriver pd = new ParserDriver("uminusTest.pas");
//        ParserDriver pd = new ParserDriver("noparmTest.pas");
//        ParserDriver pd = new ParserDriver("recursionTest.pas");
//        ParserDriver pd = new ParserDriver("theUltimateTest.pas");
//        ParserDriver pd = new ParserDriver("theUltimateTestFail.pas");
        ParserDriver pd = new ParserDriver("ult-corrected.pas");
        pd.run();
    }
}
