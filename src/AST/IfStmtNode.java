package AST;

import Util.LocationType;

public class IfStmtNode extends StmtNode {
    private ExprNode cond;
    private SuiteNode trueSuite, falseSuite;
    public IfStmtNode (LocationType _location, ExprNode _cond, SuiteNode _trueSuite, SuiteNode _falseSuite) {
        super(_location);
        cond = _cond;
        trueSuite = _trueSuite;
        falseSuite = _falseSuite;
    }
    public ExprNode getCond() { return cond; }
    public SuiteNode getTrueSuite() { return trueSuite; }
    public SuiteNode getFalseSuite() { return falseSuite; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
