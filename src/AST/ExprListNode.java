package AST;

import Util.LocationType;
import Util.ScopeType;

import java.util.List;

public class ExprListNode extends ASTNode {
    private List<ExprNode> exprList;
    public ExprListNode(ScopeType _scope, LocationType _location) {
        super(_scope, _location);
    }
    public void add(ExprNode expr) { exprList.add(expr); }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
