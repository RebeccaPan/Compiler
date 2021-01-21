package AST;

import Util.LocationType;

public class ParaNode extends ASTNode {
    private TypeNode type;
    private String paraID;
    public ParaNode (LocationType _location, TypeNode _type, String _paraID) {
        super(_location);
        type = _type; paraID = _paraID;
    }
    public TypeNode getType() { return type; }
    public String getParaID() { return paraID; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
