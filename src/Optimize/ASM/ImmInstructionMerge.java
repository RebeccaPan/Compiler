package Optimize.ASM;

import IR.IRBlock;
import IR.IRBlockList;
import IR.IRLine;
import IR.IRReg;
import Optimize.Opt;

public class ImmInstructionMerge extends Opt {
    public ImmInstructionMerge(IRBlockList _curBlockList) { super(_curBlockList); }

    public void instMerge(IRBlock block) {
        if (block.getLineList().size() <= 1) return;
        for (int i = 1; i < block.getLineList().size(); ++i) {
            IRLine prvLine = block.getLineList().get(i - 1);
            IRLine curLine = block.getLineList().get(i);
            if (prvLine.getOpcode() == IRLine.OPCODE.ADDI && curLine.getOpcode() == IRLine.OPCODE.ADDI) {
                IRReg reg0_p = prvLine.getRegList().get(0);
                IRReg reg1_p = prvLine.getRegList().get(1);
                IRReg reg2_p = prvLine.getRegList().get(2);
                IRReg reg0_c = curLine.getRegList().get(0);
                IRReg reg1_c = curLine.getRegList().get(1);
                IRReg reg2_c = curLine.getRegList().get(2);
                if (!isSame(reg0_p, reg0_c)) continue;
                if (!isSame(reg0_p, reg1_p)) continue;
                if (!isSame(reg0_c, reg1_c)) continue;
                reg2_c.setID(reg2_p.getID() + reg2_c.getID());
                block.getLineList().remove(prvLine);
            }
        }
    }

    @Override
    public void opt() {
        updated = false;
        for (IRBlock block : curBlockList.getBlockList()) {
            instMerge(block);
        }
    }
}
