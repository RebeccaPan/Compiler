package AST;

import java.net.IDN;

public interface ASTVisitor {
    // TODO
    void visit(BoolLiteralNode node);
    void visit(IntLiteralNode node);
    void visit(StrLiteralNode node);
    void visit(NullLiteralNode node);

    void visit(ProgramNode node);
    void visit(DefNode node);
    void visit(ClassDefNode node);
    void visit(FuncDefNode node);
    void visit(VarDefNode node);

    void visit(TypeNode node);
    void visit(SimpleTypeNode node);

    void visit(ParaListNode node);
    void visit(ParaNode node);

    void visit(ExprNode node);
    void visit(ExprListNode node);
    void visit(NewExprNode node);

    void visit(SuiteNode node);
    void visit(StmtNode node);
    void visit(IfStmtNode node);
    void visit(ForStmtNode node);
    void visit(WhileStmtNode node);

    void visit(PrefixExprNode node);
    void visit(PostfixExprNode node);
    void visit(ClassMemberExprNode node);
//    void visit(CallFuncExprNode node);
    void visit(SubscriptExprNode node);
    void visit(BinaryExprNode node);
    void visit(AssignExprNode node);

    void visit(ThisNode node);
    void visit(IDNode node);
    void visit(SimpleVarDefNode node);
}
