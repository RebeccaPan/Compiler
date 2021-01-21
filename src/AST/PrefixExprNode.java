package AST;

import Util.LocationType;

public class PrefixExprNode extends ExprNode {
    private String op;
    private ExprNode expr;
    public PrefixExprNode(LocationType _location, String _op, ExprNode _expr) {
        super(_location);
        op = _op; expr = _expr;
    }
    public String getOp() { return op; }
    public void setOp(String op) { this.op = op; }
    public ExprNode getExpr() { return expr; }
    public void setExpr(ExprNode expr) { this.expr = expr; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
