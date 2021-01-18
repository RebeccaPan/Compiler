package AST;

import Util.LocationType;
import Util.ScopeType;

public class ThisNode extends ASTNode {
    public ThisNode(ScopeType _scope, LocationType _location) {
        super(_scope, _location);
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
