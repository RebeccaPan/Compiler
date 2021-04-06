package AST;

import Util.LocationType;

public class ForStmtNode extends StmtNode {
    private ExprNode init, cond, step;
    private SuiteNode suite;

    public ForStmtNode(LocationType _location, ExprNode _init, ExprNode _cond, ExprNode _step, SuiteNode _suite) {
        super(_location);
        init = _init; cond = _cond; step = _step; suite = _suite;
    }
    public ExprNode getInit() { return init; }
    public ExprNode getCond() { return cond; }
    public void setCond(ExprNode cond) { this.cond = cond; }
    public ExprNode getStep() { return step; }
    public SuiteNode getSuite() { return suite; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
