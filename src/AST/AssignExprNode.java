package AST;

import Util.LocationType;

public class AssignExprNode extends ExprNode {
    private ExprNode lhs, rhs;
    public AssignExprNode(LocationType _location, ExprNode _lhs, ExprNode _rhs) {
        super(_location);
        lhs = _lhs; rhs = _rhs;
    }
    public ExprNode getLhs() { return lhs; }
    public ExprNode getRhs() { return rhs; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
