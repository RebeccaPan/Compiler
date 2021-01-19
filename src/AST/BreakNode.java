package AST;

import Util.LocationType;
import Util.ScopeType;

public class BreakNode extends StmtNode {
    public BreakNode(ScopeType _scope, LocationType _location) {
        super(_scope, _location);
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
