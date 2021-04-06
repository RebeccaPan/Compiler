package AST;

import Util.LocationType;

public class ContinueNode extends StmtNode {
    private ASTNode next;

    public ContinueNode(LocationType _location) {
        super(_location);
    }
    public ASTNode getNext() { return next; }
    public void setNext(ASTNode next) { this.next = next; }
    
    @Override
    public void accept(ASTVisitor visitor) { visitor.visit(this); }
}
