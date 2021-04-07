package Util.Scope;

import Util.CompilationError;
import Util.RegIDAllocator;
import Util.Symbol.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalScope implements ScopeType {
    private Map<String, VarSymbol> varMap;
    private Map<String, FuncSymbol> funcMap;
    private Map<String, ClassSymbol> classMap;
    public RegIDAllocator regIDAllocator;

    public GlobalScope() {
        varMap = new LinkedHashMap<>();
        funcMap = new LinkedHashMap<>();
        classMap = new LinkedHashMap<>();
        regIDAllocator = new RegIDAllocator();
    }

    @Override public ScopeType outerScope() { return null; }

    @Override public void addVar(VarSymbol cur) {
        assertNotExistID(cur.getID());
        varMap.put(cur.getID(), cur);
    }

    @Override public void addFunc(FuncSymbol cur) {
        assertNotExistID(cur.getID());
        funcMap.put(cur.getID(), cur);
    }

    @Override public void addClass(ClassSymbol cur) {
        assertNotExistID(cur.getID());
        classMap.put(cur.getID(), cur);
    }

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

    @Override
    public ClassSymbol findClassSymbol(String ID) {
        if (classMap.containsKey(ID)) return classMap.get(ID);
        throw new CompilationError("Symbol: " + ID + " not found (in Global Scope)");
    }

    @Override
    public VarSymbol findVarSymbol(String ID) {
        if (varMap.containsKey(ID)) return varMap.get(ID);
        throw new CompilationError("Symbol: " + ID + " not found (in Global Scope)");
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
    public RegIDAllocator getRegIDAllocator() { return regIDAllocator; }
}
