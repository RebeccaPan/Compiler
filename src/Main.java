import AST.*;
import IR.IRBlockList;
import IR.IRBuilder;
import Optimize.AST.ConstantBroadcast;
import Optimize.IR.ADCE;
import Optimize.IR.ConvertImm;
import Optimize.IR.SSA;
import Optimize.optimization;
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
        boolean debugInfo = false;
//        change relative address here:
//        String filename = "./testcase-2021/optim-new/efficiency.mx";
//        String filename = "./testcase/codegen/t40.mx";
//        String filename = "./testcase/codegen/shortest_path/spfa.mx";
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
            // semantic
            if (semantic) {
                ProgramNode ast = BuildAST(inFile);
                IRBlockList blockList = new IRBlockList();
                new SemanticChecker(blockList).visit(ast);
            } else if (codegen) {
                ProgramNode ast = BuildAST(inFile);
                IRBlockList blockList = new IRBlockList();
                GlobalScope globalScope = new GlobalScope();
                new SemanticChecker(blockList).visit(ast);

                // AST optimize
                ConstantBroadcast optAST = new ConstantBroadcast(blockList);
                optAST.programNode = ast; optAST.opt();

                new IRBuilder(globalScope, blockList).visit(ast);
//                blockList.initASM();

                blockList.jumpUpdate();
                blockList.labelOpt();
                if (debugInfo) { System.out.println("---0"); blockList.print(); }
                new ConvertImm(blockList).opt();
                if (debugInfo) { System.out.println("---0.5"); blockList.print(); }
                new SSA(blockList).opt();
                if (debugInfo) { System.out.println("---1"); blockList.print(); }
                blockList.fulfill();
                if (debugInfo) { System.out.println("---1.5"); blockList.print(); }
                new ADCE(blockList).opt();
                if (debugInfo) { System.out.println("---2"); blockList.print(); }
//                blockList.allocate();
                blockList.graphColor();
                if (debugInfo) { System.out.println("---3"); blockList.print(); }
                blockList.fulfillLocal();
                if (debugInfo) { System.out.println("---4"); blockList.print(); }
                blockList.allocateLocal();
                if (debugInfo) { System.out.println("---5"); blockList.print(); }
                blockList.trim();
                if (debugInfo) { System.out.println("---6"); blockList.print(); }
                blockList.calcRAM();

//                if (debugInfo) System.out.println("---ASM-OPT");
                // ASM optimize
//                new optimization(blockList).opt();
//                if (debugInfo) System.out.println("---ASM-#");
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
