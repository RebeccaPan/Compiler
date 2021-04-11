import AST.*;
import IR.IRBlockList;
import IR.IRBuilder;
import Util.Scope.GlobalScope;
import frontend.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import parser.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class Main {
    public static void main(String[] args) throws Exception {
//        change relative address here:
//        String filename = "./testcase/codegen/sorting/merge_sort.mx";
//        String filename = "./testcase/codegen/shortest_path/dijkstra.mx";
//        String filename = "./testcase/codegen/t18.mx";
        try {
            boolean semantic = false, codegen = false;
//            InputStream inFile = new FileInputStream(filename);
            InputStream inFile = System.in;
            File outFile = new File("output.s");
            PrintStream stream = new PrintStream(outFile);
            System.setOut(stream);
            for (String arg : args) {
                if (arg.equals("-semantic")) semantic = true;
                if (arg.equals("-codegen")) codegen = true;
            }
            ProgramNode ast = BuildAST(inFile);
            IRBlockList blockList = new IRBlockList();
            GlobalScope globalScope = new GlobalScope();
            new SemanticChecker(blockList).visit(ast);

            if (codegen) {
                new IRBuilder(globalScope, blockList).visit(ast);
                blockList.initASM();
                blockList.printASM();
            }
        } catch (Exception err) {
            err.printStackTrace();
            System.err.println(err.getMessage());
            throw new RuntimeException();
        }
    }
    public static ProgramNode BuildAST(InputStream _file) throws Exception {
        MxLexer lexer = new MxLexer(CharStreams.fromStream(_file));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new MxErrorListener());
        MxParser parser = new MxParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(new MxErrorListener());
        return (ProgramNode) new ASTBuilder().visit(parser.program());
    }
}
