package AST;

import Util.LocationType;

public class SubscriptExprNode extends ExprNode {
    private ExprNode arr, index;

    public SubscriptExprNode(LocationType _location, ExprNode _arr, ExprNode _index) {
        super(_location);
        arr = _arr; index = _index;
    }
    public ExprNode getArr() { return arr; }
    public void setArr(ExprNode arr) { this.arr = arr; }
    public ExprNode getIndex() { return index; }
    public void setIndex(ExprNode index) { this.index = index; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
