package AST;

import Util.LocationType;
import Util.ScopeType;

import java.util.ArrayList;

public class ClassDefNode extends DefNode {
    private String classID;
    private ArrayList<VarDefNode> varDefList;
    private ArrayList<FuncDefNode> funcDefList;
    private ArrayList<ConstructorDefNode> constructorDefList;
    public ClassDefNode(ScopeType _scope, LocationType _location, String _classID) {
        super(_scope, _location);
        classID = _classID;
    }
    public String getClassID() { return classID; }
    public void setClassID(String classID) { this.classID = classID; }
    public ArrayList<VarDefNode> getVarDefList() { return varDefList; }
    public ArrayList<FuncDefNode> getFuncDefList() { return funcDefList; }
    public ArrayList<ConstructorDefNode> getConstructorDefList() { return constructorDefList; }
    public void addVarDef(VarDefNode  _varDefNode) { varDefList.add(_varDefNode); }
    public void addFuncDef(FuncDefNode _funcDefNode) { funcDefList.add(_funcDefNode); }
    public void addConstructorDef(ConstructorDefNode _constructorDefNode) { constructorDefList.add(_constructorDefNode); }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
