package AST;

import Util.LocationType;
import Util.ScopeType;

public class TypeNode extends ASTNode {
    private SimpleTypeNode simpleTypeNode;
    private boolean isArray;
    public TypeNode (ScopeType _scope, LocationType _location, SimpleTypeNode _simpleTypeNode, boolean _isArray) {
        super(_scope, _location);
        simpleTypeNode = _simpleTypeNode;
        isArray = _isArray;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
