package IR;

import java.util.ArrayList;

public class IRLine {
    public enum OPCODE { // TODO: BLE BLE BLT BGE BGT, ANDI SLTI, EQI, MULI
        FUNC, LABEL, MOVE, JUMP, CALL, BNEQ, BEQ, NEG, NOT, LOGICNOT,
        EQ, NEQ, GE, GEQ, LE, LEQ, ADD, SUB, MUL, DIV, MOD,
        OR, AND, XOR, SHL, SHR, INDEX, LOAD, LOADSTRING, RETURN,
        ADDI, ANDI, SLTI, /*EQI, MULI, */LW, SW }
    private OPCODE opcode;
    private ArrayList<IRReg> regList = new ArrayList<>();
    private int label = 0;
    private String funcStr = null;

    public IRLine(OPCODE _opcode) { opcode = _opcode; }

    public boolean isDefLine() {
        return ! ( opcode == OPCODE.FUNC
                || opcode == OPCODE.LABEL
                || opcode == OPCODE.JUMP
                || opcode == OPCODE.CALL
                || opcode == OPCODE.BEQ
                || opcode == OPCODE.BNEQ
                || opcode == OPCODE.SW ) ;
    }

    public int regLoc() {
        return switch (opcode) {
            case FUNC, LABEL, JUMP, CALL -> -1;
            case SW, BEQ, BNEQ -> 0;
            default -> 1;
        };
    }

    public void print() {
        switch (opcode) {
            case FUNC     -> System.out.print("FUNC");
            case LABEL    -> System.out.print("LABEL");
            case MOVE     -> System.out.print("\tMOVE");
            case JUMP     -> System.out.print("\tJUMP");
            case CALL     -> System.out.print("\tCALL");
            case BNEQ     -> System.out.print("\tBNEQ");
            case BEQ      -> System.out.print("\tBEQ");
            case NEG      -> System.out.print("\tNEG");
            case NOT      -> System.out.print("\tNOT");
            case LOGICNOT -> System.out.print("\tLOGICNOT");
            case EQ       -> System.out.print("\tEQ");
            case NEQ      -> System.out.print("\tNEQ");
            case GE       -> System.out.print("\tGE");
            case GEQ      -> System.out.print("\tGEQ");
            case LE       -> System.out.print("\tLE");
            case LEQ      -> System.out.print("\tLEQ");
            case ADD      -> System.out.print("\tADD");
            case SUB      -> System.out.print("\tSUB");
            case MUL      -> System.out.print("\tMUL");
            case DIV      -> System.out.print("\tDIV");
            case MOD      -> System.out.print("\tMOD");
            case OR       -> System.out.print("\tOR");
            case AND      -> System.out.print("\tAND");
            case XOR      -> System.out.print("\tXOR");
            case SHL      -> System.out.print("\tSHL");
            case SHR      -> System.out.print("\tSHR");
            case INDEX    -> System.out.print("\tINDEX");
            case LOAD     -> System.out.print("\tLOAD");
            case LOADSTRING -> System.out.print("\tLOADSTRING");
            case RETURN   -> System.out.print("\tRETURN");
            case ADDI     -> System.out.print("\tADDI");
            case ANDI     -> System.out.print("\tANDI");
            case SLTI     -> System.out.print("\tSLTI");
//            case EQI      -> System.out.print("\tEQI");
//            case MULI     -> System.out.print("\tMULI");
            case LW       -> System.out.print("\tLW");
            case SW       -> System.out.print("\tSW");
        }
        if (label > 0) {
            System.out.printf("(%d)", label);
        }
        if (funcStr != null) {
            System.out.print(" " + funcStr);
        }
        for (IRReg reg : regList) {
            System.out.print(" ");
            reg.print();
        }
        System.out.print("\n");
    }

    public void printASM(IRBlock block) {
        String str = "";
        String reg0 = regList.size() > 0 ? regList.get(0).toASM() : null;
        String reg1 = regList.size() > 1 ? regList.get(1).toASM() : null;
        String reg2 = regList.size() > 2 ? regList.get(2).toASM() : null;
        String labelStr = ".b" + block.getIndex() + "l" + label;
        switch (opcode) {
            case FUNC -> { }
            case LABEL -> str = labelStr + ":";
            case MOVE  -> str = "\tmv\t" + reg0 + "," + reg1;
            case JUMP  -> str = "\tj\t" + labelStr;
            case CALL  -> str = "\tcall\t" + funcStr;
            case BNEQ  -> str = "\tbeq\t" + reg0 + "," + reg1 + "," + labelStr;
            case BEQ   -> str = "\tbne\t" + reg0 + "," + reg1 + "," + labelStr;
            case NEG   -> str = "\tneg\t" + reg0 + "," + reg1;
            case NOT   -> str = "\tnot\t" + reg0 + "," + reg1;
            case LOGICNOT -> str = "\tseqz\t" + reg0 + "," + reg1;
            case EQ  -> str = "\tsub\t" + "t5" + "," + reg1 + "," + reg2 + "\n"
                            + "\tseqz\t" + reg0 + "," + "t5";
            case NEQ -> str = "\tsub\t" + "t5" + "," + reg1 + "," + reg2 + "\n"
                            + "\tsnez\t" + reg0 + "," + "t5";
            case GE  -> str = "\tsgt\t" + reg0 + "," + reg1 + "," + reg2;
            case LE  -> str = "\tslt\t" + reg0 + "," + reg1 + "," + reg2;
            case GEQ -> str = "\tslt\t" + reg0 + "," + reg1 + "," + reg2 + "\n"
                            + "\txori\t" + reg0 + "," + reg0 + ",1";
            case LEQ -> str = "\tsgt\t" + reg0 + "," + reg1 + "," + reg2 + "\n"
                            + "\txori\t" + reg0 + "," + reg0 + ",1";
            case ADD -> str = "\tadd\t" + reg0 + "," + reg1 + "," + reg2;
            case SUB -> str = "\tsub\t" + reg0 + "," + reg1 + "," + reg2;
            case MUL -> str = "\tmul\t" + reg0 + "," + reg1 + "," + reg2;
            case DIV -> str = "\tdiv\t" + reg0 + "," + reg1 + "," + reg2;
            case MOD -> str = "\trem\t" + reg0 + "," + reg1 + "," + reg2;
            case OR  -> str = "\tor\t"  + reg0 + "," + reg1 + "," + reg2;
            case AND -> str = "\tand\t" + reg0 + "," + reg1 + "," + reg2;
            case XOR -> str = "\txor\t" + reg0 + "," + reg1 + "," + reg2;
            case SHL -> str = "\tsll\t" + reg0 + "," + reg1 + "," + reg2;
            case SHR -> str = "\tsra\t" + reg0 + "," + reg1 + "," + reg2;
            case INDEX -> str = "\tslli\t" + "t6" + "," + reg2 + ",2\n"
                              + "\tadd\t" + reg0 + "," + reg1 + "," + "t6";
            case LOAD, LOADSTRING -> {
                str = switch (regList.get(1).getType()) {
                    case 2 -> "\tlui\t" + reg0 + ",%hi(" + regList.get(1).toGASM() + ")";
                    case 8 -> "\tli\t" + reg0 + "," + regList.get(1).getID();
                    case 9 -> "\tlui\t" + reg0 + ",%hi(" + regList.get(1).toSASM() + ")\n"
                            + "\taddi\t" + reg0 + "," + reg0 + ",%lo(" + regList.get(1).toSASM() + ")";
                    default -> "";
                };
            }
            case RETURN -> str = "\tRETURN";
            case ADDI -> str = "\taddi\t" + reg0 + "," + reg1 + "," + regList.get(2).getID();
            case ANDI -> str = "\tandi\t" + reg0 + "," + reg1 + "," + regList.get(2).getID();
            case SLTI -> str = "\tslti\t" + reg0 + "," + reg1 + "," + regList.get(2).getID();
//            case EQI  -> str = "\taddi\t" + reg0 + "," + reg1 + "," + regList.get(2).getID();
//            case MULI -> str = "\taddi\t" + reg0 + "," + reg1 + "," + regList.get(2).getID();
            case LW -> {
                str = "\tlw\t" + reg0 + ","
                    + switch (regList.get(1).getType()) {
                    case 0 -> "0(" + reg1 + ")";
                    case 2 -> "%lo(" + regList.get(1).toGASM() + ")(" + reg2 + ")";
                    case 4 -> block.PAddr(regList.get(1).getID()) + block.getMemRAM() + "(sp)";
                    case 12-> block.LAddr(regList.get(1).getID()) + block.getMemRAM() + "(sp)";
                    default -> "";
                };
            }
            case SW -> {
                str = "\tsw\t" + reg0 + ","
                    + switch (regList.get(1).getType()) {
                    case 0 -> "0(" + reg1 + ")";
                    case 2 -> "%lo(" + regList.get(1).toGASM() + ")(" + reg2 + ")";
                    case 4 -> block.PAddr(regList.get(1).getID()) + block.getMemRAM() + "(sp)";
                    case 7 -> block.PAddr(regList.get(1).getID()) + "(sp)";
                    case 12-> block.LAddr(regList.get(1).getID()) + block.getMemRAM() + "(sp)";
                    default -> "";
                };
            }
        }
        // i.e. if not FUNC
        if (!str.equals("")) System.out.println(str);
    }

    public OPCODE getOpcode() { return opcode; }
    public void setOpcode(OPCODE opcode) { this.opcode = opcode; }
    public void addReg(IRReg reg) { regList.add(reg); }
    public ArrayList<IRReg> getRegList() { return regList; }
    public void setRegList(ArrayList<IRReg> regList) { this.regList = regList; }
    public void setLabel(int label) { this.label = label; }
    public int getLabel() { return label; }
    public String getFuncStr() { return funcStr; }
    public void setFuncStr(String funcStr) { this.funcStr = funcStr; }
}
