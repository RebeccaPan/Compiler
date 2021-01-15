package AST;

import Util.*;

public class IntLiteralNode extends ASTNode {
    private int val;
    // constructor
    public IntLiteralNode(ScopeType _scope, LocationType _location, int _val) {
        super(_scope, _location);
        this.val = _val;
    }
    public int getVal() { return val; }
    public void setVal(int val) { this.val = val; }
    @Override public void accept(ASTVisitor visitor) { visitor.visit(this); }
}