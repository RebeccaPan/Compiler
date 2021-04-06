package AST;

import Util.LocationType;

import java.util.ArrayList;

public class SuiteNode extends StmtNode {
    private ArrayList<StmtNode> stmtNodeList;

    public SuiteNode (LocationType _location, ArrayList<StmtNode> _stmtNodeList) {
        super(_location);
        stmtNodeList = _stmtNodeList;
    }
    public ArrayList<StmtNode> getStmtNodeList() { return stmtNodeList; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
