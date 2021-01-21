package AST;

import Util.*;

public class StrLiteralNode extends ExprNode {
    private String val;
    // constructor
    public StrLiteralNode(LocationType _location, String _val) {
        super(_location);
        this.val = _val;
    }
    public String getVal() { return val; }
    public void setVal(String val) { this.val = val; }
    @Override public void accept(ASTVisitor visitor) { visitor.visit(this); }
}