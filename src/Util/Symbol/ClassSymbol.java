package Util.Symbol;

import IR.IRReg;
import Util.LocationType;
import Util.Scope.ScopeType;
import Util.Type.*;

public class ClassSymbol extends Symbol {
    public ClassSymbol(String _ID, ScopeType _scope, Type _type, LocationType _loc) {
        super(_ID, _scope, _type, _loc);
    }
}