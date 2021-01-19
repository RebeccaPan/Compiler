package AST;

import Util.LocationType;
import Util.ScopeType;

import java.util.ArrayList;

public class SuiteNode extends ASTNode {
    private ArrayList<StmtNode> stmtNodeList;
    public SuiteNode (ScopeType _scope, LocationType _location, ArrayList<StmtNode> _stmtNodeList) {
        super(_scope, _location);
        stmtNodeList = _stmtNodeList;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
