package AST;

import Util.LocationType;

public class DefNode extends ASTNode {
    public DefNode(LocationType _location) {
        super(_location);
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
