package AST;

import Util.CompilationError;
import Util.Type.*;
import Util.LocationType;

abstract public class ExprNode extends ASTNode{
    private Type type;
    public enum ExprCat {LVal, RVal, Class, Func};
    private ExprCat exprCat;

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public ExprCat getExprCat() { return exprCat; }
    public void setExprCat(ExprCat exprCat) { this.exprCat = exprCat; }
    public void assertIsVal(LocationType curLoc) {
        if (!(exprCat == ExprCat.LVal || exprCat == ExprCat.RVal))
            throw new CompilationError("Expr not as LVal or RVal", curLoc);
    }

    public ExprNode(LocationType _location) {
        super(_location);
    }
}
