package AST;

import Util.LocationType;
import Util.Symbol.*;

public class ClassMemberExprNode extends ExprNode {
    private ExprNode expr;
    private String ID;

    private Symbol symbol;

    public ClassMemberExprNode(LocationType _location, ExprNode _expr, String _ID) {
        super(_location);
        expr = _expr;
        ID = _ID;
    }
    public ExprNode getExpr() { return expr; }
    public String getID() { return ID; }
    public Symbol getSymbol() { return symbol; }
    public void setSymbol(Symbol symbol) { this.symbol = symbol; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
