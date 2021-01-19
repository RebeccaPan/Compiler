package AST;

import Util.LocationType;
import Util.ScopeType;

public class BinaryExprNode extends ExprNode {
    private String op;
    private ExprNode lhs, rhs;
    public BinaryExprNode(ScopeType _scope, LocationType _location, String _op, ExprNode _lhs, ExprNode _rhs) {
        super(_scope, _location);
        op = _op; lhs = _lhs; rhs = _rhs;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
