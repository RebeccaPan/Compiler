package AST;

import Util.LocationType;
import Util.ScopeType;

public class SimpleVarDefNode extends ASTNode {
    private String varID;
    private ExprNode expr;
    public SimpleVarDefNode(ScopeType _scope, LocationType _location, String _varID, ExprNode _expr) {
        super(_scope, _location);
        varID = _varID; expr = _expr;
    }
    public String getVarID() { return varID; }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
