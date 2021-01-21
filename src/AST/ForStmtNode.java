package AST;

import Util.LocationType;

public class ForStmtNode extends StmtNode {
    private ExprNode init, cond, step;
    private StmtNode stmt;
    public ForStmtNode(LocationType _location, ExprNode _init, ExprNode _cond, ExprNode _step, StmtNode _stmt) {
        super(_location);
        init = _init; cond = _cond; step = _step; stmt = _stmt;
    }
    public ExprNode getInit() { return init; }
    public ExprNode getCond() { return cond; }
    public void setCond(ExprNode cond) { this.cond = cond; }
    public ExprNode getStep() { return step; }
    public StmtNode getStmt() { return stmt; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
