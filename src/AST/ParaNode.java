package AST;

import Util.LocationType;
import Util.Symbol.VarSymbol;

public class ParaNode extends ASTNode {
    private TypeNode type;
    private String paraID;
    private VarSymbol varSymbol;

    public ParaNode (LocationType _location, TypeNode _type, String _paraID) {
        super(_location);
        type = _type; paraID = _paraID;
    }
    public TypeNode getType() { return type; }
    public String getParaID() { return paraID; }
    public void setVarSymbol(VarSymbol varSymbol) {this.varSymbol = varSymbol;}

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
