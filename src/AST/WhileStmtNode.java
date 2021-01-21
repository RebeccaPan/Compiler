package AST;

import Util.LocationType;

public class WhileStmtNode extends StmtNode {
    private ExprNode expr;
    private StmtNode stmt;
    public WhileStmtNode(LocationType _location, ExprNode _expr, StmtNode _stmt) {
        super(_location);
        expr = _expr; stmt = _stmt;
    }
    public ExprNode getExpr() { return expr; }
    public StmtNode getStmt() { return stmt; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
