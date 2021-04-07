package AST;

import Util.LocationType;
import Util.Symbol.ClassSymbol;

import java.util.ArrayList;

public class ClassDefNode extends DefNode {
    private String classID;
    private ArrayList<VarDefNode> varDefList;
    private ArrayList<FuncDefNode> funcDefList;
    private ArrayList<ConstructorDefNode> constructorDefList;
    private ClassSymbol classSymbol;

    public ClassDefNode(LocationType _location, String _classID) {
        super(_location);
        classID = _classID;
        varDefList = new ArrayList<>();
        funcDefList = new ArrayList<>();
        constructorDefList = new ArrayList<>();
    }
    public String getClassID() { return classID; }
    public void setClassID(String classID) { this.classID = classID; }
    public ArrayList<VarDefNode> getVarDefList() { return varDefList; }
    public void setVarDefList(ArrayList<VarDefNode> varDefList) { this.varDefList = varDefList; }
    public ArrayList<FuncDefNode> getFuncDefList() { return funcDefList; }
    public void setFuncDefList(ArrayList<FuncDefNode> funcDefList) { this.funcDefList = funcDefList; }
    public ArrayList<ConstructorDefNode> getConstructorDefList() { return constructorDefList; }
    public void setConstructorDefList(ArrayList<ConstructorDefNode> constructorDefList) { this.constructorDefList = constructorDefList; }
    public ClassSymbol getClassSymbol() { return classSymbol; }
    public void setClassSymbol(ClassSymbol classSymbol) { this.classSymbol = classSymbol; }

    public void addVarDef(VarDefNode  _varDefNode) { varDefList.add(_varDefNode); }
    public void addFuncDef(FuncDefNode _funcDefNode) { funcDefList.add(_funcDefNode); }
    public void addConstructorDef(ConstructorDefNode _constructorDefNode) { constructorDefList.add(_constructorDefNode); }

    public void addDefaultConstructorDef() {
        ConstructorDefNode defaultConstructor = new ConstructorDefNode(
                this.getLocation(),
                this.classID,
                null,
                null );
        constructorDefList.add(defaultConstructor);
    }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
