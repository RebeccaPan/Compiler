package AST;

import Util.*;

public class NullLiteralNode extends ExprNode {
    // constructor
    public NullLiteralNode(LocationType _location) {
        super(_location);
    }
    @Override public void accept(ASTVisitor visitor) { visitor.visit(this); }
}