package AST;

import Util.LocationType;
import Util.ScopeType;

public class IDNode extends ASTNode {
    public IDNode(ScopeType _scope, LocationType _location) {
        super(_scope, _location);
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
