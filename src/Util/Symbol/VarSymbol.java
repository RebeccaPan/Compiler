package Util.Symbol;

import IR.IRReg;
import Util.LocationType;
import Util.Scope.ScopeType;
import Util.Type.Type;

public class VarSymbol extends Symbol {
    public VarSymbol(String _ID, ScopeType _scope, Type _type, LocationType _loc, int allocateType) {
        super(_ID, _scope, _type, _loc);
        reg = regIDAllocator.allocate(allocateType);
    }
}
