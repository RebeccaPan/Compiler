import AST.*;
import frontend.ASTBuilder;
import frontend.SemanticChecker;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import parser.*;

import java.io.FileInputStream;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        // change relative address here:
        String filename = "./testcase/sema/basic-package/basic-3.mx";
        try {
            InputStream file = new FileInputStream(filename);
            ProgramNode ast = BuildAST(file);
            new SemanticChecker().visit(ast);
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