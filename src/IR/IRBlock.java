package IR;

import Util.RegIDAllocator;

import java.util.ArrayList;

public class IRBlock {
    private String ID;
    private ArrayList<IRLine> lineList = new ArrayList<>();
    private boolean containCall = false;
    private int cntRAM = 0, memRAM = 0, stAddr = 0;
    private int retLabel;
    private int localNum;

    public RegIDAllocator regIDAllocator;

    public IRBlock(String _ID, RegIDAllocator _regIDAllocator, int _retLabel) {
        ID = _ID; regIDAllocator = _regIDAllocator; retLabel = _retLabel;
    }

    public int LAddr(int ID) { return stAddr + (ID + 1) * -4; }
    public int PAddr(int ID) { return ID*4; }
    public void calcRAM() {
        cntRAM = regIDAllocator.size(1) + regIDAllocator.size(7);
        stAddr = 0;
        cntRAM++; // s0
        stAddr -= 4;
        if (containCall) {
            cntRAM++; // ra
            stAddr -= 4;
        }
        memRAM = 16 * (int)Math.ceil(cntRAM / 4.0);
    }

    public void print() { lineList.forEach(IRLine::print); }
    public void printASM() {
        String str = "";
        str = "\t.text\n"
            + "\t.align\t2\n"
            + "\t.globl\t" + ID + "\n"
            + "\t.type\t" + ID + ", @function\n"
            + ID + ":\n"
            + "\taddi\tsp,sp,-" + memRAM + "\n"
            + "\tsw\ts0," + (memRAM - 4) + "(sp)\n"
            + ((containCall) ? ("\tsw\tra," + (memRAM - 8) + "(sp)\n") : (""))
            + "\taddi\ts0,sp," + memRAM;
        System.out.println(str);
        lineList.forEach(x -> x.printASM(this));
        str = ".LAB" + retLabel + ":\n"
            + "\tlw\ts0," + (memRAM - 4) + "(sp)\n"
            + ((containCall) ? ("\tlw\tra," + (memRAM - 8) + "(sp)\n") : (""))
            + "\taddi\tsp,sp," + memRAM + "\n"
            + "\tjr\tra\n"
            + "\t.size\t" + ID + ", .-" + ID;
        System.out.println(str);
    }


    public void fulfill() {
        // adjust LW/SW, BNEQ/BEQ, CALL
        ArrayList<IRLine> newLineList = new ArrayList<>();
        for (IRLine line : lineList) {
            switch (line.getOpcode()) {
                case MOVE, BNEQ, BEQ, NEG, NOT, LOGICNOT, EQ, NEQ, GE, GEQ, LE, LEQ,
                    ADD, SUB, MUL, DIV, MOD, OR, AND, XOR, SHL, SHR,
                    INDEX, LOAD, LOADSTRING, ADDI, LW, SW -> {
                    for (int j = 0; j < line.getRegList().size(); ++j) {
                        if (j == 0 && line.getOpcode() != IRLine.OPCODE.BNEQ
                            && line.getOpcode() != IRLine.OPCODE.BEQ
                            && line.getOpcode() != IRLine.OPCODE.SW) continue;
                        IRReg curReg = line.getRegList().get(j), temp;
                        IRLine curLine;
                        switch (curReg.getType()) {
                            case 1, 4 -> {
                                temp = regIDAllocator.allocate(5);
                                curLine = new IRLine(IRLine.OPCODE.LW);
                                curLine.addReg(temp);
                                curLine.addReg(curReg);
                                newLineList.add(curLine);
                                line.getRegList().set(j, temp);
                            }
                            case 2 -> {
                                temp = regIDAllocator.allocate(5);
                                curLine = new IRLine(IRLine.OPCODE.LOAD);
                                curLine.addReg(temp);
                                curLine.addReg(curReg);
                                newLineList.add(curLine);
                                curLine = new IRLine(IRLine.OPCODE.LW);
                                curLine.addReg(temp);
                                curLine.addReg(curReg);
                                curLine.addReg(temp);
                                newLineList.add(curLine);
                                line.getRegList().set(j, temp);
                            }
                            case 6 -> {
                                temp = regIDAllocator.allocate(5);
                                curLine = new IRLine(IRLine.OPCODE.LW);
                                curLine.addReg(temp);
                                curLine.addReg(new IRReg(curReg.getID(), 5, false));
                                newLineList.add(curLine);
                                line.getRegList().set(j, temp);
                            }
                        }
                    }
                }
                case CALL -> containCall = true;
            }
            newLineList.add(line);

            switch (line.getOpcode()) {
                case MOVE, NEG, NOT, LOGICNOT, EQ, NEQ, GE, GEQ, LE, LEQ,
                    ADD, SUB, MUL, DIV, MOD, OR, AND, XOR, SHL, SHR,
                    INDEX, LOAD, LOADSTRING, ADDI, LW -> {
                    IRReg curReg = line.getRegList().get(0), temp1, temp2;
                    IRLine curLine;
                    switch (curReg.getType()) {
                        case 1, 4 -> {
                            temp1 = regIDAllocator.allocate(5);
                            curLine = new IRLine(IRLine.OPCODE.SW);
                            curLine.getRegList().add(temp1);
                            curLine.getRegList().add(curReg);
                            newLineList.add(curLine);
                            line.getRegList().set(0, temp1);
                        }
                        case 2 -> {
                            temp1 = regIDAllocator.allocate(5);
                            temp2 = regIDAllocator.allocate(5);
                            curLine = new IRLine(IRLine.OPCODE.LOAD);
                            curLine.getRegList().add(temp2);
                            curLine.getRegList().add(curReg);
                            newLineList.add(curLine);
                            curLine = new IRLine(IRLine.OPCODE.SW);
                            curLine.getRegList().add(temp1);
                            curLine.getRegList().add(curReg);
                            curLine.getRegList().add(temp2);
                            newLineList.add(curLine);
                            line.getRegList().set(0, temp1);
                        }
                        case 3 -> {
                            if (curReg.getID() >= 6) {
                                temp1 = regIDAllocator.allocate(5);
                                curLine = new IRLine(IRLine.OPCODE.SW);
                                curLine.getRegList().add(temp1);
                                curLine.getRegList().add(
                                        (regIDAllocator.size(7) < curReg.getID() - 5) ?
                                                (regIDAllocator.allocate(7)) :
                                                (new IRReg(curReg.getID()-6, 7, false)) );
                                newLineList.add(curLine);
                                line.getRegList().set(0, temp1);
                            }
                        }
                        case 6 -> {
                            temp1 = regIDAllocator.allocate(5);
                            curLine = new IRLine(IRLine.OPCODE.SW);
                            curLine.getRegList().add(temp1);
                            curLine.getRegList().add(new IRReg(curReg.getID(), 5, false));
                            newLineList.add(curLine);
                            line.getRegList().set(0, temp1);
                        }
                    }
                }
            }
        }
        lineList = newLineList;
        localNum = regIDAllocator.size(1);
    }
    public void fulfillLocal() {
        ArrayList<IRLine> newLineList = new ArrayList<>();
        for (IRLine line : lineList) {
            switch (line.getOpcode()) {
                case MOVE, BNEQ, BEQ, NEG, NOT, LOGICNOT, EQ, NEQ, GE, GEQ, LE, LEQ,
                    ADD, SUB, MUL, DIV, MOD, OR, AND, XOR, SHL, SHR,
                    INDEX, LOAD, LOADSTRING, ADDI, LW, SW -> {
                    for (int j = 0; j < line.getRegList().size(); ++j) {
                        if (j == 0 && line.getOpcode() != IRLine.OPCODE.BNEQ
                            && line.getOpcode() != IRLine.OPCODE.BEQ
                            && line.getOpcode() != IRLine.OPCODE.SW) continue;
                        IRReg curReg = line.getRegList().get(j), temp;
                        IRLine curLine;
                        if (curReg.getType() == 1 && curReg.getID() >= localNum) {
                            temp = regIDAllocator.allocate(5);
                            curLine = new IRLine(IRLine.OPCODE.LW);
                            curLine.addReg(temp);
                            curLine.addReg(curReg);
                            newLineList.add(curLine);
                            line.getRegList().set(j, temp);
                        }
                    }
                }
            }
            newLineList.add(line);
            switch (line.getOpcode()) {
                case MOVE, NEG, NOT, LOGICNOT, EQ, NEQ, GE, GEQ, LE, LEQ,
                    ADD, SUB, MUL, DIV, MOD, OR, AND, XOR, SHL, SHR,
                    INDEX, LOAD, LOADSTRING, ADDI, LW -> {
                    IRReg curReg = line.getRegList().get(0), temp;
                    IRLine curLine;
                    if (curReg.getType() == 1 && curReg.getID() >= localNum) {
                        temp = regIDAllocator.allocate(5);
                        curLine = new IRLine(IRLine.OPCODE.SW);
                        curLine.getRegList().add(temp);
                        curLine.getRegList().add(curReg);
                        newLineList.add(curLine);
                        line.getRegList().set(0, temp);
                    }
                }
            }
        }
        lineList = newLineList;
    }

    private boolean[] isFree = new boolean[32];
    private int[] used, firstPos, lastPos;
    private IRReg[] usedRegs, usedLRegs;

    private int getFreeReg(int ID) {
        boolean flag;
        for (int i = 0; i < 32; ++i) {
            if (isFree[i]) {
                flag = false;
                for (int j = firstPos[ID]; j < lastPos[ID]; ++j) {
                    IRLine line = lineList.get(j);
                    if (line.getRegList().size() > 0
                        && line.getRegList().get(0).getID() == i - 10
                        && line.getRegList().get(0).getType() == 3) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) return i;
            }
        }
        return 0;
    }
    private void allocate_release(IRLine line, int low, int high) { // [low, high)
        // allocate
        for (int i = low; i < high; ++i) {
            IRReg reg = line.getRegList().get(i), temp;
            if (reg.getType() != 5) continue;
            if (usedRegs[reg.getID()] != null) {
                temp = usedRegs[reg.getID()];
            } else {
                if (usedLRegs[reg.getID()] != null) {
                    temp = usedLRegs[reg.getID()];
                } else { // find new free reg
                    int index = getFreeReg(reg.getID());
                    if (index > 0) {
                        isFree[index] = false;
                        temp = usedRegs[reg.getID()] = new IRReg(index, 0, false);
                    } else {
                        temp = usedLRegs[reg.getID()] = regIDAllocator.allocate(1);
                    }
                }
            }
            temp.setUsedID(reg.getID());
            line.getRegList().set(i, temp);
        }
        // release
        for (int i = low; i < high; ++i) {
            IRReg reg = line.getRegList().get(i);
            int usedID = reg.getUsedID();
            if (usedID == -1) continue;
            if (usedLRegs[usedID] != null) continue;
            --used[usedID];
            if (used[usedID] == 0 /*&& usedRegs[usedID] != null*/) isFree[usedRegs[usedID].getID()] = true;
        }
    }
    public void allocate() {
        int curSize = regIDAllocator.size(5);
        used = new int[curSize];
        firstPos = new int[curSize];
        lastPos = new int[curSize];
        for (int i = 0; i < lineList.size(); ++i) {
            IRLine curLine = lineList.get(i);
            for (int j = 0; j < curLine.getRegList().size(); ++j) {
                IRReg curReg = curLine.getRegList().get(j);
                if (curReg.getType() == 5) {
                    used[curReg.getID()]++;
                    if (firstPos[curReg.getID()] == 0) firstPos[curReg.getID()] = i;
                    lastPos[curReg.getID()] = i;
                }
            }
        }
        usedRegs = new IRReg[curSize];
        usedLRegs = new IRReg[curSize];
        for (int i = 0; i < 32; ++i) isFree[i] = (i >= 10 && i <= 15);
        for (IRLine line : lineList) {
            switch (line.getOpcode()) {
                case MOVE, NEG, NOT, LOGICNOT, EQ, NEQ, GE, GEQ, LE, LEQ,
                    ADD, SUB, MUL, DIV, MOD, OR, AND, XOR, SHL, SHR,
                    INDEX, LOAD, LOADSTRING, ADDI, LW ->  { // default - func, label, jump, return
                    allocate_release(line, 0, line.getRegList().size());
                    IRReg curReg = line.getRegList().get(0);
                    if (curReg.getType() == 3) {
                        if (curReg.getID() < 6) {
                            isFree[curReg.getID() + 10] = false;
                            line.getRegList().set(0, new IRReg(curReg.getID() + 10, 0, false));
                        } else {
                            line.getRegList().set(0, regIDAllocator.allocate(7));
                        }
                    } else {
                        allocate_release(line, 0, 1);
                    }
                }
                case BNEQ, BEQ, SW -> {
                    allocate_release(line, 0, line.getRegList().size());
                }
                case CALL -> {
                    for (int i = 0; i < curSize; ++i) {
                        if (used[i] == 0 || usedRegs[i] == null) continue;
                        usedRegs[i] = regIDAllocator.allocate(1);
                        used[i] = 0;
                    }
                    for (int i = 10; i <= 15; ++i) isFree[i] = true;
                }
            }
        }
    }
    public void allocateLocal() {
        int curSize = regIDAllocator.size(5);
        used = new int[curSize];
        firstPos = new int[curSize];
        lastPos = new int[curSize];
        for (int i = 0; i < lineList.size(); ++i) {
            IRLine curLine = lineList.get(i);
            for (int j = 0; j < curLine.getRegList().size(); ++j) {
                IRReg curReg = curLine.getRegList().get(j);
                if (curReg.getType() == 5) {
                    used[curReg.getID()]++;
                    if (firstPos[curReg.getID()] == 0) firstPos[curReg.getID()] = 0;
                    lastPos[curReg.getID()] = 0;
                }
            }
        }
        usedRegs = new IRReg[curSize];
        usedLRegs = new IRReg[curSize];
        for (int i = 0; i < 32; ++i) isFree[i] = false;
        isFree[16] = isFree[17] = true;
        for (IRLine line : lineList) {
            switch (line.getOpcode()) {
                case MOVE, NEG, NOT, LOGICNOT, EQ, NEQ, GE, GEQ, LE, LEQ,
                    ADD, SUB, MUL, DIV, MOD, OR, AND, XOR, SHL, SHR,
                    INDEX, LOAD, LOADSTRING, ADDI, LW -> { // default - func, label, jump, return
                    allocate_release(line, 1, line.getRegList().size());
                    allocate_release(line, 0, 1);
                }
                case BNEQ, BEQ, SW -> {
                    allocate_release(line, 0, line.getRegList().size());
                }
                case CALL -> {
                    isFree[16] = isFree[17] = true;
                }
            }
        }
    }

    public void trim() {
        // skip null & mv reg0 reg1 where re0 == reg1
        ArrayList<IRLine> newLineList = new ArrayList<>();
        for (IRLine line : lineList) {
            if (line == null) continue;
            if (line.getOpcode() != IRLine.OPCODE.MOVE) {
                newLineList.add(line);
            } else if (line.getRegList().get(0).getID() != line.getRegList().get(1).getID()) {
                newLineList.add(line);
            }
        }
        lineList = newLineList;
    }

    public void addLine(IRLine line) { lineList.add(line); }
    public int getRetLabel() { return retLabel; }
    public void setRetLabel(int retLabel) { this.retLabel = retLabel; }
}
