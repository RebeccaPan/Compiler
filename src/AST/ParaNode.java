package AST;

import Util.LocationType;
import Util.ScopeType;

public class ParaNode extends ASTNode {
    private TypeNode type;
    private String paraID;
    public ParaNode (ScopeType _scope, LocationType _location, TypeNode _type, String _paraID) {
        super(_scope, _location);
        type = _type; paraID = _paraID;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
