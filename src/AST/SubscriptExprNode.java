package AST;

import Util.LocationType;
import Util.ScopeType;

public class SubscriptExprNode extends ExprNode {
    private ExprNode arr, index;
    public SubscriptExprNode(ScopeType _scope, LocationType _location, ExprNode _arr, ExprNode _index) {
        super(_scope, _location);
        arr = _arr; index = _index;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
