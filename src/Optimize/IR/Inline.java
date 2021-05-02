package Optimize.IR;

import IR.IRBlock;
import IR.IRBlockList;
import IR.IRLine;
import IR.IRReg;
import Optimize.Opt;

import java.util.ArrayList;

public class Inline extends Opt {
    private boolean localUpdate = false;
    public Inline(IRBlockList _curBlockList) { super(_curBlockList); }

    int limit = 1; // parameter that can be modified
    boolean doSelfInline = false;
    private void init(IRBlock block) {
        for (IRLine line : block.getLineList()) {
            if (line.getOpcode() == IRLine.OPCODE.CALL && line.getLabel() == block.getRetLabel()) {
                // do self inline
                doSelfInline = true; break;
            }
        }
    }

    private void inline(IRBlock block) {
        localUpdate = doSelfInline;
        int retLabel = curBlockList.getMaxLabel();
        curBlockList.setMaxLabel(retLabel + 1);
        IRLine line;
        if (doSelfInline) {
            ArrayList<IRLine> newLineList = new ArrayList<>();
            IRReg[] paraRegs = new IRReg[block.getArgNum()];
            IRReg[] tempRegs = new IRReg[block.getArgNum()];
            for (int i = 0; i < block.getArgNum(); ++i) tempRegs[i] = block.regIDAllocator.allocate(1);
            for (int i = 0; i < block.getLineList().size(); ++i) {
                // self inline
                IRLine curLine = block.getLineList().get(i);
                if (curLine.getOpcode() == IRLine.OPCODE.CALL) {
                    for (int j = block.getArgNum() - 1; j >= 0; --j) {
                        line = new IRLine(IRLine.OPCODE.MOVE);
                        line.addReg(paraRegs[j]);
                        line.addReg(tempRegs[j]);
                        newLineList.add(line);
                    }
                    line = new IRLine(IRLine.OPCODE.JUMP);
                    line.setLabel(retLabel);
                    newLineList.add(line);
                } else {
                    if (i > 0 && i <= block.getArgNum()) {
                        paraRegs[i - 1] = curLine.getRegList().get(0);
                    } else if (curLine.getOpcode() == IRLine.OPCODE.MOVE
                         && curLine.getRegList().get(0).getType() == 3) {
                            curLine.getRegList().set(0, tempRegs[curLine.getRegList().get(0).getID()]);
                    }
                    newLineList.add(curLine);
                    if (i == block.getArgNum()) {
                        line = new IRLine(IRLine.OPCODE.LABEL);
                        line.setLabel(retLabel);
                        newLineList.add(line);
                    }
                }
            }
            block.setLineList(newLineList);
            block.setContainCall(false);
        } else {
            // non-self inline
        }
    }

    private void finale() {

    }

    @Override
    public void opt() {
        updated = false;
        localUpdate = true;
        for (IRBlock block : curBlockList.getBlockList()) {
            for (int cnt = 0; cnt < limit; ++cnt) {
                localUpdate = false;
                init(block);
                inline(block);
                if (!localUpdate) break;
            }
            finale();
        }
    }
}
