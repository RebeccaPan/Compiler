package Util;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.ParserRuleContext;

public class LocationType {
    private int ln, col;
    public LocationType(int _ln, int _col) { ln = _ln; col = _col; }
    public LocationType(Token token) { ln = token.getLine(); col = token.getCharPositionInLine(); }
    public LocationType(TerminalNode terminalNode) { this(terminalNode.getSymbol()); }
    public LocationType(ParserRuleContext ctx) { this(ctx.start); }
    public int getLn() { return ln; }
    public int getCol() { return col; }
    public String toString() { return getLn() + " " + getCol(); }
}