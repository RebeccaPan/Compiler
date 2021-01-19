package AST;

import Util.LocationType;
import Util.ScopeType;

import java.util.ArrayList;

public class VarDefNode extends DefNode {
    private TypeNode type;
    private ArrayList<SimpleVarDefNode> simpleVarDefList;
    public VarDefNode(ScopeType _scope, LocationType _location, TypeNode _type) {
        super(_scope, _location); type = _type;
        simpleVarDefList = new ArrayList<>();
    }
    public TypeNode getType() { return type; }
    public void addSimpleVarDef(SimpleVarDefNode _simpleVarDefNode) { simpleVarDefList.add(_simpleVarDefNode); }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
