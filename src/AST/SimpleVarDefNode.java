package AST;

import Util.LocationType;
import Util.Symbol.VarSymbol;
import Util.Type.*;

public class SimpleVarDefNode extends ASTNode {
    private String varID;
    private ExprNode expr;
    private TypeNode type;
    private VarSymbol varSymbol;
    public SimpleVarDefNode(LocationType _location, String _varID, TypeNode _type, ExprNode _expr) {
        super(_location);
        varID = _varID; type = _type; expr = _expr;
    }
    public String getVarID() { return varID; }
    public void setVarID(String varID) { this.varID = varID; }
    public ExprNode getExpr() { return expr; }
    public void setExpr(ExprNode expr) { this.expr = expr; }
    public TypeNode getType() { return type; }
    public void setType(TypeNode type) { this.type = type; }
    public VarSymbol getVarSymbol() { return varSymbol; }
    public void setVarSymbol(VarSymbol varSymbol) { this.varSymbol = varSymbol; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
