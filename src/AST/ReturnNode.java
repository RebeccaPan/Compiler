package AST;

import Util.LocationType;
import Util.Symbol.FuncSymbol;

public class ReturnNode extends StmtNode {
    private ExprNode retExpr;
    private boolean withRet;
    private FuncSymbol funcSymbol;
    public ReturnNode(LocationType _location, ExprNode _retExpr, boolean _withRet) {
        super(_location);
        retExpr = _retExpr; withRet = _withRet;
    }
    public boolean isWithRet() { return withRet; }
    public ExprNode getRetExpr() { return retExpr; }
    public void setRetExpr(ExprNode retExpr) { this.retExpr = retExpr; }
    public FuncSymbol getFuncSymbol() { return funcSymbol; }
    public void setFuncSymbol(FuncSymbol funcSymbol) { this.funcSymbol = funcSymbol; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
