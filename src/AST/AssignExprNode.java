package AST;

import Util.LocationType;
import Util.ScopeType;

public class AssignExprNode extends ExprNode {
    private ExprNode lhs, rhs;
    public AssignExprNode(ScopeType _scope, LocationType _location, ExprNode _lhs, ExprNode _rhs) {
        super(_scope, _location);
        lhs = _lhs; rhs = _rhs;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
