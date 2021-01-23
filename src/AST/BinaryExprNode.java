package AST;

import Util.LocationType;

public class BinaryExprNode extends ExprNode {
    private String op;
    private ExprNode lhs, rhs;

    public BinaryExprNode(LocationType _location, String _op, ExprNode _lhs, ExprNode _rhs) {
        super(_location);
        op = _op; lhs = _lhs; rhs = _rhs;
    }
    public String getOp() { return op; }
    public ExprNode getLhs() { return lhs; }
    public ExprNode getRhs() { return rhs; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
