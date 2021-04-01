package IR;

import AST.*;
import Util.CompilationError;
import Util.Scope.*;
import Util.Type.*;

import java.util.ArrayList;

import static Util.Constants.*;

public class IRBuilder implements ASTVisitor {
    private GlobalScope globalScope;
    private Type curClass;

    // the whole global BlockList actually
    private IRBlockList curBlockList;
    private IRBlock curBlock;

    private int loopStart, loopEnd, loopNext;
    private int condEnd, condFalse;
    private int labelNum;

    private ArrayList<ASTNode> globalVarDefList = new ArrayList<>();

    public IRBuilder(GlobalScope _globalScope, IRBlockList _curBlockList) {
        globalScope = _globalScope;
        curBlockList = _curBlockList;
        loopStart = loopEnd = loopNext = 0;
        condEnd = condFalse = 0;
        labelNum = 0;
//        curBlock = new IRBlock("globalBlock", globalScope.getRegIDAllocator(), ++labelNum);
    }

    @Override
    public void visit(BoolLiteralNode node) {
        node.setReg(curBlock.regIDAllocator.allocate(5));
        IRLine line = new IRLine(IRLine.OPCODE.LOAD);
        line.addReg(node.getReg());
        line.addReg(node.getVal() ? CONST_ONE : CONST_ZERO);
        curBlock.addLine(line);
    }

    @Override
    public void visit(IntLiteralNode node) {
        node.setReg(curBlock.regIDAllocator.allocate(5));
        IRLine line = new IRLine(IRLine.OPCODE.LOAD);
        line.addReg(node.getReg());
        line.addReg(new IRReg(node.getVal(), 8, false));
        curBlock.addLine(line);
    }

    @Override
    public void visit(StrLiteralNode node) {
        node.setReg(curBlock.regIDAllocator.allocate(5));
        IRLine line = new IRLine(IRLine.OPCODE.LOADSTRING);
        line.addReg(node.getReg());
        line.addReg(new IRReg(curBlockList.addStr(node.getVal()), 9, false));
        curBlock.addLine(line);
    }

    @Override
    public void visit(NullLiteralNode node) {
        // do nothing
    }

    @Override
    public void visit(ProgramNode node) {
        for (DefNode defNode : node.getDefNodeList()) {
            if (defNode instanceof VarDefNode) globalVarDefList.add(defNode); // accept later in main func
            else defNode.accept(this);
        }
    }

    @Override
    public void visit(DefNode node) {
        throw new CompilationError("IRBuilder - Unreachable in visit(DefNode)");
    }

    @Override
    public void visit(ClassDefNode node) {
        curClass = new ClassType(node.getClassID());
        node.getFuncDefList().forEach(x -> x.accept(this));
        node.getConstructorDefList().forEach(x -> x.accept(this));
//        curBlock = new IRBlock(node.getClassID(), node.getScope().getRegIDAllocator(), ++labelNum);
        node.getVarDefList().forEach(x -> x.accept(this));
        curClass = null;
    }

    @Override
    public void visit(FuncDefNode node) {
        curBlock = new IRBlock(node.getFuncID(), node.getScope().getRegIDAllocator(), ++labelNum);
        curBlockList.addBlock(curBlock);
        IRLine line = new IRLine(IRLine.OPCODE.FUNC);
        line.setFuncStr(node.getFuncID());
        curBlock.addLine(line);

        if (node.getParaList() != null) node.getParaList().accept(this);

        if (node.getFuncID().equals("main")) globalVarDefList.forEach(x -> x.accept(this));

        if (curClass != null) { // in class
            IRReg reg = new IRReg(0, 1, false);
            line = new IRLine(IRLine.OPCODE.MOVE);
            line.addReg(reg);
            line.addReg(new IRReg(10, 0, false));
            curBlock.addLine(line);

            for (int i = 0; i < node.getParaList().getParaList().size(); ++i) {
                line = new IRLine(IRLine.OPCODE.MOVE);
                line.addReg(node.getParaList().getParaList().get(i).getReg());
                if (i < 5) line.addReg(new IRReg(i + 11, 0, false));
                else line.addReg(new IRReg(i - 5, 4, false));
                curBlock.addLine(line);
            }
            node.getSuite().accept(this);
        } else { // not in class
            if (node.getParaList() != null) {
                for (int i = 0; i < node.getParaList().getParaList().size(); ++i) {
                    line = new IRLine(IRLine.OPCODE.MOVE);
                    line.addReg(node.getParaList().getParaList().get(i).getReg());
                    if (i < 6) line.addReg(new IRReg(i + 10, 0, false));
                    else line.addReg(new IRReg(i - 6, 4, false));
                    curBlock.addLine(line);
                }
            }
            node.getSuite().accept(this);
            if (node.getFuncID().equals("main") && curBlockList.mainNeedRet) {
                // add `return 0` to main func
                line = new IRLine(IRLine.OPCODE.MOVE);
                line.addReg(new IRReg(10, 0, false));
                line.addReg(CONST_NULL);
                curBlock.addLine(line);
                line = new IRLine(IRLine.OPCODE.JUMP);
                line.setLabel(curBlock.getRetLabel());
                curBlock.addLine(line);
            }
        }
    }

    @Override
    public void visit(ConstructorDefNode node) {
        // almost the same as FuncDefNode
        curBlock = new IRBlock(node.getFuncID(), node.getScope().getRegIDAllocator(), ++labelNum);
        curBlockList.addBlock(curBlock);
        IRLine line = new IRLine(IRLine.OPCODE.FUNC);
        line.setFuncStr(node.getFuncID());
        curBlock.addLine(line);

        if (node.getParaList() != null) node.getParaList().accept(this);

        if (curClass != null) { // in class
            IRReg reg = new IRReg(0, 1, false);
            line = new IRLine(IRLine.OPCODE.MOVE);
            line.addReg(reg);
            line.addReg(new IRReg(10, 0, false));
            curBlock.addLine(line);

            for (int i = 0; i < node.getParaList().getParaList().size(); ++i) {
                line = new IRLine(IRLine.OPCODE.MOVE);
                line.addReg(node.getParaList().getParaList().get(i).getReg());
                if (i < 5) line.addReg(new IRReg(i + 11, 0, false));
                else line.addReg(new IRReg(i - 5, 4, false));
                curBlock.addLine(line);
            }
            node.getSuite().accept(this);
        } else {
            throw new CompilationError("IRBuilder - constructor not in class");
        }
    }

    @Override
    public void visit(VarDefNode node) {
        node.getSimpleVarDefList().forEach(x -> x.accept(this));
    }

    @Override
    public void visit(VarDefStmtNode node) {
        node.getVarDefNode().accept(this);
    }

    @Override
    public void visit(TypeNode node) {
//        System.out.println("IRBuilder - in visit(TypeNode)");
    }

    @Override
    public void visit(SimpleTypeNode node) {
//        System.out.println("IRBuilder - in visit(SimpleTypeNode)");
    }

    @Override
    public void visit(ParaListNode node) {
        node.getParaList().forEach(x -> x.accept(this));
    }

    @Override
    public void visit(ParaNode node) {
        // allocated in Semantic Checker when adding the new VarSymbol
        // node.setReg(curBlock.regIDAllocator.allocate(1));
        node.setReg(node.getVarSymbol().getReg());
    }

    @Override
    public void visit(ExprListNode node) {
        node.getExprList().forEach(x -> x.accept(this));
    }

    private IRReg malloc(NewExprNode node, int i) {
        if (i < node.getDimExprList().size()) {
            IRReg iter = curBlock.regIDAllocator.allocate(1);
            IRReg reg  = curBlock.regIDAllocator.allocate(1);
            IRReg temp = curBlock.regIDAllocator.allocate(5);
            IRLine line = new IRLine(IRLine.OPCODE.MOVE);
            line.addReg(iter);
            line.addReg(node.getDimExprList().get(i).getReg());
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.MOVE);
            line.addReg(temp);
            line.addReg(node.getDimExprList().get(i).getReg());
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.MOVE);
            line.addReg(new IRReg(10, 0, false));
            line.addReg(temp);
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.CALL);
            line.setFuncStr("my_array_alloc");
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.MOVE);
            line.addReg(reg);
            line.addReg(new IRReg(10, 0, false));
            curBlock.addLine(line);

            int iterStart = ++labelNum, iterEnd = ++labelNum;
            line = new IRLine(IRLine.OPCODE.LABEL);
            line.setLabel(iterStart);
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.BNEQ);
            line.addReg(iter);
            line.addReg(CONST_NULL);
            line.setLabel(iterEnd);
            curBlock.addLine(line);

            IRReg next_result = malloc(node, i + 1);
            IRReg result = curBlock.regIDAllocator.allocate(5);
            line = new IRLine(IRLine.OPCODE.INDEX);
            line.addReg(result);
            line.addReg(reg);
            line.addReg(iter);
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.MOVE);
            line.addReg(new IRReg(result.getID(), result.getType(), true));
            line.addReg(next_result);
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.ADDI);
            line.addReg(iter);
            line.addReg(iter);
            line.addReg(CONST_MINUS_ONE);
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.JUMP);
            line.setLabel(iterStart);
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.LABEL);
            line.setLabel(iterEnd);
            curBlock.addLine(line);
            return reg;
        } else {
            if (node.getType() instanceof ClassType) {
                IRLine line = new IRLine(IRLine.OPCODE.LOAD);
                line.addReg(new IRReg(10, 0, false));
                line.addReg(new IRReg(curBlock.regIDAllocator.size(8) + 1, 8, false));
                curBlock.addLine(line);

                line = new IRLine(IRLine.OPCODE.CALL);
                line.setFuncStr("malloc");
                curBlock.addLine(line);

                IRReg reg = curBlock.regIDAllocator.allocate(5);
                line = new IRLine(IRLine.OPCODE.MOVE);
                line.addReg(reg);
                line.addReg(new IRReg(10, 0, false));
                curBlock.addLine(line);

                ScopeType curScope = globalScope;
                if (curScope.existFuncLocal(node.getType().getType())) {
                    line = new IRLine(IRLine.OPCODE.CALL);
                    line.setFuncStr("my_c_" + node.getType().getType() + "_" + node.getType().getType());
                    curBlock.addLine(line);
                }
                return reg;
            } else {
                return CONST_NULL;
            }
        }
    }

    @Override
    public void visit(NewExprNode node) {
        node.getDimExprList().forEach(x -> x.accept(this));
        node.setReg(malloc(node, 0));
    }

    @Override
    public void visit(SuiteNode node) {
        for (StmtNode curStmtNode : node.getStmtNodeList()) {
            curStmtNode.accept(this);
        }
    }

    @Override
    public void visit(ExprStmtNode node) {
        node.getExprNode().accept(this);
    }

    @Override
    public void visit(IfStmtNode node) {
        int _condFalse = condFalse, _condEnd = condEnd;
        condEnd = ++labelNum;
        if (node.getFalseSuite().getStmtNodeList().size() > 0) condFalse = ++labelNum;

        node.getCond().accept(this);
        IRLine line = new IRLine(IRLine.OPCODE.BNEQ);
        line.addReg(node.getCond().getReg());
        line.addReg(CONST_NULL);
        line.setLabel( (node.getFalseSuite().getStmtNodeList().size() > 0) ? condFalse : condEnd);
        curBlock.addLine(line);

        if (node.getTrueSuite().getStmtNodeList().size() > 0) {
            node.getTrueSuite().accept(this);
            if (node.getFalseSuite().getStmtNodeList().size() > 0) {
                line = new IRLine(IRLine.OPCODE.JUMP);
                line.setLabel(condEnd);
                curBlock.addLine(line);
            }
        }

        if (node.getFalseSuite().getStmtNodeList().size() > 0) {
            line = new IRLine(IRLine.OPCODE.LABEL);
            line.setLabel(condFalse);
            curBlock.addLine(line);
            node.getFalseSuite().accept(this);
        }

        line = new IRLine(IRLine.OPCODE.LABEL);
        line.setLabel(condEnd);
        curBlock.addLine(line);
        condFalse = _condFalse; condEnd = _condEnd;
    }

    @Override
    public void visit(ForStmtNode node) {
        int _loopStart, _loopEnd, _loopNext;
        _loopStart = loopStart; _loopEnd = loopEnd; _loopNext = loopNext;
        loopStart = ++labelNum;
        loopEnd = ++labelNum;
        loopNext = ++labelNum;

        if (node.getInit() != null) {
            node.getInit().accept(this);
        }

        IRLine line = new IRLine(IRLine.OPCODE.LABEL);
        line.setLabel(loopStart);
        curBlock.addLine(line);

        if (node.getCond() != null) {
            node.getCond().accept(this);
            line = new IRLine(IRLine.OPCODE.BNEQ);
            line.addReg(node.getCond().getReg());
            line.addReg(CONST_NULL);
            line.setLabel(loopEnd);
            curBlock.addLine(line);
        }

        if (node.getSuite().getStmtNodeList().size() > 0) {
            node.getSuite().accept(this);
        }

        line = new IRLine(IRLine.OPCODE.LABEL);
        line.setLabel(loopNext);
        curBlock.addLine(line);

        if (node.getStep() != null) {
            node.getStep().accept(this);
        }

        line = new IRLine(IRLine.OPCODE.JUMP);
        line.setLabel(loopStart);
        curBlock.addLine(line);
        line = new IRLine(IRLine.OPCODE.LABEL);
        line.setLabel(loopEnd);
        curBlock.addLine(line);
        loopStart = _loopStart; loopEnd = _loopEnd; loopNext = _loopNext;
    }

    @Override
    public void visit(WhileStmtNode node) {
        int _loopStart, _loopEnd, _loopNext;
        _loopStart = loopStart; _loopEnd = loopEnd; _loopNext = loopNext;
        loopNext = loopStart = ++labelNum;
        loopEnd = ++labelNum;
        IRLine line = new IRLine(IRLine.OPCODE.LABEL);
        line.setLabel(loopStart);
        curBlock.addLine(line);
        if (node.getExpr() != null) {
            node.getExpr().accept(this);
            line = new IRLine(IRLine.OPCODE.BNEQ);
            line.addReg(node.getExpr().getReg());
            line.addReg(CONST_NULL);
            line.setLabel(loopEnd);
            curBlock.addLine(line);
        }
        node.getSuite().accept(this);
        line = new IRLine(IRLine.OPCODE.JUMP);
        line.setLabel(loopStart);
        curBlock.addLine(line);
        line = new IRLine(IRLine.OPCODE.LABEL);
        line.setLabel(loopEnd);
        curBlock.addLine(line);
        loopStart = _loopStart; loopEnd = _loopEnd; loopNext = _loopNext;
    }

    @Override
    public void visit(BreakNode node) {
        IRLine line = new IRLine(IRLine.OPCODE.JUMP);
        line.setLabel(loopEnd);
        curBlock.addLine(line);
    }

    @Override
    public void visit(ContinueNode node) {
        IRLine line = new IRLine(IRLine.OPCODE.JUMP);
        line.setLabel(loopNext);
        curBlock.addLine(line);
    }

    @Override
    public void visit(ReturnNode node) {
        if (node.getRetExpr() != null) {
            node.getRetExpr().accept(this);
            IRLine line = new IRLine(IRLine.OPCODE.MOVE);
            line.getRegList().add(new IRReg(10, 0, false));
            line.getRegList().add(node.getRetExpr().getReg());
            curBlock.addLine(line);
        }
        IRLine line = new IRLine(IRLine.OPCODE.JUMP);
        line.setLabel(curBlock.getRetLabel());
        curBlock.addLine(line);
    }

    @Override
    public void visit(PrefixExprNode node) {
        node.getExpr().accept(this);
        String op = node.getOp();
        switch (op) {
            case "++", "--" -> { // ++i --i
                node.setReg(node.getExpr().getReg());
                IRLine line = new IRLine(IRLine.OPCODE.ADDI);
                line.addReg(node.getReg());
                line.addReg(node.getReg());
                line.addReg(op.equals("++") ? CONST_ONE : CONST_MINUS_ONE);
                curBlock.addLine(line);
            }
            case "+" -> node.setReg(node.getExpr().getReg());
            case "-" -> {
                node.setReg(curBlock.regIDAllocator.allocate(5));
                IRLine line = new IRLine(IRLine.OPCODE.NEG);
                line.addReg(node.getReg());
                line.addReg(node.getExpr().getReg());
                curBlock.addLine(line);
            }
            case "!", "~" -> {
                node.setReg(curBlock.regIDAllocator.allocate(5));
                IRLine line = new IRLine( op.equals("!") ?
                        IRLine.OPCODE.LOGICNOT : IRLine.OPCODE.NOT);
                line.addReg(node.getReg());
                line.addReg(node.getExpr().getReg());
                curBlock.addLine(line);
            }
            default -> throw new CompilationError("IRBuilder - PrefixExprOp op not found");
        }
    }

    @Override
    public void visit(PostfixExprNode node) {
        node.getExpr().accept(this);
        String op = node.getOp();
        node.setReg(curBlock.regIDAllocator.allocate(5));

        IRLine line = new IRLine(IRLine.OPCODE.MOVE);
        line.addReg(node.getReg());
        line.addReg(node.getExpr().getReg());
        curBlock.addLine(line);

        line = new IRLine(IRLine.OPCODE.ADDI);
        line.addReg(node.getExpr().getReg());
        line.addReg(node.getExpr().getReg());
        line.addReg(op.equals("++") ? CONST_ONE : CONST_MINUS_ONE);
        curBlock.addLine(line);
    }

    @Override
    public void visit(ClassMemberExprNode node) { // expr.ID
        ExprNode expr = node.getExpr();
        IDNode ID = node.getID(); // notice: ID is NOT a String
        expr.accept(this);
        ID.setParentReg(expr.getReg());
        ID.accept(this);
        if (ID.isInClass()) {
            IRReg reg = curBlock.regIDAllocator.allocate(5);
            IRLine line = new IRLine(IRLine.OPCODE.INDEX);
            line.addReg(reg);
            line.addReg(expr.getReg());
            line.addReg(ID.getReg());
            curBlock.addLine(line);
            node.setReg(new IRReg(reg.getID(), reg.getType(), true));
        } else {
            node.setReg(expr.getReg());
        }
    }

    @Override
    public void visit(CallFuncExprNode node) {
        if (node.getExprList() != null) node.getExprList().accept(this);
        boolean inClass = node.getFuncSymbol().isInClass();
        if (node.getExprList() != null) {
            for (int i = node.getExprList().getExprList().size() - 1; i >= 0; --i) {
                IRLine line = new IRLine(IRLine.OPCODE.MOVE);
                line.addReg(new IRReg(i + ((inClass) ? 1 : 0), 3, false));
                line.addReg(node.getExprList().getExprList().get(i).getReg());
                curBlock.addLine(line);
            }
        }
        if (inClass) {
            IRLine line = new IRLine(IRLine.OPCODE.MOVE);
            line.addReg(new IRReg(0, 3, false));
            if (node.getFuncSymbol().getID().equals("my_array_size")) {
                ( (ClassMemberExprNode) node.getExpr() ).getExpr().accept(this);
                node.getExpr().setReg(( (ClassMemberExprNode) node.getExpr() ).getExpr().getReg());
                line.addReg(node.getExpr().getReg());
            } else {
                line.addReg(node.getParentReg() != null
                        ? node.getExpr().getReg()
                        : new IRReg(0, 1, false));
            }
            curBlock.addLine(line);
        }
        IRLine line = new IRLine(IRLine.OPCODE.CALL);
        line.setFuncStr(node.getFuncSymbol().getID());
        curBlock.addLine(line);

        node.setReg(curBlock.regIDAllocator.allocate(5));
        line = new IRLine(IRLine.OPCODE.MOVE);
        line.addReg(node.getReg());
        line.addReg(new IRReg(10, 0, false));
        curBlock.addLine(line);
    }

    @Override
    public void visit(SubscriptExprNode node) {
        node.getArr().accept(this);
        node.getIndex().accept(this);
        IRReg temp1 = curBlock.regIDAllocator.allocate(5);

        IRLine line = new IRLine(IRLine.OPCODE.ADDI);
        line.addReg(temp1);
        line.addReg(node.getIndex().getReg());
        line.addReg(CONST_ONE);
        curBlock.addLine(line);

        IRReg temp2 = curBlock.regIDAllocator.allocate(5);
        line = new IRLine(IRLine.OPCODE.INDEX);
        line.addReg(temp2);
        line.addReg(node.getArr().getReg());
        line.addReg(temp1);
        curBlock.addLine(line);

        node.setReg(new IRReg(temp2.getID(), temp2.getType(), true));
    }

    @Override
    public void visit(BinaryExprNode node) {
        node.setReg(curBlock.regIDAllocator.allocate(5));
        String op = node.getOp();

        if (op.equals("&&") || op.equals("||")) {
            int Mid = ++labelNum, End = ++labelNum;
            node.getLhs().accept(this);
            IRLine line = new IRLine( op.equals("&&") ? IRLine.OPCODE.BNEQ : IRLine.OPCODE.BEQ);
            line.addReg(node.getLhs().getReg());
            line.addReg(CONST_NULL);
            line.setLabel(Mid);
            curBlock.addLine(line);

            node.getRhs().accept(this);
            line = new IRLine( op.equals("&&") ? IRLine.OPCODE.BNEQ : IRLine.OPCODE.BEQ );
            line.addReg(node.getRhs().getReg());
            line.addReg(CONST_NULL);
            line.setLabel(Mid);
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.LOAD);
            line.addReg(node.getReg());
            line.addReg( op.equals("&&") ? CONST_ONE : CONST_ZERO );
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.JUMP);
            line.setLabel(End);
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.LABEL);
            line.setLabel(Mid);
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.LOAD);
            line.addReg(node.getReg());
            line.addReg( op.equals("&&") ? CONST_ZERO : CONST_ONE );
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.LABEL);
            line.setLabel(End);
            curBlock.addLine(line);
            return;
        }

        node.getLhs().accept(this);
        node.getRhs().accept(this);
        if (node.getLhs().getType() instanceof StringType && node.getRhs().getType() instanceof StringType) {
            IRLine line = new IRLine(IRLine.OPCODE.MOVE);
            line.addReg(new IRReg(1, 3, false));
            line.addReg(node.getRhs().getReg());
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.MOVE);
            line.addReg(new IRReg(0, 3, false));
            line.addReg(node.getLhs().getReg());
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.CALL);
            line.setFuncStr("my_string_" + switch (op) {
                case "==" -> "eq";
                case "!=" -> "neq";
                case ">" -> "ge";
                case ">=" -> "geq";
                case "<" -> "le";
                case "<=" -> "leq";
                case "+" -> "plus";
                default -> throw new IllegalStateException("Unexpected value: " + op);
            });
            curBlock.addLine(line);

            line = new IRLine(IRLine.OPCODE.MOVE);
            line.addReg(node.getReg());
            line.addReg(new IRReg(10, 0, false));
            curBlock.addLine(line);
            return;
        }

        IRLine line = new IRLine(switch (op) {
            // compare binary
            case "<" -> IRLine.OPCODE.LE;
            case ">" -> IRLine.OPCODE.GE;
            case "<=" -> IRLine.OPCODE.LEQ;
            case ">=" -> IRLine.OPCODE.GEQ;
            case "==" -> IRLine.OPCODE.EQ;
            case "!=" -> IRLine.OPCODE.NEQ;
            // arithmetical binary
            case "*" -> IRLine.OPCODE.MUL;
            case "/" -> IRLine.OPCODE.DIV;
            case "%" -> IRLine.OPCODE.MOD;
            case "+" -> IRLine.OPCODE.ADD;
            case "-" -> IRLine.OPCODE.SUB;
            case "<<" -> IRLine.OPCODE.SHL;
            case ">>" -> IRLine.OPCODE.SHR;
            // bitwise binary
            case "&" -> IRLine.OPCODE.AND;
            case "|" -> IRLine.OPCODE.OR;
            case "^" -> IRLine.OPCODE.XOR;
            default -> throw new CompilationError("IRBuilder - BinaryExprOp op not found");
        } );
        line.getRegList().add(node.getReg());
        line.getRegList().add(node.getLhs().getReg());
        line.getRegList().add(node.getRhs().getReg());
        curBlock.addLine(line);
    }

    @Override
    public void visit(AssignExprNode node) {
        node.getRhs().accept(this);
        node.getLhs().accept(this);
        IRLine line = new IRLine(IRLine.OPCODE.MOVE);
        line.addReg(node.getLhs().getReg());
        line.addReg(node.getRhs().getReg());
        curBlock.addLine(line);
        node.setReg(node.getLhs().getReg());
    }

    @Override
    public void visit(ThisNode node) {
        node.setReg(new IRReg(0,1,false));
    }

    @Override
    public void visit(IDNode node) {
        ScopeType curScope = node.getScope();
        while (true) {
            if (curScope.existVarLocal(node.getID())) {
                if (node.getParentReg() != null) {
                    node.setReg(curBlock.regIDAllocator.allocate(5));
                    IRLine line = new IRLine(IRLine.OPCODE.LOAD);
                    line.addReg(node.getReg());
                    line.addReg(new IRReg(curScope.findVarSymbol(node.getID()).getReg().getID(), 8, false)); // symbol.reg
                    curBlock.addLine(line);
                    node.setInClass(true);
                } else {
                    if (node.getReg().getType() == 11) {
                        IRReg temp1 = curBlock.regIDAllocator.allocate(5);
                        IRLine line = new IRLine(IRLine.OPCODE.LOAD);
                        line.addReg(temp1);
                        line.addReg(new IRReg(node.getReg().getID(), 8, false));
                        curBlock.addLine(line);

                        IRReg temp2 = curBlock.regIDAllocator.allocate(5);
                        line = new IRLine(IRLine.OPCODE.INDEX);
                        line.addReg(temp2);
                        line.addReg(new IRReg(0, 1, false));
                        line.addReg(temp1);
                        curBlock.addLine(line);

                        node.setReg(new IRReg(node.getReg().getID(), node.getReg().getType(), true));
                    }
                }
            } else {
                if (curScope.existFuncLocal(node.getID())) {
                    if (node.getParentReg() != null) node.setReg(node.getParentReg());
                    break;
                }
            }
            if (curScope instanceof GlobalScope) break;
            curScope = curScope.outerScope();
        }
    }

    @Override
    public void visit(EmptyNode node) {
        // Do nothing
    }

    @Override
    public void visit(SimpleVarDefNode node) { // ID | ID '=' expr
        if (node.getExpr() != null) {
            node.getExpr().accept(this);
            IRLine line = new IRLine(IRLine.OPCODE.MOVE);
            line.addReg(node.getScope().findVarSymbol(node.getVarID()).getReg());
            line.addReg(node.getExpr().getReg());
            curBlock.addLine(line);
        }
    }
}
