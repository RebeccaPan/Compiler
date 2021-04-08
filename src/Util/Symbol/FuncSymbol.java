package Util.Symbol;

import Util.LocationType;
import Util.Scope.ScopeType;
import Util.Type.*;

public class FuncSymbol extends Symbol {
    private String classID = null;

    public FuncSymbol(String _ID, ScopeType _scope, Type _type, LocationType _loc, boolean _inClass, String _classID) {
        super(_ID, _scope, _type, _loc);
        inClass = _inClass;
        classID = _classID;
        assert inClass || classID == null;
    }

    public String getClassID() { return classID; }
}
