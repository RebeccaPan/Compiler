package AST;

import Util.*;

public class StrLiteralNode extends ExprNode {
    private String val;

    public StrLiteralNode(LocationType _location, String _val) {
        super(_location);
        this.val = _val.substring(1, _val.length() - 1); // delete \" and \"
    }
    public String getVal() { return val; }
    public void setVal(String val) { this.val = val; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}