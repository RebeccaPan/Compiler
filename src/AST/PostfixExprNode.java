package AST;

import Util.LocationType;
import Util.ScopeType;

public class PostfixExprNode extends ExprNode {
    private String op;
    private ExprNode expr;
    public PostfixExprNode(ScopeType _scope, LocationType _location, String _op, ExprNode _expr) {
        super(_scope, _location);
        op = _op; expr = _expr;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
