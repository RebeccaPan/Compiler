package AST;

public interface ASTVisitor {
    // TODO
    void visit(BoolLiteralNode node);
    void visit(IntLiteralNode node);
    void visit(StrLiteralNode node);
    void visit(NullLiteralNode node);
}
