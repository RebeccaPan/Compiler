package AST;

import Util.LocationType;

import java.util.ArrayList;

public class ExprListNode extends ASTNode {
    private ArrayList<ExprNode> exprList;
    public ExprListNode(LocationType _location) {
        super(_location);
    }
    public void add(ExprNode expr) { exprList.add(expr); }
    public ArrayList<ExprNode> getExprList() { return exprList; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
