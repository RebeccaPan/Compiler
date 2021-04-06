package AST;

import Util.LocationType;

public class ExprStmtNode extends StmtNode {
    private ExprNode exprNode;

    public ExprStmtNode(LocationType _location, ExprNode _exprNode) {
        super(_location); exprNode = _exprNode;
    }
    public ExprNode getExprNode() { return exprNode; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
