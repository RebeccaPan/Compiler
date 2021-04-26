package Optimize;

import IR.IRBlockList;
import IR.IRReg;

public abstract class Opt {
    protected final boolean isZero(IRReg reg) { return reg.getID() == 0; }
    protected final boolean isOne(IRReg reg) { return reg.getID() == 1 && reg.getType() == 8; }
    protected final boolean isSame(IRReg r1, IRReg r2) { return r1.getID() == r2.getID(); }

    protected boolean updated = false;
    protected IRBlockList curBlockList;
    public Opt(IRBlockList _curBlockList) { curBlockList = _curBlockList; }
    public abstract void opt();
}
