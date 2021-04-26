package Util.Scope;

import IR.IRReg;
import Util.CompilationError;
import Util.RegIDAllocator;
import Util.Symbol.*;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ScopeType {
    protected Map<String, VarSymbol> varMap = new LinkedHashMap<>();
    protected Map<String, FuncSymbol> funcMap = new LinkedHashMap<>();
    public RegIDAllocator regIDAllocator;
    protected Map<String, Integer> varIndex = new LinkedHashMap<>();
    protected Map<String, IRReg> varReg = new LinkedHashMap<>();
    protected int index = 0;

    abstract public ScopeType outerScope();

    abstract public void addVar(VarSymbol cur);
    public void addFunc(FuncSymbol cur) {
        assertNotExistID(cur.getID());
        funcMap.put(cur.getID(), cur);
    }
    abstract public void addClass(ClassSymbol cur);

    abstract public boolean existID(String ID);
    abstract public void assertNotExistID(String ID);
    abstract public Symbol findSymbol(String ID);
    abstract public ClassSymbol findClassSymbol(String ID);
    abstract public VarSymbol findVarSymbol(String ID);

    public int findVarIndexLocal(String ID) {
        if (varMap.containsKey(ID)) return varIndex.get(ID);
        throw new CompilationError("String: " + ID + " not found in current Scope");
    }
    public IRReg findVarRegLocal(String ID) {
        if (varMap.containsKey(ID)) return varReg.get(ID);
        throw new CompilationError("String: " + ID + " not found in current Scope");
    }
    public boolean existVarLocal(String ID) {
        return varMap.containsKey(ID);
    }
    public boolean existFuncLocal(String ID) {
        return funcMap.containsKey(ID);
    }
    abstract public boolean existClassLocal(String ID);
    public int getVarSize() {
        return varMap.size();
    }
    public RegIDAllocator getRegIDAllocator() {
        return regIDAllocator;
    }
}