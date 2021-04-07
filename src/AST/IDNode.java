package AST;

import Util.LocationType;
import Util.Symbol.*;

public class IDNode extends ExprNode {
    private String ID;
    private Symbol symbol;

    public IDNode(LocationType _location, String _ID) {
        super(_location); ID = _ID;
    }
    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }
    public Symbol getSymbol() { return symbol; }
    public void setSymbol(Symbol symbol) { this.symbol = symbol; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
