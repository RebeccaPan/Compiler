package AST;

import Util.LocationType;
import Util.ScopeType;

import java.util.List;

public class ProgramNode extends ASTNode {
    List<DefNode> defNodeList;
    public ProgramNode(ScopeType _scope, LocationType _location, List<DefNode> _defNodeList) {
        super(_scope, _location);
        defNodeList = _defNodeList;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
