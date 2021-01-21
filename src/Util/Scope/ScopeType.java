package Util.Scope;

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
}