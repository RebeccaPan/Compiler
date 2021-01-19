package AST;

import Util.LocationType;
import Util.ScopeType;

public class ClassMemberExprNode extends ExprNode {
    private ExprNode expr;
    private String ID;
    public ClassMemberExprNode(ScopeType _scope, LocationType _location, ExprNode _expr, String _ID) {
        super(_scope, _location);
        expr = _expr; ID = _ID;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
