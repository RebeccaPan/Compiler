package AST;

import Util.*;

public class NullLiteralNode extends ExprNode {
    public NullLiteralNode(LocationType _location) {
        super(_location);
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}