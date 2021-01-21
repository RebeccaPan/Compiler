package AST;

import Util.LocationType;

public class IfStmtNode extends StmtNode {
    private ExprNode cond;
    private StmtNode trueStmt, falseStmt;
    public IfStmtNode (LocationType _location, ExprNode _cond, StmtNode _trueStmt, StmtNode _falseStmt) {
        super(_location);
        cond = _cond;
        trueStmt = _trueStmt;
        falseStmt = _falseStmt;
    }
    public ExprNode getCond() { return cond; }
    public StmtNode getTrueStmt() { return trueStmt; }
    public StmtNode getFalseStmt() { return falseStmt; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
