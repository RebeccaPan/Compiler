package AST;

import Util.LocationType;
import Util.ScopeType;

import java.util.ArrayList;

public class NewExprNode extends ASTNode {
    private SimpleTypeNode simpleType;
    private ArrayList<ExprNode> dimExprList;
    private int dim;
    public NewExprNode (ScopeType _scope, LocationType _location, SimpleTypeNode _simpleType, ArrayList<ExprNode> _dimExprList, int _dim) {
        super(_scope, _location);
        simpleType = _simpleType; dimExprList = _dimExprList; dim = _dim;
    }
    public void add(ExprNode expr) { dimExprList.add(expr); }
    public int getDim() { return dim; }
    public void setDim(int dim) { this.dim = dim; }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
