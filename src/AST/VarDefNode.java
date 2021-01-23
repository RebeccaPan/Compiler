package AST;

import Util.LocationType;

import java.util.ArrayList;

public class VarDefNode extends DefNode {
    private TypeNode type;
    private ArrayList<SimpleVarDefNode> simpleVarDefList;

    public VarDefNode(LocationType _location, TypeNode _type) {
        super(_location); type = _type;
        simpleVarDefList = new ArrayList<>();
    }
    public TypeNode getType() { return type; }
    public void setType(TypeNode type) {
        this.type = type;
        for (var cur : simpleVarDefList)
            cur.setType(type);
    }
    public ArrayList<SimpleVarDefNode> getSimpleVarDefList() { return simpleVarDefList; }
    public void setSimpleVarDefList(ArrayList<SimpleVarDefNode> simpleVarDefList) { this.simpleVarDefList = simpleVarDefList; }
    public void addSimpleVarDef(SimpleVarDefNode _simpleVarDefNode) { simpleVarDefList.add(_simpleVarDefNode); }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
