package Util.Scope;

import Util.CompilationError;
import Util.Symbol.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalScope implements ScopeType {
    private Map<String, VarSymbol> varMap;
    private Map<String, FuncSymbol> funcMap;
    private Map<String, ClassSymbol> classMap;

    public GlobalScope() {
        varMap = new LinkedHashMap<>();
        funcMap = new LinkedHashMap<>();
        classMap = new LinkedHashMap<>();
    }

    @Override public ScopeType outerScope() { return null; }

    @Override public void addVar(VarSymbol cur) { varMap.put(cur.getID(), cur); }

    @Override public void addFunc(FuncSymbol cur) { funcMap.put(cur.getID(), cur); }

    @Override public void addClass(ClassSymbol cur) { classMap.put(cur.getID(), cur); }

    public Map<String, ClassSymbol> getClassMap() { return classMap; }

    @Override public boolean existID(String ID) {
        return (varMap.containsKey(ID) || funcMap.containsKey(ID) || classMap.containsKey(ID));
    }

    @Override public void assertNotExistID(String ID) {
        if (existID(ID))
            throw new CompilationError("Symbol: " + ID + "conflicted in (Global Scope)");
    }

    @Override
    public Symbol findSymbol(String ID) {
        if (varMap.containsKey(ID)) return varMap.get(ID);
        if (funcMap.containsKey(ID)) return funcMap.get(ID);
        if (classMap.containsKey(ID)) return classMap.get(ID);
        throw new CompilationError("Symbol: " + ID + " not found (in Global Scope)");
    }
}
