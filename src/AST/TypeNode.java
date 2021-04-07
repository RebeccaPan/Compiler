package AST;

import Util.LocationType;

public class TypeNode extends ASTNode {
    private SimpleTypeNode simpleTypeNode;
    private int dim;

    public TypeNode (LocationType _location, SimpleTypeNode _simpleTypeNode, int _dim) {
        super(_location);
        simpleTypeNode = _simpleTypeNode;
        dim = _dim;
    }
    public SimpleTypeNode getSimpleTypeNode() { return simpleTypeNode; }
    public void setSimpleTypeNode(SimpleTypeNode simpleTypeNode) { this.simpleTypeNode = simpleTypeNode; }
    public int getDim() { return dim; }
    public void setDim(int dim) { this.dim = dim; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
