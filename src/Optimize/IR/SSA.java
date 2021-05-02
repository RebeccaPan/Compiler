package Optimize.IR;

import IR.*;
import Optimize.Opt;

import java.util.ArrayList;

public class SSA extends Opt {
    private int[] times;
    private boolean[] tempTimes;
    private int cntL;
    private ArrayList<ArrayList<ArrayList<Integer>>> regInfo = new ArrayList<>();
    private IRReg[] newRegs;
    private int[] par;

    private IRBlock block; // curBlock used by all func

    // find-union
    private int find(int x) {
        if (par[x] == x) return x;
//        if (par[x] == 0) return x;
        else return par[x] = find(par[x]);
    }
    private void union(int p, int q) {
        int x = find(p), y = find(q);
        if (x != y) par[x] = y;
    }

    private void locAdjust(int i, int ID, int loc) {
        while (i < block.getLineList().size() && times[i] < cntL) {
            times[i] = cntL;
            IRLine line = block.getLineList().get(i);
            for (int j = line.regLoc(); j != -1 && j < line.getRegList().size(); ++j) {
                IRReg reg = line.getRegList().get(j);
                if (reg.getType() == 1 && reg.getID() == ID) {
                    regInfo.get(i).get(j).add(loc);
                }
            }
            if (line.isDefLine()) {
                IRReg reg = line.getRegList().get(0);
                if (reg.getType() == 1 && reg.getID() == ID) {
                    regInfo.get(i).get(0).add(loc); break;
                }
            }
            if (line.getOpcode() == IRLine.OPCODE.JUMP) {
                i = block.jumpTarget[i];
            } else {
                if (line.getOpcode() == IRLine.OPCODE.BEQ || line.getOpcode() == IRLine.OPCODE.BNEQ) {
                    locAdjust(block.jumpTarget[i], ID, loc);
                }
                ++i;
            }
        }
    }

    private void assignAdjust(int i, int ID, int loc) {
        while (i < block.getLineList().size() && times[i] < cntL) {
            times[i] = cntL;
            IRLine line = block.getLineList().get(i);
            for (int j = line.regLoc(); j != -1 && j < line.getRegList().size(); ++j) {
                IRReg reg = line.getRegList().get(j);
                if (reg.getType() == 1 && reg.getID() == ID) {
                    line.getRegList().set(j, newRegs[loc]);
                }
            }
            if (line.isDefLine()) {
                IRReg reg = line.getRegList().get(0);
                if (reg.getType() == 1 && reg.getID() == ID) {
                    break;
                }
            }
            if (line.getOpcode() == IRLine.OPCODE.JUMP) {
                i = block.jumpTarget[i];
            } else {
                if (line.getOpcode() == IRLine.OPCODE.BEQ || line.getOpcode() == IRLine.OPCODE.BNEQ) {
                    assignAdjust(block.jumpTarget[i], ID, loc);
                }
                ++i;
            }
        }
    }

    public SSA(IRBlockList _curBlockList) { super(_curBlockList); }

    private void operate(IRBlock _block) {
        block = _block;
        ArrayList<IRLine> lineList = block.getLineList();
        int linesNum = lineList.size();
        // init
        times = new int[linesNum];
        tempTimes = new boolean[linesNum];
        cntL = 0;
        regInfo = new ArrayList<>();
        newRegs = new IRReg[linesNum];
        par = new int[linesNum];
        for (int i = 0; i < linesNum; i++) { par[i] = i; }
//        Arrays.fill(par, 0);
        for (IRLine line : lineList) {
            ArrayList<ArrayList<Integer>> tempRegInfo = new ArrayList<>();
            for (IRReg ignored : line.getRegList()) tempRegInfo.add(new ArrayList<>());
            regInfo.add(tempRegInfo);
        }
        for (int i = 0; i < linesNum; ++i) {
            IRLine line = lineList.get(i);
            if (line.isDefLine() && line.getRegList().get(0).getType() == 1) {
                cntL++;
                newRegs[i] = block.regIDAllocator.allocate(5);
                locAdjust(i + 1, line.getRegList().get(0).getID(), i);
            }
        }
        for (int i = 0; i < linesNum; ++i) {
            for (int j = 0; j < lineList.get(i).getRegList().size(); ++j) {
                for (int k = 1; k < regInfo.get(i).get(j).size(); ++k) {
                    int x = regInfo.get(i).get(j).get(0);
                    int y = regInfo.get(i).get(j).get(k);
                    newRegs[x].setMulti(true);
                    newRegs[y].setMulti(true);
                    union(x, y);
                }
            }
        }
        for (int i = 0; i < linesNum; ++i) {
            IRLine line = lineList.get(i);
            if (line.isDefLine() && line.getRegList().get(0).getType() == 1) {
                newRegs[i].assign(newRegs[find(i)]);
                cntL++;
                assignAdjust(i + 1, line.getRegList().get(0).getID(), i);
                line.getRegList().set(0, newRegs[i]); updated = true;
            }
        }
    }

    @Override
    public void opt() {
        updated = false;
        for (IRBlock block : curBlockList.getBlockList()) {
            block.jumpUpdate(curBlockList.getMaxLabel());
            block.labelOpt(curBlockList.getMaxLabel());
            operate(block);
            block.setCntL(cntL);
        }
    }
}
