package AST;

import Util.LocationType;
import Util.Symbol.*;

public class ThisNode extends ExprNode {
    private Symbol Symbol;
    public ThisNode(LocationType _location) {
        super(_location);
    }
    public Util.Symbol.Symbol getSymbol() { return Symbol; }
    public void setSymbol(Util.Symbol.Symbol symbol) { Symbol = symbol; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
