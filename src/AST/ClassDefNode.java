package AST;

import Util.LocationType;
import Util.ScopeType;

import java.util.List;

public class ClassDefNode extends DefNode {
    private String classID;
    private List<VarDefNode> varDefList;
    private List<FuncDefNode> funcDefList;
    private List<ConstructorDefNode> constructorDefList;
    public ClassDefNode(ScopeType _scope, LocationType _location, String _classID) {
        super(_scope, _location);
        classID = _classID;
    }
    public String getClassID() { return classID; }
    public void setClassID(String classID) { this.classID = classID; }
    public List<VarDefNode> getVarDefList() { return varDefList; }
    public List<FuncDefNode> getFuncDefList() { return funcDefList; }
    public List<ConstructorDefNode> getConstructorDefList() { return constructorDefList; }
    public void addVarDef(VarDefNode  _varDefNode) { varDefList.add(_varDefNode); }
    public void addFuncDef(FuncDefNode _funcDefNode) { funcDefList.add(_funcDefNode); }
    public void addConstructorDef(ConstructorDefNode _constructorDefNode) { constructorDefList.add(_constructorDefNode); }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
