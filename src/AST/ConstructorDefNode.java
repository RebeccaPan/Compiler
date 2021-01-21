package AST;

import Util.LocationType;
import Util.Symbol.FuncSymbol;

public class ConstructorDefNode extends DefNode {
    private String funcID;
    private ParaListNode paraList;
    private SuiteNode suite;

    private FuncSymbol funcSymbol;

    public ConstructorDefNode(
            LocationType _location,
            String _funcID,
            ParaListNode _paraList,
            SuiteNode _suite ) {
        super(_location);
        funcID = _funcID; paraList = _paraList; suite = _suite;
    }
    public String getFuncID() { return funcID; }
    public ParaListNode getParaList() { return paraList; }
    public SuiteNode getSuite() { return suite; }

    public FuncSymbol getFuncSymbol() { return funcSymbol; }
    public void setFuncSymbol(FuncSymbol funcSymbol) { this.funcSymbol = funcSymbol; }

    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
