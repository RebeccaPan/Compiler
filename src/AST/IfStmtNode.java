package AST;

import Util.LocationType;
import Util.ScopeType;

import java.util.ArrayList;

public class IfStmtNode extends StmtNode {
    private StmtNode trueStmt, falseStmt;
    public IfStmtNode (ScopeType _scope, LocationType _location, StmtNode _trueStmt, StmtNode _falseStmt) {
        super(_scope, _location);
        trueStmt = _trueStmt;
        falseStmt = _falseStmt;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
