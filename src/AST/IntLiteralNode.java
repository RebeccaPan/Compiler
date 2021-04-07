package AST;

import Util.*;

public class IntLiteralNode extends ExprNode {
    private int val;

    public IntLiteralNode(LocationType _location, int _val) {
        super(_location);
        this.val = _val;
    }

    public int getVal() { return val; }
    public void setVal(int val) { this.val = val; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}