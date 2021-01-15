package Util;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.ParserRuleContext;

public class LocationType {
    // TODO: LocationType
    private int ln, col;
    public LocationType(int _ln, int _col) { ln = _ln; col = _col; }
    public LocationType(Token token) { ln = token.getline(); col = token.getColumn(); }
//    public LocationType(TerminalNode terminalNode) {}
//    public LocationType(ParserRuleContext ctx) {}
    public int getLn() { return ln; }
    public int getCol() { return col; }
}