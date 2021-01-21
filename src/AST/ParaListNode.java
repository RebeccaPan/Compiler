package AST;

import Util.LocationType;

import java.util.ArrayList;

public class ParaListNode extends ASTNode {
    private ArrayList<ParaNode> paraList;
    public ParaListNode (LocationType _location) {
        super(_location);
    }
    public void add(ParaNode para) { paraList.add(para); }
    public ArrayList<ParaNode> getParaList() { return paraList; }
    public void setParaList(ArrayList<ParaNode> paraList) { this.paraList = paraList; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
