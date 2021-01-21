package AST;

import Util.LocationType;

public class WhileStmtNode extends StmtNode {
    private ExprNode expr;
    private SuiteNode suite;
    public WhileStmtNode(LocationType _location, ExprNode _expr, SuiteNode _suite) {
        super(_location);
        expr = _expr; suite = _suite;
    }
    public ExprNode getExpr() { return expr; }
    public SuiteNode getSuite() { return suite; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
