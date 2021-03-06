package Util.Symbol;

import AST.*;
import IR.IRReg;
import Util.RegIDAllocator;
import Util.Scope.*;
import Util.LocationType;
import Util.Type.*;

public class Symbol {
    private String ID;
    private ScopeType scope;
    private Type type;
    private LocationType loc;
    boolean inClass; // for FuncSymbol only
    IRReg reg; // for VarSymbol only
    RegIDAllocator regIDAllocator;
    public Symbol(String _ID, ScopeType _scope, Type _type, LocationType _loc) {
        ID = _ID; scope = _scope; type = _type; loc = _loc;
        regIDAllocator = _scope.getRegIDAllocator();
    }
    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }
    public ScopeType getScope() { return scope; }
    public void setScope(ScopeType scope) { this.scope = scope; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public LocationType getLoc() { return loc; }
    public void setLoc(LocationType loc) { this.loc = loc; }
    public IRReg getReg() { return reg; }
    public void setReg(IRReg reg) { this.reg = reg; }
    public boolean isInClass() { return inClass; }
    public void setInClass(boolean inClass) { this.inClass = inClass; }
}
