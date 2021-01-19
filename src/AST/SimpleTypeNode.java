package AST;

import Util.LocationType;
import Util.ScopeType;

public class SimpleTypeNode extends ASTNode {
    private String type;
    private boolean isClassType;
    public SimpleTypeNode (ScopeType _scope, LocationType _location, String _type, boolean _isClassType) {
        super(_scope, _location);
        type = _type; isClassType = _isClassType;
    }
    public String getType() { return type; }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
