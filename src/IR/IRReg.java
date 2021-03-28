package IR;

public class IRReg {
    private int ID, type; // the ID-th of typeID
    private boolean isPtr;
    private int usedID = -1;

    public IRReg(int _ID, int _type, boolean _isPtr) {
        ID = _ID; type = _type; isPtr = _isPtr;
    }

    public void print() {
        // used by IRLine.print() regList
        String typeStr = switch (type) {
            case 1 -> "L";
            case 2 -> "G";
            case 3 -> "P";
            case 4 -> "F";
            case 5 -> "T";
            case 6 -> "*T";
            case 7 -> "LP";
            case 8 -> "i";
            case 9 -> "S";
            case 10 -> "Q";
            case 11 -> "C";
            default -> ""; // no-print
        };
        System.out.print(typeStr + ID);
    }

    public String toASM() {
        String ret = null;
        switch (ID) {
            case 0 -> ret = "zero";
            case 1 -> ret = "ra";
            case 2 -> ret = "sp";
            case 3 -> ret = "gp";
            case 4 -> ret = "tp";
            case 5 -> ret = "t0";
            case 6 -> ret = "t1";
            case 7 -> ret = "t2";
            case 8 -> ret = "s0";
            case 9 -> ret = "s1";
            case 10 -> ret = "a0";
            case 11 -> ret = "a1";
            case 12 -> ret = "a2";
            case 13 -> ret = "a3";
            case 14 -> ret = "a4";
            case 15 -> ret = "a5";
            case 16 -> ret = "a6";
            case 17 -> ret = "a7";
            case 18 -> ret = "s2";
            case 19 -> ret = "s3";
            case 20 -> ret = "s4";
            case 21 -> ret = "s5";
            case 22 -> ret = "s6";
            case 23 -> ret = "s7";
            case 24 -> ret = "s8";
            case 25 -> ret = "s9";
            case 26 -> ret = "s10";
            case 27 -> ret = "s11";
            case 28 -> ret = "t3";
            case 29 -> ret = "t4";
            case 30 -> ret = "t5";
            case 31 -> ret = "t6";
        }
        return ret;
    }

    public String toGASM(){
        return ".G" + ID;
    }

    public String toSASM(){
        return ".LS" + ID;
    }

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    public boolean isPtr() { return isPtr; }
    public void setPtr(boolean ptr) { isPtr = ptr; }
    public int getUsedID() { return usedID; }
    public void setUsedID(int usedID) { this.usedID = usedID; }
}
