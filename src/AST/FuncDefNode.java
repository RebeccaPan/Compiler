package AST;

import Util.LocationType;
import Util.ScopeType;

public class FuncDefNode extends DefNode {
    private TypeNode type;
    private String funcID;
    private ParaListNode paraList;
    private SuiteNode suite;
    public FuncDefNode(
            ScopeType _scope,
            LocationType _location,
            TypeNode _type,
            String _funcID,
            ParaListNode _paraList,
            SuiteNode _suite ) {
        super(_scope, _location);
        type = _type; funcID = _funcID; paraList = _paraList; suite = _suite;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
