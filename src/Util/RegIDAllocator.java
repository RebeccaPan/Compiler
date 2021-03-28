package Util;

import IR.IRReg;

public class RegIDAllocator {
    private int[] curRegID; // index: typeID = 1~11

    public RegIDAllocator() {
        curRegID = new int[15];
    }

    public int size(int type) { return curRegID[type]; }

    public IRReg allocate(int type) { return new IRReg(curRegID[type]++, type, false); }
}
