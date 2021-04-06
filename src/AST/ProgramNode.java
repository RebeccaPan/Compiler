package AST;

import Util.LocationType;

import java.util.ArrayList;

public class ProgramNode extends ASTNode {
    ArrayList<DefNode> defNodeList;

    public ProgramNode(LocationType _location, ArrayList<DefNode> _defNodeList) {
        super(_location);
        defNodeList = _defNodeList;
    }
    public ArrayList<DefNode> getDefNodeList() { return defNodeList; }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
