package Optimize.AST;

import AST.*;
import IR.IRBlockList;
import Optimize.Opt;
import Util.CompilationError;
import Util.LocationType;
import Util.Scope.GlobalScope;
import Util.Symbol.Symbol;
import Util.Type.BoolType;
import Util.Type.IntType;
import Util.Type.StringType;
import Util.Type.Type;

import java.util.*;

//Constant folding/broadcast/whatever name you want
//- visit according to the order of AST
//- for b = 1, c = 2, a = b + c:
//- (1) a = 1 + 2 (2) a = 3
public class ConstantBroadcast extends Opt implements ASTVisitor {
    public ProgramNode programNode;
    private Map<Symbol, ExprNode> constValMap = new LinkedHashMap<>();
    private Set<Symbol> BlackList = new LinkedHashSet<>();
    private boolean localUpdated = false;
    private boolean roundOne = false;

    public ConstantBroadcast(IRBlockList _curBlockList) { super(_curBlockList); programNode = null; }

    @Override
    public void opt() {
        updated = false;
        visit(programNode);
        System.out.print("");
    }

    @Override public void visit(BoolLiteralNode node) { node.setConstVal(node); }
    @Override public void visit(IntLiteralNode node) { node.setConstVal(node); }
    @Override public void visit(StrLiteralNode node) { node.setConstVal(node); }
    @Override public void visit(NullLiteralNode node) { node.setConstVal(node); }

    @Override
    public void visit(ProgramNode node) {
        roundOne = true;
        for (DefNode defNode : node.getDefNodeList()) defNode.accept(this);
        roundOne = false;
        localUpdated = true;
        while (localUpdated) {
            localUpdated = false;
            for (DefNode defNode : node.getDefNodeList()) defNode.accept(this);
            updated |= localUpdated;
        }
    }

    // Unreachable
    @Override public void visit(DefNode node) {
        throw new CompilationError("ConstantBroadcast Error: unreachable");
    }

    @Override
    public void visit(ClassDefNode node) {
        node.getVarDefList().forEach(x -> x.accept(this));
        node.getFuncDefList().forEach(x -> x.accept(this));
        node.getConstructorDefList().forEach(x -> x.accept(this));
    }

    @Override
    public void visit(FuncDefNode node) { if (node.getSuite() != null) node.getSuite().accept(this); }

    @Override
    public void visit(ConstructorDefNode node) { if (node.getSuite() != null) node.getSuite().accept(this); }

    @Override
    public void visit(VarDefNode node) { node.getSimpleVarDefList().forEach(x -> x.accept(this)); }

    @Override
    public void visit(VarDefStmtNode node) { node.getVarDefNode().accept(this); }

    @Override public void visit(TypeNode node) { }
    @Override public void visit(SimpleTypeNode node) { }

    @Override public void visit(ParaListNode node) { }
    @Override public void visit(ParaNode node) { }

    @Override
    public void visit(ExprListNode node) { node.getExprList().forEach(x -> x.accept(this)); }

    @Override public void visit(NewExprNode node) { }

    @Override
    public void visit(SuiteNode node) { node.getStmtNodeList().forEach(x -> x.accept(this)); }

    @Override public void visit(ExprStmtNode node) { node.getExprNode().accept(this); }

    @Override
    public void visit(IfStmtNode node) {
        if (node.getCond() != null) node.getCond().accept(this);
        if (node.getTrueSuite() != null) node.getTrueSuite().accept(this);
        if (node.getFalseSuite() != null) node.getFalseSuite().accept(this);
    }

    @Override
    public void visit(ForStmtNode node) {
        if (node.getInit() != null) node.getInit().accept(this);
        if (node.getCond() != null) node.getCond().accept(this);
        if (node.getStep() != null) node.getStep().accept(this);
        if (node.getSuite()!= null) node.getSuite().accept(this);
    }

    @Override
    public void visit(WhileStmtNode node) {
        node.getExpr().accept(this);
        node.getSuite().accept(this);
    }

    @Override public void visit(BreakNode node) { }
    @Override public void visit(ContinueNode node) { }

    @Override
    public void visit(ReturnNode node) {
        if (node.getRetExpr() != null) node.getRetExpr().accept(this);
    }

    @Override
    public void visit(PrefixExprNode node) {
        node.getExpr().accept(this);
        String op = node.getOp();
        if (roundOne) {
            if (op.equals("++") || op.equals("--")) {
                if (node.getExpr() instanceof IDNode) {
                    BlackList.add(((IDNode) node.getExpr()).getSymbol());
                }
            }
        } else {
            if (!node.hasConstVal() && node.getExpr().hasConstVal()) {
                if (op.equals("!")) node.setConstVal(new BoolLiteralNode(node.getLocation(),
                                                    !((BoolLiteralNode) node.getExpr().getConstVal()).getVal()));
                else { // + - ~ for int val
                    int val = ((IntLiteralNode) node.getExpr().getConstVal()).getVal();
                    if (op.equals("+")) node.setConstVal(new IntLiteralNode(node.getLocation(), +val));
                    if (op.equals("-")) node.setConstVal(new IntLiteralNode(node.getLocation(), -val));
                    if (op.equals("~")) node.setConstVal(new IntLiteralNode(node.getLocation(), ~val));
                }
                localUpdated = true;
            }
        }
    }

    @Override
    public void visit(PostfixExprNode node) {
        node.getExpr().accept(this);
        if (roundOne) {
            if (node.getExpr() instanceof IDNode) {
                BlackList.add(((IDNode) node.getExpr()).getSymbol());
            }
        }
    }

    @Override
    public void visit(ClassMemberExprNode node) {
        node.getExpr().accept(this);
        node.getID().accept(this);
    }

    @Override
    public void visit(CallFuncExprNode node) {
        node.getExpr().accept(this);
        if (node.getExprList() != null) node.getExprList().accept(this);
    }

    @Override
    public void visit(SubscriptExprNode node) {
        node.getIndex().accept(this);
        node.getArr().accept(this);
    }

    @Override
    public void visit(BinaryExprNode node) {
        node.getRhs().accept(this);
        node.getLhs().accept(this);
        String op = node.getOp();
        LocationType loc = node.getLocation();
        if (roundOne) return;
        if (!node.hasConstVal() && node.getLhs().hasConstVal() && node.getRhs().hasConstVal()) {
            boolean attemptSuccess = true; // may be set false later
            if (node.getLhs().getType() instanceof StringType
             && node.getRhs().getType() instanceof StringType) {
                // str: + < > <= >=
                String valLhs = ((StrLiteralNode) node.getLhs().getConstVal()).getVal();
                String valRhs = ((StrLiteralNode) node.getRhs().getConstVal()).getVal();
                switch (op) {
                    case "+" -> node.setConstVal(new  StrLiteralNode(loc, "_" + valLhs + valRhs + "_"));
                    case "<" -> node.setConstVal(new BoolLiteralNode(loc, valLhs.compareTo(valRhs) < 0));
                    case ">" -> node.setConstVal(new BoolLiteralNode(loc, valLhs.compareTo(valRhs) > 0));
                    case "<="-> node.setConstVal(new BoolLiteralNode(loc, valLhs.compareTo(valRhs) <=0));
                    case ">="-> node.setConstVal(new BoolLiteralNode(loc, valLhs.compareTo(valRhs) >=0));
                    case "=="-> node.setConstVal(new BoolLiteralNode(loc, valLhs.compareTo(valRhs) ==0));
                    case "!="-> node.setConstVal(new BoolLiteralNode(loc, valLhs.compareTo(valRhs) !=0));
                    default -> attemptSuccess = false;
                }
            } else if (node.getLhs().getType() instanceof IntType
                    && node.getRhs().getType() instanceof IntType) {
                int valLhs = ((IntLiteralNode) node.getLhs().getConstVal()).getVal();
                int valRhs = ((IntLiteralNode) node.getRhs().getConstVal()).getVal();
                switch (op) {
                    case "*" -> node.setConstVal(new IntLiteralNode(loc, valLhs * valRhs));
                    case "/" -> {
                        if (valRhs == 0) attemptSuccess = false;
                        else node.setConstVal(new IntLiteralNode(loc, valLhs / valRhs));
                    }
                    case "%" -> {
                        if (valRhs == 0) attemptSuccess = false;
                        else node.setConstVal(new IntLiteralNode(loc, valLhs % valRhs));
                    }
                    case "+" -> node.setConstVal(new IntLiteralNode(loc, valLhs + valRhs));
                    case "-" -> node.setConstVal(new IntLiteralNode(loc, valLhs - valRhs));
                    case ">>"-> node.setConstVal(new IntLiteralNode(loc, valLhs >> valRhs));
                    case "<<"-> node.setConstVal(new IntLiteralNode(loc, valLhs << valRhs));

                    case "<" -> node.setConstVal(new BoolLiteralNode(loc, valLhs < valRhs));
                    case ">" -> node.setConstVal(new BoolLiteralNode(loc, valLhs > valRhs));
                    case "<="-> node.setConstVal(new BoolLiteralNode(loc, valLhs <=valRhs));
                    case ">="-> node.setConstVal(new BoolLiteralNode(loc, valLhs >=valRhs));
                    case "=="-> node.setConstVal(new BoolLiteralNode(loc, valLhs ==valRhs));
                    case "!="-> node.setConstVal(new BoolLiteralNode(loc, valLhs !=valRhs));

                    case "&" -> node.setConstVal(new IntLiteralNode(loc, valLhs & valRhs));
                    case "|" -> node.setConstVal(new IntLiteralNode(loc, valLhs | valRhs));
                    case "^" -> node.setConstVal(new IntLiteralNode(loc, valLhs ^ valRhs));
                    default -> attemptSuccess = false;
                }
            } else if (node.getLhs().getType() instanceof BoolType
                    && node.getRhs().getType() instanceof BoolType) {
                boolean valLhs = ((BoolLiteralNode) node.getLhs().getConstVal()).getVal();
                boolean valRhs = ((BoolLiteralNode) node.getRhs().getConstVal()).getVal();
                switch (op) {
                    case "==" -> node.setConstVal(new BoolLiteralNode(loc, valLhs == valRhs));
                    case "!=" -> node.setConstVal(new BoolLiteralNode(loc, valLhs != valRhs));
                    case "&&" -> node.setConstVal(new BoolLiteralNode(loc, valLhs && valRhs));
                    case "||" -> node.setConstVal(new BoolLiteralNode(loc, valLhs || valRhs));
                    default -> attemptSuccess = false;
                }
            } else if (op.equals("==") || op.equals("!=")) { // nullLiteralType and nullLiteralType
                node.setConstVal(new BoolLiteralNode(loc, op.equals("==")));
            } else attemptSuccess = false;
            localUpdated |= attemptSuccess;
        }
    }

    @Override
    public void visit(AssignExprNode node) {
        node.getRhs().accept(this);
        node.getLhs().accept(this);
        if (roundOne) {
            if (node.getLhs() instanceof IDNode) {
                BlackList.add(((IDNode) node.getLhs()).getSymbol());
            }
        }
    }

    @Override
    public void visit(ThisNode node) { }

    @Override
    public void visit(IDNode node) {
        if (roundOne) return;
        if (constValMap.containsKey(node.getSymbol()) && !BlackList.contains(node.getSymbol()) && !node.hasConstVal()) {
            node.setConstVal(constValMap.get(node.getSymbol()));
            localUpdated = true;
        }
    }

    @Override public void visit(EmptyNode node) { }

    @Override
    public void visit(SimpleVarDefNode node) {
        if (roundOne) {
            if (node.getVarSymbol().getScope() instanceof GlobalScope) {
                if (node.getExpr() != null) {
                    node.getExpr().accept(this);
                    if (node.getExpr().hasConstVal()) {
                        constValMap.put(node.getVarSymbol(), node.getExpr().getConstVal());
                    }
                } else {
                    Type type = node.getVarSymbol().getType();
                    if (type instanceof BoolType)
                        constValMap.put(node.getVarSymbol(), new BoolLiteralNode(node.getLocation(), false));
                    else if (type instanceof IntType)
                        constValMap.put(node.getVarSymbol(), new IntLiteralNode(node.getLocation(), 0));
                    else if (type instanceof StringType)
                        constValMap.put(node.getVarSymbol(), new StrLiteralNode(node.getLocation(), "__"));
                    else if (type instanceof NullLiteralNode)
                        constValMap.put(node.getVarSymbol(), new NullLiteralNode(node.getLocation()));
                }
            }
        } else {
            if (constValMap.containsKey(node.getVarSymbol())) return;
            if (BlackList.contains(node.getVarSymbol())) return;
            if (node.getExpr() == null) return;
            node.getExpr().accept(this);
            if (node.getExpr().hasConstVal()) {
                constValMap.put(node.getVarSymbol(), node.getExpr().getConstVal());
                localUpdated = true;
            }
        }
    }
}
