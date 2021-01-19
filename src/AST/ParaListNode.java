package AST;

import Util.LocationType;
import Util.ScopeType;

import java.util.ArrayList;

public class ParaListNode extends ASTNode {
    private ArrayList<ParaNode> paraList;
    public ParaListNode (ScopeType _scope, LocationType _location) {
        super(_scope, _location);
    }
    public void add(ParaNode para) { paraList.add(para); }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
