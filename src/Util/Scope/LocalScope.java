package Util.Scope;

import Util.CompilationError;
import Util.Symbol.ClassSymbol;
import Util.Symbol.FuncSymbol;
import Util.Symbol.Symbol;
import Util.Symbol.VarSymbol;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class LocalScope implements ScopeType {
    private ScopeType outerScope;
    private Map<String, VarSymbol> varMap;
    private Map<String, FuncSymbol> funcMap;
    private ArrayList<VarSymbol> varList;

    public LocalScope(ScopeType _outerScope) {
        outerScope = _outerScope;
        varMap = new LinkedHashMap<>();
        funcMap = new LinkedHashMap<>();
        varList = new ArrayList<>();
    }

    public ScopeType getOuterScope() { return outerScope; }
    public Map<String, VarSymbol> getVarMap() { return varMap; }
    public Map<String, FuncSymbol> getFuncMap() { return funcMap; }
    public ArrayList<VarSymbol> getVarList() { return varList; }

    @Override public ScopeType outerScope() { return outerScope; }

    @Override public void addVar(VarSymbol cur) {
        assertNotExistID(cur.getID());
        varMap.put(cur.getID(), cur);
        varList.add(cur);
    }

    @Override public void addFunc(FuncSymbol cur) {
        assertNotExistID(cur.getID());
        funcMap.put(cur.getID(), cur);
    }

    public void addConstructor (FuncSymbol cur) { funcMap.put(cur.getID(), cur); }

    @Override public void addClass(ClassSymbol cur) {
        throw new CompilationError("ClassSymbol: " + cur.getID() + " addClass in Local Scope");
    }

    @Override public boolean existID(String ID) {
        ScopeType globalScope = outerScope();
        while (globalScope instanceof LocalScope) globalScope = globalScope.outerScope();
        return varMap.containsKey(ID) || funcMap.containsKey(ID) || ((GlobalScope) globalScope).getClassMap().containsKey(ID);
    }

    @Override public void assertNotExistID(String ID) {
        if (existID(ID))
            throw new CompilationError("Symbol: " + ID + " conflicted in (Local Scope)");
    }

    @Override
    public Symbol findSymbol(String ID) {
        if (varMap.containsKey(ID)) return varMap.get(ID);
        if (funcMap.containsKey(ID)) return funcMap.get(ID);
        return outerScope().findSymbol(ID);
    }

    @Override
    public ClassSymbol findClassSymbol(String ID) {
        return outerScope().findClassSymbol(ID);
    }
}
