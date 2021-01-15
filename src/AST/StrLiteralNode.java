package AST;

import Util.*;

public class StrLiteralNode extends ASTNode {
    private String val;
    // constructor
    public StrLiteralNode(ScopeType _scope, LocationType _location, String _val) {
        super(_scope, _location);
        this.val = _val;
    }
    public String getVal() { return val; }
    public void setVal(String val) { this.val = val; }
    @Override public void accept(ASTVisitor visitor) { visitor.visit(this); }
}