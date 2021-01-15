package AST;

import Util.*;

public class NullLiteralNode extends ASTNode {
    // constructor
    public NullLiteralNode(ScopeType _scope, LocationType _location) {
        super(_scope, _location);
    }
    @Override public void accept(ASTVisitor visitor) { visitor.visit(this); }
}