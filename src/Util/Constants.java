package Util;

import IR.*;

public class Constants {
    public static final IRReg CONST_ZERO = new IRReg(0, 8, false);
    public static final IRReg CONST_ONE = new IRReg(1, 8, false);
    public static final IRReg CONST_MINUS_ONE = new IRReg(-1, 8, false);

    public static final IRReg CONST_NULL = new IRReg(0, 0, false);
}
