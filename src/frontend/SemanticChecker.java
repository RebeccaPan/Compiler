package frontend;

import AST.*;
import IR.IRBlockList;
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
    private IRBlockList curBlockList;

    public SemanticChecker(IRBlockList _curBlockList) {
        curScope = new GlobalScope();
        curClass = null; curFunc = null; curLoop = null;
        curBlockList = _curBlockList;

        LocationType virtualLoc = new LocationType(-1, -1);

        // int, bool, string, void
        ClassSymbol Int    = new ClassSymbol("int",    new LocalScope(curScope), new IntType(), virtualLoc);
        ClassSymbol Bool   = new ClassSymbol("bool",   new LocalScope(curScope), new BoolType(), virtualLoc);
        ClassSymbol string = new ClassSymbol("string", new LocalScope(curScope), new StringType(), virtualLoc);
        ClassSymbol Void   = new ClassSymbol("void",   new LocalScope(curScope), new VoidType(), virtualLoc);

        // - set scope of string after creating FuncSymbol length, substring, parseInt, ord
        LocalScope StringScope = new LocalScope(curScope);
        FuncSymbol Length = new FuncSymbol("length", new LocalScope(string.getScope()), new IntType(), virtualLoc, true);
        StringScope.addFunc(Length);
        FuncSymbol Substring = new FuncSymbol("substring", new LocalScope(curScope), new StringType(), virtualLoc, true);
        LocalScope SubstringScope = new LocalScope(string.getScope());
        VarSymbol Left  = new VarSymbol("left", SubstringScope, new IntType(), Substring.getLoc(), 1);
        VarSymbol Right = new VarSymbol("right", SubstringScope, new IntType(), Substring.getLoc(), 1);
        SubstringScope.addVar(Left);
        SubstringScope.addVar(Right);
        Substring.setScope(SubstringScope);
        StringScope.addFunc(Substring);
        FuncSymbol ParseInt = new FuncSymbol("parseInt", new LocalScope(curScope), new IntType(), virtualLoc, true);
        StringScope.addFunc(ParseInt);
        FuncSymbol Ord = new FuncSymbol("ord", new LocalScope(curScope), new IntType(), virtualLoc, true);
        LocalScope OrdScope = new LocalScope(curScope);
        VarSymbol Pos = new VarSymbol("pos", OrdScope, new IntType(), virtualLoc, 1);
        OrdScope.addVar(Pos);
        Ord.setScope(OrdScope);
        StringScope.addFunc(Ord);
        string.setScope(StringScope);

        curScope.addClass(Int);
        curScope.addClass(Bool);
        curScope.addClass(string);
        curScope.addClass(Void);

        // toString, size
        FuncSymbol ToString = new FuncSymbol("toString", new LocalScope(curScope), new StringType(), virtualLoc, false);
        LocalScope ToStringScope = new LocalScope(curScope);
        VarSymbol ItoStr = new VarSymbol("i", ToStringScope, new IntType(), virtualLoc, 1);
        ToStringScope.addVar(ItoStr);
        ToString.setScope(ToStringScope);
        curScope.addFunc(ToString);

        FuncSymbol ArraySize = new FuncSymbol("my_array_size", new LocalScope(curScope), new IntType(), virtualLoc, true);
        curScope.addFunc(ArraySize);

        // Print & PrintLn with VarSymbol StrPrint(Ln) in local scope
        FuncSymbol Print = new FuncSymbol("print", new LocalScope(curScope), new VoidType(), virtualLoc, false);
        LocalScope PrintScope = new LocalScope(curScope);
        VarSymbol StrPrint = new VarSymbol("str", PrintScope, new StringType(), virtualLoc, 1);
        PrintScope.addVar(StrPrint);
        Print.setScope(PrintScope);
        curScope.addFunc(Print);

        FuncSymbol PrintLn = new FuncSymbol("println", new LocalScope(curScope), new VoidType(), virtualLoc, false);
        LocalScope PrintLnScope = new LocalScope(curScope);
        VarSymbol StrPrintLn = new VarSymbol("str", PrintLnScope, new StringType(), virtualLoc, 1);
        PrintLnScope.addVar(StrPrintLn);
        PrintLn.setScope(PrintLnScope);
        curScope.addFunc(PrintLn);

        FuncSymbol GetStr = new FuncSymbol("getString", new LocalScope(curScope), new StringType(), virtualLoc, false);
        curScope.addFunc(GetStr);

        // PrintInt & PrintLnInt with VarSymbol IntPrintLn in local scope
        FuncSymbol PrintInt = new FuncSymbol("printInt", new LocalScope(curScope), new VoidType(), virtualLoc, false);
        LocalScope PrintIntScope = new LocalScope(curScope);
        VarSymbol IntPrint = new VarSymbol("num", PrintIntScope, new IntType(), virtualLoc, 1);
        PrintIntScope.addVar(IntPrint);
        PrintInt.setScope(PrintIntScope);
        curScope.addFunc(PrintInt);

        FuncSymbol PrintIntLn = new FuncSymbol("printlnInt", new LocalScope(curScope), new VoidType(), virtualLoc, false);
        LocalScope PrintIntLnScope = new LocalScope(curScope);
        VarSymbol IntPrintLn = new VarSymbol("num", PrintIntLnScope, new IntType(), virtualLoc, 1);
        PrintIntLnScope.addVar(IntPrintLn);
        PrintIntLn.setScope(PrintIntLnScope);
        curScope.addFunc(PrintIntLn);

        FuncSymbol GetInt = new FuncSymbol("getInt", new LocalScope(curScope), new IntType(), virtualLoc, false);
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
                        new LocalScope(curScope),
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
                curScope = classDefNode.getScope();
                classDefNode.getVarDefList().forEach(x -> x.accept(this));
                for (FuncDefNode curNode : classDefNode.getFuncDefList()) {
                    FuncSymbol funcSymbol = new FuncSymbol(
                            curNode.getFuncID(),
                            new LocalScope(curScope),
                            null,
                            curNode.getLocation(), true );
                    curScope.addFunc(funcSymbol);
                    Type type = curScope.findSymbol(curNode.getType().getSimpleTypeNode().getType()).getType();
                    funcSymbol.setType( (curNode.getType().getDim() == 0) ? type : new ArrayType(type, curNode.getType().getDim()) );
                    // set scope & funcSymbol of curNode
                    curNode.setScope(funcSymbol.getScope());
                    curNode.setFuncSymbol(funcSymbol);
                    // visit para
                    curScope = curNode.getScope();
                    if (curNode.getParaList() != null)
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
                            new VoidType(),
                            curNode.getLocation(), true );
                    // set scope & funcSymbol of curNode
                    curNode.setScope(constructorSymbol.getScope());
                    curNode.setFuncSymbol(constructorSymbol);
                    ((LocalScope)curScope).addConstructor(constructorSymbol);
                    // visit para
                    curScope = curNode.getScope();
                    if (curNode.getParaList() != null)
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
                            classDefNode.getLocation(), true );
                    // set scope & funcSymbol of curNode
                    curNode.setScope(constructorSymbol.getScope());
                    curScope = constructorSymbol.getScope();
                    curNode.setFuncSymbol(constructorSymbol);
                    ((LocalScope)curScope).addConstructor(constructorSymbol);
                    curScope = curScope.outerScope();
                }
                curScope = curScope.outerScope();
            }
        }

        // store info of all functions
        boolean mainFuncFound = false;
        for (var curNode : node.getDefNodeList()) {
            if (curNode instanceof FuncDefNode) {
                if (((FuncDefNode) curNode).getFuncID().equals("main")) {
                    if (mainFuncFound)
                        throw new CompilationError("Semantic - multiple main func", curNode.getLocation());
                    if (((FuncDefNode) curNode).getParaList() != null)
                        throw new CompilationError("Semantic - main func paraList no empty", curNode.getLocation());
                    if (!((FuncDefNode) curNode).getType().getSimpleTypeNode().getType().equals("int"))
                        throw new CompilationError("Semantic - main func type should be int", curNode.getLocation());
                    else mainFuncFound = true;
                }
                // add info of curFunc(Symbol) in curScope
                FuncSymbol funcSymbol = new FuncSymbol(
                        ((FuncDefNode) curNode).getFuncID(),
                        new LocalScope(curScope),
                        null,
                        curNode.getLocation(), false );
                curScope.addFunc(funcSymbol);
                Type type = curScope.findSymbol(((FuncDefNode) curNode).getType().getSimpleTypeNode().getType()).getType();
                funcSymbol.setType( (((FuncDefNode) curNode).getType().getDim() == 0) ? type : new ArrayType(type, ((FuncDefNode) curNode).getType().getDim()) );
                // set scope & funcSymbol of curNode
                curNode.setScope(funcSymbol.getScope());
                ((FuncDefNode) curNode).setFuncSymbol(funcSymbol);
                // visit para
                curScope = curNode.getScope();
                if (((FuncDefNode) curNode).getParaList() != null)
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
        throw new CompilationError("Semantic - visit def node");
    }

    @Override
    public void visit(ClassDefNode node) {
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
        if (node.getSuite() != null)
            node.getSuite().accept(this);
        curScope = funcSymbol.getScope();
        if (node.getFuncID().equals("main") && !(curScope.outerScope() instanceof GlobalScope))
            throw new CompilationError("Semantic - main func not in global scope", node.getLocation());
    }

    @Override
    public void visit(ConstructorDefNode node) {
        FuncSymbol constructorSymbol = node.getFuncSymbol();
        curScope = constructorSymbol.getScope();
        curFunc = constructorSymbol;
        if (node.getSuite() != null)
            node.getSuite().accept(this);
        curScope = constructorSymbol.getScope();
        if (node.getFuncID().equals("main") && !(curScope.outerScope() instanceof GlobalScope))
            throw new CompilationError("Semantic - main func not in global scope", node.getLocation());
    }

    @Override
    public void visit(VarDefNode node) {
        node.setScope(curScope);
        node.getSimpleVarDefList().forEach(x -> x.accept(this));
    }

    @Override
    public void visit(VarDefStmtNode node) {
        node.setScope(curScope);
        node.getVarDefNode().accept(this);
    }

    @Override
    public void visit(SimpleVarDefNode node) {
        if (curScope instanceof GlobalScope) curBlockList.addGlobal(0);
        String typeStr = node.getType().getSimpleTypeNode().getType();
        int dim = node.getType().getDim();
        Type type, baseType = curScope.findClassSymbol(typeStr).getType();
        if (dim == 0)
            type = baseType;
        else
            type = new ArrayType(baseType, dim);
        if (node.getExpr() != null) {
            node.getExpr().accept(this);
            type.assignable(node.getExpr().getType(), node.getLocation());
        }
        int allocateType;
        if (curScope.outerScope() == null) allocateType = 2; // Global
        else allocateType = (curClass == null) ? 1 : 11; // Local or inClass
        VarSymbol varSymbol = new VarSymbol(node.getVarID(), curScope, type, node.getLocation(), allocateType);
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
        String typeStr = node.getType().getSimpleTypeNode().getType();
        int dim = node.getType().getDim();
        Type type, baseType = curScope.findSymbol(typeStr).getType();
        if (dim == 0)
            type = baseType;
        else
            type = new ArrayType(baseType, dim);
        VarSymbol varSymbol = new VarSymbol(node.getParaID(), curScope, type, node.getLocation(), 1);
        curScope.addVar(varSymbol);
        node.setScope(curScope);
        node.setVarSymbol(varSymbol);
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
        for (ExprNode cur : node.getDimExprList()) {
            cur.accept(this);
            new IntType().assignable(cur.getType(), cur.getLocation());
            cur.assertIsVal(node.getLocation());
        }
        Type type = curScope.findClassSymbol(node.getSimpleType().getType()).getType();
        int dim = node.getDim();
        node.setType((dim == 0) ? type : new ArrayType(type, dim));
        node.setExprCat(ExprNode.ExprCat.RVal);
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
    public void visit(ExprStmtNode node) {
        node.setScope(curScope);
        node.getExprNode().accept(this);
    }

    @Override
    public void visit(IfStmtNode node) {
        node.setScope(curScope);
        if (node.getCond() != null) {
            node.getCond().accept(this);
            if (node.getCond().getExprCat() == ExprNode.ExprCat.Class)
                throw new CompilationError("Semantic - if stmt cond with type as class");
            if (node.getCond().getExprCat() == ExprNode.ExprCat.Func)
                throw new CompilationError("Semantic - if stmt cond with type as func");
            new BoolType().assignable(node.getCond().getType(), node.getCond().getLocation());
        }
        if (node.getTrueSuite() != null)
            node.getTrueSuite().accept(this);
        if (node.getFalseSuite() != null)
            node.getFalseSuite().accept(this);
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
        else { // set empty cond as true
            node.setCond(new BoolLiteralNode(node.getLocation(), true));
            node.getCond().accept(this);
        }
        if (node.getStep() != null)
            node.getStep().accept(this);
        if (node.getSuite() != null)
            node.getSuite().accept(this);
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
        node.getSuite().accept(this);
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
        if (!(node.isWithRet()) && !(node.getFuncSymbol().getType() instanceof VoidType)) {
            throw new CompilationError("Semantic - return without val found in non-void func", node.getLocation());
        }
        else if(node.isWithRet()) {
            if (node.getFuncSymbol().getType() instanceof VoidType)
                throw new CompilationError("Semantic - return with val found in void func", node.getLocation());
            node.getRetExpr().accept(this);
            node.getRetExpr().assertIsVal(node.getLocation());
            curFunc.getType().assignable(node.getRetExpr().getType(), node.getLocation());
        }
        if (curFunc.getID().equals("main")) {
            if (node.isWithRet()) curBlockList.mainNeedRet = false;
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
            node.setExprCat(ExprNode.ExprCat.RVal);
        }
        if (op.equals("++") || op.equals("--")) {
            if (node.getExpr().getExprCat() != ExprNode.ExprCat.LVal)
                throw new CompilationError("Semantic - prefix ++/-- found with non-LVal", node.getLocation());
            new IntType().assignable(node.getExpr().getType(), node.getLocation());
            node.setType(new IntType());
            node.setExprCat(ExprNode.ExprCat.LVal);
        }
        if (op.equals("!")) {
            new BoolType().assignable(node.getExpr().getType(), node.getLocation());
            node.setType(new BoolType());
            node.setExprCat(ExprNode.ExprCat.RVal);
        }
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
        if (node.getExpr().getType() instanceof ClassType || node.getExpr().getType() instanceof StringType) {
            ClassSymbol classSymbol = curScope.findClassSymbol(node.getExpr().getType().getType());
            Symbol symbol = classSymbol.getScope().findSymbol(node.getID().getID());
            node.setType(symbol.getType());
            node.getID().setScope(classSymbol.getScope());
            node.setSymbol(symbol);
            if (symbol instanceof FuncSymbol) node.setExprCat(ExprNode.ExprCat.Func);
            else if (symbol instanceof VarSymbol) node.setExprCat(ExprNode.ExprCat.LVal);
            else throw new CompilationError("Semantic - class member expr error", node.getLocation());
        }
        else if (node.getExpr().getType() instanceof ArrayType) {
            if (node.getID().getID().equals("size")) {
                node.setType(new IntType());
                node.setExprCat(ExprNode.ExprCat.Func);
                node.setSymbol(curScope.findSymbol("my_array_size"));
                node.getID().setScope(node.getSymbol().getScope());
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
        if (node.getExprList() == null) {
            if (((LocalScope) funcSymbol.getScope()).getVarList().size() != 0)
                throw new CompilationError("Semantic - call func para list size not match", node.getLocation());
        }
        else if (((LocalScope) funcSymbol.getScope()).getVarList().size() != node.getExprList().getExprList().size())
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
                arrayType.getBaseType() : new ArrayType(arrayType.getBaseType(), arrayType.getDim() - 1));
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
            if (lhs.getType() instanceof IntType && rhs.getType() instanceof IntType) node.setType(new BoolType());
            else if (lhs.getType() instanceof StringType && rhs.getType() instanceof StringType) node.setType(new BoolType());
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
        node.setExprCat(ExprNode.ExprCat.LVal);
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
            node.setReg(node.getSymbol().getReg());
        }
        else throw new CompilationError("Semantic - IDNode not as class, func or var", node.getLocation());
    }

    @Override
    public void visit(EmptyNode node) {
        // Do nothing
    }
}

