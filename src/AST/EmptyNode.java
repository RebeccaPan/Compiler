package AST;

import Util.LocationType;

public class EmptyNode extends StmtNode {
    public EmptyNode(LocationType _location) { super(_location); }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
