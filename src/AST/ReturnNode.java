package AST;

import Util.LocationType;
import Util.ScopeType;

public class ReturnNode extends StmtNode {
    private ExprNode retExpr;
    private boolean useRet;
    public ReturnNode(ScopeType _scope, LocationType _location, ExprNode _retExpr, boolean _useRet) {
        super(_scope, _location);
        retExpr = _retExpr; useRet = _useRet;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
