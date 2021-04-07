package AST;

import IR.IRReg;
import Util.*;
import Util.Scope.ScopeType;

abstract public class ASTNode {
    private ScopeType scope;
    private LocationType location;

    private IRReg reg;
    private IRReg parentReg;
    private boolean inClass;

    public ASTNode(LocationType _location) {
        this.location = _location;
    }
    public ScopeType getScope() { return scope; }
    public void setScope(ScopeType scope) { this.scope = scope; }
    public LocationType getLocation() { return location; }
    public void setLocation(LocationType location) { this.location = location; }
    public IRReg getReg() { return reg; }
    public void setReg(IRReg reg) { this.reg = reg; }
    public IRReg getParentReg() { return parentReg; }
    public void setParentReg(IRReg parentReg) { this.parentReg = parentReg; }
    public boolean isInClass() { return inClass; }
    public void setInClass(boolean inClass) { this.inClass = inClass; }

    public abstract void accept(ASTVisitor visitor);
}