package AST;

import Util.LocationType;
import Util.ScopeType;

public class ConstructorDefNode extends DefNode {
    private String funcID;
    private ParaListNode paraList;
    private SuiteNode suite;
    public ConstructorDefNode(
            ScopeType _scope,
            LocationType _location,
            String _funcID,
            ParaListNode _paraList,
            SuiteNode _suite ) {
        super(_scope, _location);
        funcID = _funcID; paraList = _paraList; suite = _suite;
    }
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
