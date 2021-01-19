package AST;

import Util.LocationType;
import Util.ScopeType;

public class WhileStmtNode extends StmtNode {
    private ExprNode expr;
    private StmtNode stmt;
    public WhileStmtNode(ScopeType _scope, LocationType _location, ExprNode _expr, StmtNode _stmt) {
        super(_scope, _location);
        expr = _expr; stmt = _stmt;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
