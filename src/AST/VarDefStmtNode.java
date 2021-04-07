package AST;

import Util.LocationType;

import java.util.ArrayList;

public class VarDefStmtNode extends StmtNode {
    private VarDefNode varDefNode;

    public VarDefStmtNode(LocationType _location, VarDefNode _varDefNode) {
        super(_location); varDefNode = _varDefNode;
    }
    public VarDefNode getVarDefNode() { return varDefNode; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
