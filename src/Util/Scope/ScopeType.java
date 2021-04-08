package Util.Scope;

import Util.RegIDAllocator;
import Util.Symbol.*;

public interface ScopeType {
    ScopeType outerScope();
    void addVar(VarSymbol cur);
    void addFunc(FuncSymbol cur);
    void addClass(ClassSymbol cur);
    boolean existID(String ID);
    void assertNotExistID(String ID);
    Symbol findSymbol(String ID);
    ClassSymbol findClassSymbol(String ID);
    VarSymbol findVarSymbol(String ID);
    boolean existVarLocal(String ID);
    boolean existFuncLocal(String ID);
    boolean existClassLocal(String ID);
    RegIDAllocator getRegIDAllocator();

}