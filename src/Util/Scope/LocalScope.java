package Util.Scope;

import IR.IRReg;
import Util.CompilationError;
import Util.RegIDAllocator;
import Util.Symbol.ClassSymbol;
import Util.Symbol.FuncSymbol;
import Util.Symbol.Symbol;
import Util.Symbol.VarSymbol;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class LocalScope extends ScopeType {
    private ScopeType outerScope;
    private ArrayList<VarSymbol> varList = new ArrayList<>();

    public LocalScope(ScopeType _outerScope) {
        outerScope = _outerScope;
        regIDAllocator = _outerScope.getRegIDAllocator();
    }

    public ArrayList<VarSymbol> getVarList() { return varList; }

    @Override
    public ScopeType outerScope() { return outerScope; }

    @Override
    public void addVar(VarSymbol cur) {
        assertNotExistID(cur.getID());
        varMap.put(cur.getID(), cur);
        varIndex.put(cur.getID(), index++);
        varReg.put(cur.getID(), cur.getReg());
        varList.add(cur);
    }

    public void addConstructor(FuncSymbol cur) { funcMap.put(cur.getID(), cur); }

    @Override
    public void addClass(ClassSymbol cur) {
        throw new CompilationError("ClassSymbol: " + cur.getID() + " addClass in Local Scope");
    }

    @Override
    public boolean existID(String ID) {
        ScopeType globalScope = outerScope();
        while (globalScope instanceof LocalScope) globalScope = globalScope.outerScope();
        return varMap.containsKey(ID) || funcMap.containsKey(ID) || ((GlobalScope) globalScope).getClassMap().containsKey(ID);
    }

    @Override
    public void assertNotExistID(String ID) {
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

    @Override
    public VarSymbol findVarSymbol(String ID) {
        if (varMap.containsKey(ID)) return varMap.get(ID);
        return outerScope().findVarSymbol(ID);
    }

    @Override
    public boolean existClassLocal(String ID) {
        return false;
    }
}
