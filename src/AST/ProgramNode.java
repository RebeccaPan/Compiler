package AST;

import Util.LocationType;
import Util.ScopeType;

import java.util.ArrayList;

public class ProgramNode extends ASTNode {
    ArrayList<DefNode> defNodeList;
    public ProgramNode(ScopeType _scope, LocationType _location, ArrayList<DefNode> _defNodeList) {
        super(_scope, _location);
        defNodeList = _defNodeList;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
