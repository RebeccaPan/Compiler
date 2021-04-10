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

public class LocalScope implements ScopeType {
    private ScopeType outerScope;
    private Map<String, VarSymbol> varMap;
    private Map<String, FuncSymbol> funcMap;
    private ArrayList<VarSymbol> varList;
    private RegIDAllocator regIDAllocator;
    private Map<String, Integer> varIndex;
    private Map<String, IRReg> varReg;
    private int index;

    public LocalScope(ScopeType _outerScope) {
        outerScope = _outerScope;
        varMap = new LinkedHashMap<>();
        funcMap = new LinkedHashMap<>();
        varList = new ArrayList<>();
        regIDAllocator = _outerScope.getRegIDAllocator();
        varIndex = new LinkedHashMap<>(); index = 0;
        varReg = new LinkedHashMap<>();
    }

    public ScopeType getOuterScope() { return outerScope; }
    public Map<String, VarSymbol> getVarMap() { return varMap; }
    public Map<String, FuncSymbol> getFuncMap() { return funcMap; }
    public ArrayList<VarSymbol> getVarList() { return varList; }

    @Override
    public ScopeType outerScope() { return outerScope; }

    @Override
    public void addVar(VarSymbol cur) {
        assertNotExistID(cur.getID());
        varMap.put(cur.getID(), cur);
        varList.add(cur);
        varIndex.put(cur.getID(), index++);
        varReg.put(cur.getID(), cur.getReg());
    }

    @Override
    public void addFunc(FuncSymbol cur) {
        assertNotExistID(cur.getID());
        funcMap.put(cur.getID(), cur);
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
    public int findVarIndexLocal(String ID) {
        if (varMap.containsKey(ID)) return varIndex.get(ID);
        throw new CompilationError("String: " + ID + " not found (in Local Scope)");
    }

    @Override
    public IRReg findVarRegLocal(String ID) {
        if (varMap.containsKey(ID)) return varReg.get(ID);
        throw new CompilationError("String: " + ID + " not found (in Local Scope)");
    }

    @Override
    public boolean existVarLocal(String ID) {
        return varMap.containsKey(ID);
    }

    @Override
    public boolean existFuncLocal(String ID) {
        return funcMap.containsKey(ID);
    }

    @Override
    public boolean existClassLocal(String ID) {
        return false;
    }

    @Override
    public int getVarSize() { return varMap.size(); }

    @Override
    public RegIDAllocator getRegIDAllocator() { return regIDAllocator; }
}
