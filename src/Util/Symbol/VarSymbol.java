package Util.Symbol;

import Util.LocationType;
import Util.Scope.ScopeType;
import Util.Type.Type;

public class VarSymbol extends Symbol {
    public VarSymbol(String _ID, ScopeType _scope, Type _type, LocationType _loc) {
        super(_ID, _scope, _type, _loc);
    }
}
