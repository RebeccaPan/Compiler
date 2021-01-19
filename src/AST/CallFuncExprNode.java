package AST;

import Util.LocationType;
import Util.ScopeType;

import java.util.ArrayList;

public class CallFuncExprNode extends ExprNode {
    private ExprNode expr;
    private ExprListNode exprList;
    public CallFuncExprNode(ScopeType _scope, LocationType _location, ExprNode _expr, ExprListNode _exprList) {
        super(_scope, _location);
        expr = _expr; exprList = _exprList;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
