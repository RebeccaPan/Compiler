package AST;

import Util.*;

abstract public class ASTNode {
    private ScopeType scope;
    private LocationType location;

    // constructor
    public ASTNode(ScopeType _scope, LocationType _location) {
        this.scope = _scope;
        this.location = _location;
    }
    public ScopeType getScope() { return scope; }
    public LocationType getLocation() { return location; }
    public abstract void accept(ASTVisitor visitor);
}