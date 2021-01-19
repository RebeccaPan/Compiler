package AST;

import Util.LocationType;
import Util.ScopeType;

public class ForStmtNode extends StmtNode {
    private ExprNode init, cond;
    private StmtNode stmt;
    public ForStmtNode(ScopeType _scope, LocationType _location, ExprNode _init, ExprNode _cond, StmtNode _stmt) {
        super(_scope, _location);
        init = _init; cond = _cond; stmt = _stmt;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
