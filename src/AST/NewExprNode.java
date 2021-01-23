package AST;

import Util.LocationType;
import java.util.ArrayList;

public class NewExprNode extends ExprNode {
    private SimpleTypeNode simpleType;
    private ArrayList<ExprNode> dimExprList;
    private int dim;

    public NewExprNode (LocationType _location, SimpleTypeNode _simpleType, ArrayList<ExprNode> _dimExprList, int _dim) {
        super(_location);
        simpleType = _simpleType; dimExprList = _dimExprList; dim = _dim;
        if (dimExprList == null) dimExprList = new ArrayList<>();
    }
    public void add(ExprNode expr) { dimExprList.add(expr); }
    public SimpleTypeNode getSimpleType() { return simpleType; }
    public void setSimpleType(SimpleTypeNode simpleType) { this.simpleType = simpleType; }
    public ArrayList<ExprNode> getDimExprList() { return dimExprList; }
    public void setDimExprList(ArrayList<ExprNode> dimExprList) { this.dimExprList = dimExprList; }
    public int getDim() { return dim; }
    public void setDim(int dim) { this.dim = dim; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
