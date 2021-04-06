package AST;

import Util.LocationType;
import Util.Symbol.FuncSymbol;

public class FuncDefNode extends DefNode {
    private TypeNode type;
    private String funcID;
    private ParaListNode paraList;
    private SuiteNode suite;
    private FuncSymbol funcSymbol;

    public FuncDefNode(
            LocationType _location,
            TypeNode _type,
            String _funcID,
            ParaListNode _paraList,
            SuiteNode _suite ) {
        super(_location);
        type = _type; funcID = _funcID; paraList = _paraList; suite = _suite;
    }
    public TypeNode getType() { return type; }
    public void setType(TypeNode type) { this.type = type; }
    public String getFuncID() { return funcID; }
    public void setFuncID(String funcID) { this.funcID = funcID; }
    public ParaListNode getParaList() { return paraList; }
    public void setParaList(ParaListNode paraList) { this.paraList = paraList; }
    public SuiteNode getSuite() { return suite; }
    public void setSuite(SuiteNode suite) { this.suite = suite; }
    public FuncSymbol getFuncSymbol() { return funcSymbol; }
    public void setFuncSymbol(FuncSymbol funcSymbol) { this.funcSymbol = funcSymbol; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
