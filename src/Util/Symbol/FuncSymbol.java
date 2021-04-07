package Util.Symbol;

import IR.IRReg;
import Util.LocationType;
import Util.Scope.ScopeType;
import Util.Type.*;

public class FuncSymbol extends Symbol {
    public FuncSymbol(String _ID, ScopeType _scope, Type _type, LocationType _loc, boolean _inClass) {
        super(_ID, _scope, _type, _loc);
        inClass = _inClass;
    }
}
