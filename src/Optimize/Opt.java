package Optimize;

import IR.IRBlockList;
import IR.IRReg;

public abstract class Opt {
    protected final boolean isZero(IRReg reg) { return reg.getID() == 0 && reg.getType() == 8; }
    protected final boolean isOne(IRReg reg) { return reg.getID() == 1 && reg.getType() == 8; }
    protected final boolean isSame(IRReg r1, IRReg r2) { return r1.getID() == r2.getID() && r1.getType() == r2.getType(); }
    protected final boolean validImm(IRReg reg) { return reg.getID() >= -2000 && reg.getID() <= 2000; }

    protected boolean updated = false;
    protected IRBlockList curBlockList;
    public Opt(IRBlockList _curBlockList) { curBlockList = _curBlockList; }
    public abstract void opt();
}
