package AST;

import Util.LocationType;
import Util.Symbol.*;

public class CallFuncExprNode extends ExprNode {
    private ExprNode expr;
    private ExprListNode exprList;
    private FuncSymbol funcSymbol;

    public CallFuncExprNode(LocationType _location, ExprNode _expr, ExprListNode _exprList) {
        super(_location);
        expr = _expr; exprList = _exprList;
    }
    public ExprNode getExpr() { return expr; }
    public ExprListNode getExprList() { return exprList; }
    public Symbol getFuncSymbol() { return funcSymbol; }
    public void setFuncSymbol(FuncSymbol funcSymbol) { this.funcSymbol = funcSymbol; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
