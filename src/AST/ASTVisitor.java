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
    void visit(SuiteNode node);
    void visit(ThisNode node);
    void visit(IDNode node);
    void visit(SimpleVarDefNode node);
    void visit(ExprListNode node);
    void visit(ExprNode node);
}
