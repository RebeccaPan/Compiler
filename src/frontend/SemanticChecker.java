package frontend;

import AST.*;
import Util.CompilationError;
import Util.LocationType;
import Util.Scope.*;
import Util.Symbol.*;
import Util.Type.*;

public class SemanticChecker implements ASTVisitor {
    private ScopeType curScope;
    private ClassSymbol curClass;
    private FuncSymbol curFunc;
    private ASTNode curLoop;

    public SemanticChecker() {
        curScope = new GlobalScope();
        curClass = null; curFunc = null; curLoop = null;

        LocationType virtualLoc = new LocationType(-1, -1);

        // int, bool, string, void
        ClassSymbol Int    = new ClassSymbol("int", new LocalScope(curScope), new IntType(), virtualLoc);
        ClassSymbol Bool   = new ClassSymbol("bool", new LocalScope(curScope), new BoolType(), virtualLoc);
        ClassSymbol string = new ClassSymbol("string", null, new StringType(), virtualLoc);
        ClassSymbol Void   = new ClassSymbol("void", new LocalScope(curScope), new VoidType(), virtualLoc);

        // - set scope of string after creating FuncSymbol length, substring, parseInt, ord
        LocalScope StringScope = new LocalScope(curScope);
        FuncSymbol Length = new FuncSymbol("length", new LocalScope(string.getScope()), new IntType(), virtualLoc);
        StringScope.addFunc(Length);
        FuncSymbol Substring = new FuncSymbol("substring", null, new StringType(), virtualLoc);
        LocalScope SubstringScope = new LocalScope(string.getScope());
        VarSymbol Left  = new VarSymbol("left", SubstringScope, new IntType(), Substring.getLoc());
        VarSymbol Right = new VarSymbol("right", SubstringScope, new IntType(), Substring.getLoc());
        SubstringScope.addVar(Left);
        SubstringScope.addVar(Right);
        Substring.setScope(SubstringScope);
        StringScope.addFunc(Substring);
        FuncSymbol ParseInt = new FuncSymbol("parseInt", new LocalScope(curScope), new IntType(), virtualLoc);
        StringScope.addFunc(ParseInt);
        FuncSymbol Ord = new FuncSymbol("ord", null, new IntType(), virtualLoc);
        LocalScope OrdScope = new LocalScope(curScope);
        VarSymbol Pos = new VarSymbol("pos", OrdScope, new IntType(), virtualLoc);
        OrdScope.addVar(Pos);
        Ord.setScope(OrdScope);
        StringScope.addFunc(Ord);
        string.setScope(StringScope);

        curScope.addClass(Int);
        curScope.addClass(Bool);
        curScope.addClass(string);
        curScope.addClass(Void);

        // toString, size
        FuncSymbol ToString = new FuncSymbol("toString", null, new StringType(), virtualLoc);
        LocalScope ToStringScope = new LocalScope(curScope);
        VarSymbol ItoStr = new VarSymbol("i", ToStringScope, new IntType(), virtualLoc);
        ToStringScope.addVar(ItoStr);
        ToString.setScope(ToStringScope);
        curScope.addFunc(ToString);

        FuncSymbol ArraySize = new FuncSymbol("#size#", new LocalScope(curScope), new IntType(), virtualLoc);
        curScope.addFunc(ArraySize);

        // Print & PrintLn with VarSymbol StrPrint(Ln) in local scope
        FuncSymbol Print = new FuncSymbol("print", null, new VoidType(), virtualLoc);
        LocalScope PrintScope = new LocalScope(curScope);
        VarSymbol StrPrint = new VarSymbol("str", PrintScope, new StringType(), virtualLoc);
        PrintScope.addVar(StrPrint);
        Print.setScope(PrintScope);
        curScope.addFunc(Print);

        FuncSymbol PrintLn = new FuncSymbol("println", null, new VoidType(), virtualLoc);
        LocalScope PrintLnScope = new LocalScope(curScope);
        VarSymbol StrPrintLn = new VarSymbol("str", PrintLnScope, new StringType(), virtualLoc);
        PrintLnScope.addVar(StrPrintLn);
        PrintLn.setScope(PrintLnScope);
        curScope.addFunc(PrintLn);

        FuncSymbol GetStr = new FuncSymbol("getStr", new LocalScope(curScope), new StringType(), virtualLoc);
        curScope.addFunc(GetStr);

        // PrintInt & PrintLnInt with VarSymbol IntPrintLn in local scope
        FuncSymbol PrintInt = new FuncSymbol("printInt", null, new VoidType(), virtualLoc);
        LocalScope PrintIntScope = new LocalScope(curScope);
        VarSymbol IntPrint = new VarSymbol("num", PrintIntScope, new IntType(), virtualLoc);
        PrintIntScope.addVar(IntPrint);
        PrintInt.setScope(PrintIntScope);
        curScope.addFunc(PrintInt);

        FuncSymbol PrintIntLn = new FuncSymbol("println", null, new VoidType(), virtualLoc);
        LocalScope PrintIntLnScope = new LocalScope(curScope);
        VarSymbol IntPrintLn = new VarSymbol("num", PrintIntLnScope, new IntType(), virtualLoc);
        PrintIntLnScope.addVar(IntPrintLn);
        PrintIntLn.setScope(PrintIntLnScope);
        curScope.addFunc(PrintIntLn);

        FuncSymbol GetInt = new FuncSymbol("getInt", new LocalScope(curScope), new IntType(), virtualLoc);
        curScope.addFunc(GetInt);
    }

    @Override
    public void visit(BoolLiteralNode node) {
        node.setScope(curScope);
        node.setType(new BoolType());
        node.setExprCat(ExprNode.ExprCat.RVal);
    }

    @Override
    public void visit(IntLiteralNode node) {
        node.setScope(curScope);
        node.setType(new IntType());
        node.setExprCat(ExprNode.ExprCat.RVal);
    }

    @Override
    public void visit(StrLiteralNode node) {
        node.setScope(curScope);
        node.setType(new StringType());
        node.setExprCat(ExprNode.ExprCat.RVal);
    }

    @Override
    public void visit(NullLiteralNode node) {
        node.setScope(curScope);
        node.setType(new NullType());
        node.setExprCat(ExprNode.ExprCat.RVal);
    }

    @Override
    public void visit(ProgramNode node) {
        // store info of all classes
        for (var curNode : node.getDefNodeList()) {
            if (curNode instanceof ClassDefNode) {
                if (((ClassDefNode) curNode).getClassID().equals("main"))
                    throw new CompilationError("Semantic - class name as main", curNode.getLocation());
                ClassSymbol classSymbol = new ClassSymbol(
                        ((ClassDefNode) curNode).getClassID(),
                        curNode.getScope(),
                        new ClassType(((ClassDefNode) curNode).getClassID()),
                        curNode.getLocation() );
                // add info of curClass(Symbol) in curScope
                curScope.addClass(classSymbol);
                // set scope & classSymbol of curNode
                curNode.setScope(classSymbol.getScope());
                ((ClassDefNode) curNode).setClassSymbol(classSymbol);
            }
        }

        // store info of func and constructor of classes
        for (var cur : node.getDefNodeList()) {
            if (cur instanceof ClassDefNode) {
                ClassDefNode classDefNode = (ClassDefNode) cur;
                classDefNode.getVarDefList().forEach(x -> x.accept(this));
                for (FuncDefNode curNode : classDefNode.getFuncDefList()) {
                    FuncSymbol funcSymbol = new FuncSymbol(
                            curNode.getFuncID(),
                            new LocalScope(curScope),
                            null,
                            curNode.getLocation() );
                    Type type = curScope.findSymbol(curNode.getFuncID()).getType();
                    funcSymbol.setType( (type.getDim() == 0) ? type : new ArrayType(type, type.getDim()) );
                    // set scope & funcSymbol of curNode
                    curNode.setScope(funcSymbol.getScope());
                    curNode.setFuncSymbol(funcSymbol);
                    // visit para
                    curScope = curNode.getScope();
                    curNode.getParaList().getParaList().forEach(x -> x.accept(this));
                    curScope = curNode.getScope().outerScope();
                    if (curNode.getFuncID().equals(classDefNode.getClassID()))
                        throw new CompilationError("Semantic - funcID same as classID in class", node.getLocation());
                }
                boolean haveConstructor = false;
                for (ConstructorDefNode curNode : classDefNode.getConstructorDefList()) {
                    if (!curNode.getFuncID().equals(classDefNode.getClassID()))
                        throw new CompilationError("Semantic - constructorID different from classID", node.getLocation());
                    haveConstructor = true;
                    FuncSymbol constructorSymbol = new FuncSymbol(
                            curNode.getFuncID(),
                            new LocalScope(curScope),
                            null,
                            curNode.getLocation() );
                    // set scope & funcSymbol of curNode
                    curNode.setScope(constructorSymbol.getScope());
                    curNode.setFuncSymbol(constructorSymbol);
                    // visit para
                    curScope = curNode.getScope();
                    curNode.getParaList().getParaList().forEach(x -> x.accept(this));
                    curScope = curNode.getScope().outerScope();
                }
                if (!haveConstructor) { // create default constructor which does nothing
                    classDefNode.addDefaultConstructorDef();
                    ConstructorDefNode curNode = classDefNode.getConstructorDefList().get(0);
                    FuncSymbol constructorSymbol = new FuncSymbol(
                            classDefNode.getClassID(),
                            new LocalScope(curScope),
                            null,
                            classDefNode.getLocation() );
                    // set scope & funcSymbol of curNode
                    curNode.setScope(constructorSymbol.getScope());
                    curNode.setFuncSymbol(constructorSymbol);
                }
            }
        }

        // store info of all functions
        boolean mainFuncFound = false;
        for (var curNode : node.getDefNodeList()) {
            if (curNode instanceof FuncDefNode) {
                if (((FuncDefNode) curNode).getFuncID().equals("main")) {
                    if (mainFuncFound) throw new CompilationError("Semantic - multiple main func", curNode.getLocation());
                    else mainFuncFound = true;
                }
                // add info of curFunc(Symbol) in curScope
                FuncSymbol funcSymbol = new FuncSymbol(
                        ((FuncDefNode) curNode).getFuncID(),
                        new LocalScope(curScope),
                        null,
                        curNode.getLocation() );
                Type type = curScope.findSymbol(((FuncDefNode) curNode).getFuncID()).getType();
                funcSymbol.setType( (type.getDim() == 0) ? type : new ArrayType(type, type.getDim()) );
                curScope.addFunc(funcSymbol);
                // set scope & funcSymbol of curNode
                curNode.setScope(funcSymbol.getScope());
                ((FuncDefNode) curNode).setFuncSymbol(funcSymbol);
                // visit para
                curScope = curNode.getScope();
                ((FuncDefNode) curNode).getParaList().getParaList().forEach(x -> x.accept(this));
                curScope = curNode.getScope().outerScope();
            }
        }
        if (!mainFuncFound) throw new CompilationError("Semantic - main func not found");

        ScopeType outerScope = curScope;
        for (var curNode : node.getDefNodeList()) {
            curNode.accept(this);
            curScope = outerScope;
            curFunc = null;
            curClass = null;
            curLoop = null;
        }
    }

    @Override
    public void visit(DefNode node) {
        node.accept(this);
    }

    @Override
    public void visit(ClassDefNode node) {
        curScope.assertNotExistID(node.getClassID());
        ClassSymbol classSymbol = node.getClassSymbol();
        curClass = classSymbol;
        curScope = classSymbol.getScope();

        for (FuncDefNode curNode : node.getFuncDefList()) {
            curNode.accept(this);
            curScope = curNode.getScope().outerScope();
            curFunc = null;
        }
        for (ConstructorDefNode curNode : node.getConstructorDefList()) {
            curNode.accept(this);
            curScope = curNode.getScope().outerScope();
            curFunc = null;
        }
    }

    @Override
    public void visit(FuncDefNode node) {
        FuncSymbol funcSymbol = node.getFuncSymbol();
        curScope = funcSymbol.getScope();
        curFunc = funcSymbol;
        node.getSuite().accept(this);
        if (node.getFuncID().equals("main") && !(curScope.outerScope() instanceof GlobalScope))
            throw new CompilationError("Semantic - main func not in global scope", node.getLocation());
    }

    @Override
    public void visit(VarDefNode node) {
        node.setScope(curScope);
        node.getSimpleVarDefList().forEach(x -> x.accept(this));
    }

    @Override
    public void visit(SimpleVarDefNode node) {
        String typeStr = node.getType().getSimpleTypeNode().getType();
        int dim = node.getType().getDim();
        Type type, baseType = curScope.findSymbol(typeStr).getType();
        if (dim == 0)
            type = baseType;
        else
            type = new ArrayType(baseType, dim);
        if (node.getExpr() != null) {
            node.getExpr().accept(this);
            type.assignable(node.getExpr().getType(), node.getLocation());
        }
        VarSymbol varSymbol = new VarSymbol(node.getVarID(), curScope, type, node.getLocation());
        curScope.addVar(varSymbol);
        node.setScope(curScope);
        node.setVarSymbol(varSymbol);
    }

    @Override
    public void visit(TypeNode node) {
        throw new CompilationError("Semantic - visit type node", node.getLocation());
    }

    @Override
    public void visit(SimpleTypeNode node) {
        throw new CompilationError("Semantic - visit simple type node", node.getLocation());
    }

    @Override
    public void visit(ParaListNode node) {
        for (var cur : node.getParaList()) {
            cur.accept(this);
        }
    }

    @Override
    public void visit(ParaNode node) {
        if (!curScope.existID(node.getParaID()))
            throw new CompilationError("Semantic - paraID not found in scope", node.getLocation());
    }

    @Override
    public void visit(ExprListNode node) {
        for (var cur : node.getExprList()) {
            cur.accept(this);
        }
    }

    @Override
    public void visit(NewExprNode node) {
        node.setScope(curScope);
        node.getSimpleType().accept(this);
        for (ExprNode cur : node.getDimExprList()) {
            cur.accept(this);
            new IntType().assignable(cur.getType(), cur.getLocation());
            node.assertIsVal(node.getLocation());
        }
    }

    @Override
    public void visit(SuiteNode node) {
        LocalScope suiteScope = new LocalScope(curScope);
        node.setScope(suiteScope);
        curScope = suiteScope;
        for (StmtNode cur : node.getStmtNodeList()) {
            cur.accept(this);
            curScope = suiteScope; // in case curScope is changed in accept()
        }
    }

    @Override
    public void visit(IfStmtNode node) {
        node.setScope(curScope);
        node.getCond().accept(this);
        if (node.getCond().getExprCat() == ExprNode.ExprCat.Class)
            throw new CompilationError("Semantic - if stmt cond with type as class");
        if (node.getCond().getExprCat() == ExprNode.ExprCat.Func)
            throw new CompilationError("Semantic - if stmt cond with type as func");
        new BoolType().assignable(node.getCond().getType(), node.getCond().getLocation());
        node.getTrueStmt().accept(this);
        if (node.getFalseStmt() != null)
            node.getFalseStmt().accept(this);
    }

    @Override
    public void visit(ForStmtNode node) {
        ASTNode tempLoop = curLoop;
        curLoop = node;
        if (node.getInit() != null)
            node.getInit().accept(this);
        if (node.getCond() != null) {
            node.getCond().accept(this);
            if (node.getCond().getExprCat() == ExprNode.ExprCat.Class)
                throw new CompilationError("Semantic - for stmt cond with type as class");
            if (node.getCond().getExprCat() == ExprNode.ExprCat.Func)
                throw new CompilationError("Semantic - for stmt cond with type as func");
            new BoolType().assignable(node.getCond().getType(), node.getCond().getLocation());
        }
        else {
            node.setCond(new BoolLiteralNode(node.getLocation(), true));
            node.getCond().accept(this);
        }
        if (node.getStep() != null)
            node.getStep().accept(this);
        node.getStmt().accept(this);
        curLoop = tempLoop;
    }

    @Override
    public void visit(WhileStmtNode node) {
        node.setScope(new LocalScope(curScope));
        ASTNode tempLoop = curLoop;
        curLoop = node;
        node.getExpr().accept(this);
        if (node.getExpr().getExprCat() == ExprNode.ExprCat.Class)
            throw new CompilationError("Semantic - for stmt cond with type as class");
        if (node.getExpr().getExprCat() == ExprNode.ExprCat.Func)
            throw new CompilationError("Semantic - for stmt cond with type as func");
        new BoolType().assignable(node.getExpr().getType(), node.getExpr().getLocation());
        node.getStmt().accept(this);
        curLoop = tempLoop;
    }

    @Override
    public void visit(BreakNode node) {
        node.setScope(curScope);
        if (curLoop == null)
            throw new CompilationError("Semantic - break found outside loop", node.getLocation());
        node.setNext(curLoop);
    }

    @Override
    public void visit(ContinueNode node) {
        node.setScope(curScope);
        if (curLoop == null)
            throw new CompilationError("Semantic - continue found outside loop", node.getLocation());
        node.setNext(curLoop);
    }

    @Override
    public void visit(ReturnNode node) {
        if (curFunc == null)
            throw new CompilationError("Semantic - return found outside func", node.getLocation());
        node.setScope(curScope);
        node.setFuncSymbol(curFunc);
        if (node.isWithRet()) {
            if (node.getFuncSymbol().getType() instanceof VoidType)
                throw new CompilationError("Semantic - return with val found in void func", node.getLocation());
        }
        else {
            if (!(node.getFuncSymbol().getType() instanceof VoidType))
                throw new CompilationError("Semantic - return without val found in non-void func", node.getLocation());
        }
    }

    @Override
    public void visit(PrefixExprNode node) {
        node.setScope(curScope);
        node.getExpr().accept(this);
        node.getExpr().assertIsVal(node.getLocation());
        String op = node.getOp();
        if (op.equals("+") || op.equals("-") || op.equals("~")) {
            new IntType().assignable(node.getExpr().getType(), node.getLocation());
            node.setType(new IntType());
        }
        if (op.equals("++") || op.equals("--")) {
            if (node.getExpr().getExprCat() != ExprNode.ExprCat.LVal)
                throw new CompilationError("Semantic - prefix ++/-- found with non-LVal", node.getLocation());
            new IntType().assignable(node.getExpr().getType(), node.getLocation());
            node.setType(new IntType());
        }
        if (op.equals("!")) {
            new BoolType().assignable(node.getExpr().getType(), node.getLocation());
            node.setType(new BoolType());
        }
        node.setExprCat(ExprNode.ExprCat.RVal);
    }

    @Override
    public void visit(PostfixExprNode node) {
        node.setScope(curScope);
        node.getExpr().accept(this);
        node.getExpr().assertIsVal(node.getLocation());
        if (node.getExpr().getExprCat() != ExprNode.ExprCat.LVal)
            throw new CompilationError("Semantic - postfix ++/-- found with non-LVal", node.getLocation());
        new IntType().assignable(node.getExpr().getType(), node.getLocation());
        node.setType(new IntType());
        node.setExprCat(ExprNode.ExprCat.RVal);
    }

    @Override
    public void visit(ClassMemberExprNode node) {
        node.setScope(curScope);
        node.getExpr().accept(this);
        node.getExpr().assertIsVal(node.getLocation());
        if (node.getExpr().getType() instanceof ClassType) {
            ClassSymbol classSymbol = (ClassSymbol) curScope.findSymbol(node.getExpr().getType().getType());
            Symbol symbol = classSymbol.getScope().findSymbol(node.getID());
            node.setType(symbol.getType());
            node.setSymbol(symbol);
            if (symbol instanceof FuncSymbol) node.setExprCat(ExprNode.ExprCat.Func);
            else if (symbol instanceof VarSymbol) node.setExprCat(ExprNode.ExprCat.LVal);
            else throw new CompilationError("Semantic - class member expr error", node.getLocation());
        }
        else if (node.getExpr().getType() instanceof ArrayType) {
            if (node.getID().equals("#size#")) {
                node.setType(new IntType());
                node.setExprCat(ExprNode.ExprCat.Func);
                node.setSymbol(curScope.findSymbol("#size#"));
            }
            else throw new CompilationError("Semantic - class member expr error", node.getLocation());
        }
        else throw new CompilationError("Semantic - class member expr error", node.getLocation());
    }

    @Override
    public void visit(CallFuncExprNode node) {
        node.setScope(curScope);
        ExprNode exprNode = node.getExpr();
        exprNode.accept(this);
        if (exprNode.getExprCat() != ExprNode.ExprCat.Func)
            throw new CompilationError("Semantic - call func which is not func", node.getLocation());
        FuncSymbol funcSymbol;
        if (exprNode instanceof IDNode) {
            funcSymbol = (FuncSymbol) ((IDNode) exprNode).getSymbol();
        }
        else if (exprNode instanceof ClassMemberExprNode) {
            funcSymbol = (FuncSymbol) ((ClassMemberExprNode) exprNode).getSymbol();
        }
        else throw new CompilationError("Semantic - call func with neither ID nor ClassID", node.getLocation());
        node.setFuncSymbol(funcSymbol);
        if (((LocalScope) funcSymbol.getScope()).getVarList().size() != node.getExprList().getExprList().size())
            throw new CompilationError("Semantic - call func para list size not match", node.getLocation());
        for (int i = 0; i < ((LocalScope) funcSymbol.getScope()).getVarList().size(); ++i) {
            ExprNode cur = node.getExprList().getExprList().get(i);
            cur.accept(this);
            cur.assertIsVal(node.getLocation());
            ((LocalScope) funcSymbol.getScope()).getVarList().get(i).getType().assignable(cur.getType(), node.getLocation());
        }
        node.setType(funcSymbol.getType());
        node.setExprCat(ExprNode.ExprCat.RVal);
    }

    @Override
    public void visit(SubscriptExprNode node) {
        node.setScope(curScope);
        node.getArr().accept(this);
        if (!(node.getArr().getType() instanceof ArrayType))
            throw new CompilationError("Semantic - subscript related array type error", node.getLocation());
        ArrayType arrayType = (ArrayType) node.getArr().getType();
        node.getIndex().accept(this);
        node.getIndex().assertIsVal(node.getLocation());
        if (!(node.getIndex().getType() instanceof IntType))
            throw new CompilationError("Semantic - subscript related index type error", node.getLocation());
        node.setExprCat(ExprNode.ExprCat.LVal);
        node.setType((arrayType.getDim() == 1) ?
                arrayType.getBaseType() : new ArrayType(arrayType.getBaseType(), arrayType.getDim()));
    }

    @Override
    public void visit(BinaryExprNode node) {
        node.setScope(curScope);
        String op = node.getOp();
        ExprNode lhs = node.getLhs(), rhs = node.getRhs();
        lhs.accept(this); lhs.assertIsVal(node.getLocation());
        rhs.accept(this); rhs.assertIsVal(node.getLocation());
        if (op.equals("*") || op.equals("/") || op.equals("%")
                || op.equals("-")
                || op.equals("<<") || op.equals(">>")
                || op.equals("&") || op.equals("|") || op.equals("^") ) {
            new IntType().assignable(lhs.getType(), node.getLocation());
            new IntType().assignable(rhs.getType(), node.getLocation());
            node.setType(new IntType());
        }
        if (op.equals("+")) {
            if (lhs.getType() instanceof IntType && rhs.getType() instanceof IntType) node.setType(new IntType());
            else if (lhs.getType() instanceof StringType && rhs.getType() instanceof StringType) node.setType(new StringType());
            else throw new CompilationError("Semantic - binary add type error", node.getLocation());
        }
        if (op.equals("<") || op.equals(">") || op.equals("<=") || op.equals(">=")) {
            if (lhs.getType() instanceof IntType && rhs.getType() instanceof IntType) node.setType(new IntType());
            else if (lhs.getType() instanceof StringType && rhs.getType() instanceof StringType) node.setType(new StringType());
            else throw new CompilationError("Semantic - binary compare type error", node.getLocation());
        }
        if (op.equals("==") || op.equals("!=")) {
            lhs.getType().comparable(rhs.getType(), node.getLocation());
            node.setType(new BoolType());
        }
        if (op.equals("&&") || op.equals("||") ) {
            new BoolType().assignable(lhs.getType(), node.getLocation());
            new BoolType().assignable(rhs.getType(), node.getLocation());
            node.setType(new BoolType());
        }
        node.setExprCat(ExprNode.ExprCat.RVal);
    }

    @Override
    public void visit(AssignExprNode node) {
        node.setScope(curScope);
        ExprNode lhs = node.getLhs(), rhs = node.getRhs();
        lhs.accept(this); lhs.assertIsVal(node.getLocation());
        rhs.accept(this); rhs.assertIsVal(node.getLocation());
        if (!(lhs.getExprCat() == ExprNode.ExprCat.LVal))
            throw new CompilationError("Semantic - assign to non-LVal", node.getLocation());
        lhs.getType().assignable(rhs.getType(), node.getLocation());
        node.setType(lhs.getType());
        node.setExprCat(ExprNode.ExprCat.RVal);
    }

    @Override
    public void visit(ThisNode node) {
        if (curClass == null)
            throw new CompilationError("Semantic - use this outside class", node.getLocation());
        node.setScope(curScope);
        node.setSymbol(curClass);
        node.setType(curClass.getType());
        node.setExprCat(ExprNode.ExprCat.RVal);
    }

    @Override
    public void visit(IDNode node) {
        node.setScope(curScope);
        Symbol symbol = curScope.findSymbol(node.getID());
        node.setSymbol(symbol);
        if (symbol instanceof ClassSymbol) {
            node.setType(new ClassDefType());
            node.setExprCat(ExprNode.ExprCat.Class);
        }
        else if (symbol instanceof FuncSymbol) {
            node.setType(symbol.getType());
            node.setExprCat(ExprNode.ExprCat.Func);
        }
        else if (symbol instanceof VarSymbol) {
            node.setType(symbol.getType());
            node.setExprCat(ExprNode.ExprCat.LVal);
        }
        else throw new CompilationError("Semantic - IDNode not as class, func or var", node.getLocation());
    }
}

