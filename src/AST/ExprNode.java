package AST;

import Util.LocationType;
import Util.ScopeType;

abstract public class ExprNode extends ASTNode{
    // TODO
    public ExprNode(ScopeType _scope, LocationType _location) {
        super(_scope, _location);
    }
}
