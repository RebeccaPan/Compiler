package IR;

import Util.RegIDAllocator;
import Util.Pair;
import Util.Graph;

import static IR.IRLine.OPCODE.*;

import java.util.ArrayList;

public class IRBlock {
    private String ID;
    private ArrayList<IRLine> lineList = new ArrayList<>();
    private boolean containCall = false;
    private int cntRAM = 0, memRAM = 0, stAddr = 0;
    private int retLabel;
    private int localNum;
    private int argNum, index;
    public ArrayList<Pair<String, Integer>> jumpedBy = new ArrayList<>();
    private int cntL;

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
        String str = "", subStr = "";
        for (int i = 0; i < graph.useSaved(); ++i) {
            subStr += "\tsw\ts" + i + "," + (memRAM - 4*(i+1)) + "(sp)" + "\n";
        }
        str = "\t.text\n"
            + "\t.align\t2\n"
            + "\t.globl\t" + ID + "\n"
            + "\t.type\t" + ID + ", @function\n"
            + ID + ":\n"
            + "\taddi\tsp,sp,-" + memRAM + "\n"
            + subStr
            + ((containCall) ? ("\tsw\tra," + (memRAM - 4*graph.useSaved() - 4) + "(sp)\n") : (""));
        System.out.print(str);
        lineList.forEach(x -> x.printASM(this));
        for (int i = 0; i < graph.useSaved(); ++i) {
            subStr += "\tlw\ts" + i + "," + (memRAM - 4*(i+1)) + "(sp)\n";
        }
        str = ((lineList.get(lineList.size() - 1).getOpcode() == LABEL
            && lineList.get(lineList.size() - 1).getLabel() == retLabel) ? ("") : (".b" + index + "l" + retLabel + ":\n"))
//        str = ".LAB" + retLabel + ":\n"
            + subStr
            + ((containCall) ? ("\tlw\tra," + (memRAM - 4*graph.useSaved() - 4) + "(sp)\n") : (""))
            + "\taddi\tsp,sp," + memRAM + "\n"
            + "\tjr\tra\n"
            + "\t.size\t" + ID + ", .-" + ID;
        System.out.println(str);
    }

    // TODO!!!
    public void jumpUpdate(int maxLabel) {
        int[] used = new int [maxLabel + 1];
        ArrayList<IRLine> newLineList = new ArrayList<>();
        for (IRLine line : lineList) {
            switch (line.getOpcode()) {
                case JUMP, BNEQ, BEQ -> {
                    used[line.getLabel()]++;
                    newLineList.add(line);
                }
                case LABEL -> {
                    if (newLineList.get(newLineList.size() - 1).getOpcode() == JUMP
                     && newLineList.get(newLineList.size() - 1).getLabel() == line.getLabel()) {
                        used[line.getLabel()]--;
                        newLineList.remove(newLineList.size() - 1);
                    } else newLineList.add(line);
                }
                default -> newLineList.add(line);
            }
        }
        lineList = newLineList;
        newLineList = new ArrayList<>();
        for (IRLine line : lineList) {
            if (line.getOpcode() == LABEL
                    && used[line.getLabel()] == 0) continue;
            newLineList.add(line);
        }
        lineList = newLineList;
    }

    public int[] jumpTarget, labelTarget;
    public void labelOpt(int maxLabel) {
        jumpTarget = new int[lineList.size()];
        labelTarget = new int[maxLabel + 1];
        for (int i = 0; i < lineList.size(); ++i) {
            IRLine line = lineList.get(i);
            if (line.getOpcode() == IRLine.OPCODE.LABEL) labelTarget[line.getLabel()] = i;
        }
        for (int i = 0; i < lineList.size(); ++i) {
            IRLine line = lineList.get(i);
            if (line.getOpcode() == IRLine.OPCODE.JUMP
                    || line.getOpcode() == IRLine.OPCODE.BEQ || line.getOpcode() == IRLine.OPCODE.BNEQ)
                jumpTarget[i] = labelTarget[line.getLabel()];
        }
    }

    public void fulfill() {
        // adjust LW/SW, BNEQ/BEQ, CALL
        ArrayList<IRLine> newLineList = new ArrayList<>();
        for (IRLine line : lineList) {
            switch (line.getOpcode()) {
                case MOVE, BNEQ, BEQ, NEG, NOT, LOGICNOT, EQ, NEQ, GE, GEQ, LE, LEQ,
                    ADD, SUB, MUL, DIV, MOD, OR, AND, XOR, SHL, SHR,
                    INDEX, LOAD, LOADSTRING, ADDI, ANDI, SLTI, LW, SW -> {
                    for (int j = 0; j < line.getRegList().size(); ++j) {
                        if (j == 0 && line.getOpcode() != BNEQ
                            && line.getOpcode() != IRLine.OPCODE.BEQ
                            && line.getOpcode() != IRLine.OPCODE.SW) continue;
                        IRReg curReg = line.getRegList().get(j), temp;
                        IRLine curLine;
                        switch (curReg.getType()) {
                            case 1, 4 -> {
                                // TODO
//                                temp = regIDAllocator.allocate(5);
//                                curLine = new IRLine(IRLine.OPCODE.LW);
//                                curLine.addReg(temp);
//                                curLine.addReg(curReg);
//                                newLineList.add(curLine);
//                                line.getRegList().set(j, temp);
                                line.setOpcode(LW);
                            }
                            case 2 -> {
                                temp = regIDAllocator.allocate(5);
                                curLine = new IRLine(IRLine.OPCODE.LOAD);
                                curLine.addReg(temp);
                                curLine.addReg(curReg);
                                newLineList.add(curLine);
                                IRReg temp_ = regIDAllocator.allocate(5);
                                curLine = new IRLine(IRLine.OPCODE.LW);
                                curLine.addReg(temp_);
                                curLine.addReg(curReg);
                                curLine.addReg(temp);
                                newLineList.add(curLine);
                                line.getRegList().set(j, temp_);
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
                    INDEX, LOAD, LOADSTRING, ADDI, ANDI, SLTI, LW -> {
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
                            if (curReg.getID() < 8) {
                                line.getRegList().set(0, new IRReg(curReg.getID() + 10, 0, false));
                            } else {
                                temp1 = regIDAllocator.allocate(5);
                                curLine = new IRLine(IRLine.OPCODE.SW);
                                curLine.getRegList().add(temp1);
                                IRReg temp_, ignored;
                                while (regIDAllocator.size(7) + 7 < curReg.getID())
                                    ignored = regIDAllocator.allocate(7);
                                temp_ = new IRReg(curReg.getID() - 8, 7, false);
                                curLine.getRegList().add(temp_);
                                newLineList.add(curLine);
                                line.getRegList().set(0, temp1);
                            }
                        }
                        case 6 -> {
                            if (line.getOpcode() == MOVE && line.getRegList().get(1).getType() == 5) {
                                curLine = new IRLine(SW);
                                curLine.getRegList().add(line.getRegList().get(1));
                                curLine.getRegList().add(new IRReg(curReg.getID(), 5, false));
                                newLineList.set(newLineList.size() - 1, curLine);
                            } else {
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
                    INDEX, LOAD, LOADSTRING, ADDI, ANDI, SLTI, LW, SW -> {
                    for (int j = 0; j < line.getRegList().size(); ++j) {
                        if (j == 0 && line.getOpcode() != BNEQ
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
                    INDEX, LOAD, LOADSTRING, ADDI, ANDI, SLTI, LW -> {
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

    private boolean[] isFree;
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

    private Graph graph;
    private int[] reach, beginT, endT;
    private ArrayList<ArrayList<Integer>> reachTo;
    private void reachPass(int ID) {
        int[] vec = new int[lineList.size()];
        int top = 0;
        for (int i = beginT[ID] + 1; i <= endT[ID]; ++i) {
            IRLine line = lineList.get(i);
            for (int j = line.regLoc(); j != -1 && j < line.getRegList().size(); ++j) {
                IRReg reg = line.getRegList().get(j);
                if (reg.getType() == 5 && reg.getID() == ID) {
                    reach[i] = cntL;
                    vec[top++] = i;
                }
            }
        }
        for (int i = 0; i < top; ++i) {
            for (int j = 0; j < reachTo.get(vec[i]).size(); ++j) {
                int cur = reachTo.get(vec[i]).get(j);
                IRLine line = lineList.get(cur);
              /*  if (reach[cur] < cntL && !line.isDefLine()) {
                    if (line.getRegList().size() > 0) {
                        IRReg reg = line.getRegList().get(0);
                        if (reg.getType() == 5 && reg.getID() == ID) {
                            reach[i] = cntL;
                            vec[top++] = i;
                        }
                    }
                }*/
                if(reach[cur] < cntL && !(line.isDefLine() && line.getRegList().size() > 0 && line.getRegList().get(0).getType() == 5 && line.getRegList().get(0).getID() == ID)){
                    reach[cur] = cntL;
                    vec[top++] = cur;
                }
            }
        }
        for (int i = 0; i < top; ++i) {
            IRLine line = lineList.get(vec[i]);
            if (line.getOpcode() == CALL) {
                graph.saved[ID] = true;
            }
            for (int j = line.regLoc(); j != -1 && j < line.getRegList().size(); ++j) {
                IRReg reg = line.getRegList().get(j);
                if (reg.getType() == 5)
                    graph.add(ID, reg.getID());
                else if (reg.getType() == 0 && reg.getID() >= 10)
                    graph.add(ID, reg.getID() - 10 + regIDAllocator.size(5));
            }
            if (line.isDefLine() && vec[i] + 1 < lineList.size()
             && reach[vec[i] + 1] == cntL) {
                IRReg reg = line.getRegList().get(0);
                if (reg.getType() == 5)
                    graph.add(ID, reg.getID());
                else if (reg.getType() == 0 && reg.getID() >= 10)
                    graph.add(ID, reg.getID() - 10 + regIDAllocator.size(5));
            }
        }
    }
    private void allocPass(int i, int ID) {
        for (IRLine line : lineList) {
            for (IRReg reg : line.getRegList()) {
                if (reg.getType() == 5) graph.add(ID, reg.getID());
            }
            if (line.getOpcode() == CALL) break;
        }
    }
    public void graphColor(int maxLabel) {
        labelOpt(maxLabel);
        int linesNum = lineList.size();
        reach = new int[linesNum];
        endT = new int[regIDAllocator.size(5)];
        beginT = new int[regIDAllocator.size(5)];
        boolean[] flag = new boolean[regIDAllocator.size(5)];
        graph = new Graph(regIDAllocator.size(5));
        cntL = 1;
        reachTo = new ArrayList<>();
        for (int i = 0; i < linesNum; i++) reachTo.add(new ArrayList<>());
        for (int i = 0; i < linesNum; i++){
            IRLine line = lineList.get(i);
            if (line.getOpcode() == JUMP || line.getOpcode() == BEQ || line.getOpcode() == BNEQ){
                reachTo.get(jumpTarget[i]).add(i);
            }
            if (line.getOpcode() != JUMP && i + 1 < lineList.size()) reachTo.get(i + 1).add(i);
        }

        for (int i = 0; i < linesNum; i++){
            IRLine line = lineList.get(i);
            for (int j = 0; j < line.getRegList().size(); j++){
                if (line.getRegList().get(j).getType() == 5){
                    endT[line.getRegList().get(j).getID()] = i;
                }
            }
        }
        for (int i = 0; i < linesNum; i++){
            IRLine line = lineList.get(i);
//            if (line.ignored) continue;
            if (line.isDefLine()){
                IRReg reg = line.getRegList().get(0);
                if (reg.getType() == 5){
                    if (!flag[reg.getID()]){
                        flag[reg.getID()] = true;
                        beginT[reg.getID()] = i;
                        cntL++;
                        reachPass(reg.getID());
                    }
                } else if (reg.getType() == 0){
                    allocPass(i + 1, reg.getID() - 10 + regIDAllocator.size(5));
                }
            }
        }
        // TODO
        graph.work();
        IRReg[] spillRegs = new IRReg[regIDAllocator.size(5)];
        for (IRLine line : lineList){
            for (int j = 0; j < line.getRegList().size(); j++){
                IRReg reg = line.getRegList().get(j);
                if (reg.getType() == 5){
                    if (graph.getColor(reg.getID()) == -1){
                        if (spillRegs[reg.getID()] == null) line.getRegList().set(j, spillRegs[reg.getID()] = regIDAllocator.allocate(12));
                        else line.getRegList().set(j, spillRegs[reg.getID()]);
                    }else{
                        line.getRegList().set(j, new IRReg(graph.getColor(reg.getID()), 0, false));
                    }
                }else if (reg.getType() == 0 && reg.getID() >= 10){
                    reg.setID(graph.c[reg.getID()- 10]);
                }
            }
        }
    }
    /*public void allocate() {
        int curSize = regIDAllocator.size(5);
        used = new int[curSize];
        firstPos = new int[curSize];
        lastPos = new int[curSize];
        usedRegs = new IRReg[curSize];
        usedLRegs = new IRReg[curSize];
        isFree = new boolean[32];
        for (int i = 0; i < 32; ++i) isFree[i] = (i >= 10 && i <= 15);
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
        for (IRLine line : lineList) {
            switch (line.getOpcode()) {
                case MOVE, NEG, NOT, LOGICNOT, EQ, NEQ, GE, GEQ, LE, LEQ,
                    ADD, SUB, MUL, DIV, MOD, OR, AND, XOR, SHL, SHR,
                    INDEX, LOAD, LOADSTRING, ADDI, ANDI, SLTI, LW ->  { // default - func, label, jump, return
                    if (line.getOpcode() == MUL) {
                        System.out.print("");
                    }
                    allocate_release(line, 1, line.getRegList().size());
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
                        IRReg tempReg = regIDAllocator.allocate(1);
                        usedRegs[i].setID(tempReg.getID());
                        usedRegs[i].setType(tempReg.getType());
                        usedRegs[i].setPtr(tempReg.isPtr());
                        used[i] = 0;
                    }
                    for (int i = 10; i <= 15; ++i) isFree[i] = true;
                }
            }
        }
    }*/
    public void allocateLocal() {
        int curSize = regIDAllocator.size(5);
        isFree = new boolean[32];
        used = new int[curSize];
        firstPos = new int[curSize];
        lastPos = new int[curSize];
        for (IRLine curLine : lineList) {
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
                    INDEX, LOAD, LOADSTRING, ADDI, ANDI, SLTI, LW -> { // default - func, label, jump, return
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
        // skip null & mv reg0 reg1 where reg0 == reg1
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
    public String getID() { return ID; }
    public int getRetLabel() { return retLabel; }
    public void setRetLabel(int retLabel) { this.retLabel = retLabel; }
    public ArrayList<IRLine> getLineList() { return lineList; }
    public void setLineList(ArrayList<IRLine> lineList) { this.lineList = lineList; }
    public boolean containsCall() { return containCall; }
    public void setContainCall(boolean containCall) { this.containCall = containCall; }
    public int getArgNum() { return argNum; }
    public void setArgNum(int argNum) { this.argNum = argNum; }
    public int getCntL() { return cntL; }
    public void setCntL(int cntL) { this.cntL = cntL; }
    public int getMemRAM() { return memRAM; }
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
}
