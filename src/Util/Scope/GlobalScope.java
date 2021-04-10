package Util.Scope;

import IR.IRReg;
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
    private Map<String, Integer> varIndex;
    private Map<String, IRReg> varReg;
    private int index;

    public GlobalScope() {
        varMap = new LinkedHashMap<>();
        funcMap = new LinkedHashMap<>();
        classMap = new LinkedHashMap<>();
        regIDAllocator = new RegIDAllocator();
        varIndex = new LinkedHashMap<>(); index = 0;
        varReg = new LinkedHashMap<>();
    }

    @Override public ScopeType outerScope() { return null; }

    @Override public void addVar(VarSymbol cur) {
        assertNotExistID(cur.getID());
        varMap.put(cur.getID(), cur);
        varIndex.put(cur.getID(), index++);
        varReg.put(cur.getID(), cur.getReg());
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
    public int findVarIndexLocal(String ID) {
        if (varMap.containsKey(ID)) return varIndex.get(ID);
        throw new CompilationError("String: " + ID + " not found (in Global Scope)");
    }

    @Override
    public IRReg findVarRegLocal(String ID) {
        if (varMap.containsKey(ID)) return varReg.get(ID);
        throw new CompilationError("String: " + ID + " not found (in Global Scope)");
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
        return classMap.containsKey(ID);
    }

    @Override
    public int getVarSize() { return varMap.size(); }

    @Override
    public RegIDAllocator getRegIDAllocator() { return regIDAllocator; }
}
