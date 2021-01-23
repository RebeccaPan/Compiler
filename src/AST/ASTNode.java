package AST;

import Util.*;
import Util.Scope.ScopeType;

abstract public class ASTNode {
    private ScopeType scope;
    private LocationType location;

    public ASTNode(LocationType _location) {
        this.location = _location;
    }
    public ScopeType getScope() { return scope; }
    public void setScope(ScopeType scope) { this.scope = scope; }
    public LocationType getLocation() { return location; }
    public void setLocation(LocationType location) { this.location = location; }

    public abstract void accept(ASTVisitor visitor);
}