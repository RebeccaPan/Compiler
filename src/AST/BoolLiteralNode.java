package AST;

import Util.*;

public class BoolLiteralNode extends ExprNode {
    private boolean val;

    public BoolLiteralNode(LocationType _location, boolean _val) {
        super(_location);
        this.val = _val;
    }
    public boolean getVal() { return val; }
    public void setVal(boolean val) { this.val = val; }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}