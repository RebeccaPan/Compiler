package Optimize.IR;

import IR.IRBlock;
import IR.IRBlockList;
import IR.IRLine;
import IR.IRReg;
import Optimize.Opt;

import java.util.ArrayList;

public class ConvertImm extends Opt {
    public ConvertImm(IRBlockList _curBlockList) { super(_curBlockList); }

    private void convert(IRBlock block) {
        ArrayList<IRLine> newLineList = new ArrayList<>();
        for (IRLine curLine : block.getLineList()) {
            if (!curLine.isDefLine()/* || curLine.getRegList().get(0).getType() != 5*/) {
                newLineList.add(curLine);
            } else {
                IRLine prvLine = newLineList.get(newLineList.size() - 1);
                switch (curLine.getOpcode()) {
                    case ADD, AND -> {
                        if (prvLine.getOpcode() == IRLine.OPCODE.LOAD
                         && prvLine.getRegList().get(0).getType() == 5
                         && prvLine.getRegList().get(1).getType() == 8) {
                            IRReg reg0_p = prvLine.getRegList().get(0);
                            IRReg reg1_p = prvLine.getRegList().get(1);
                            IRReg reg0_c = curLine.getRegList().get(0);
                            IRReg reg1_c = curLine.getRegList().get(1);
                            IRReg reg2_c = curLine.getRegList().get(2);
                            if (validImm(reg1_p)) {
                                if (isSame(reg0_p, reg1_c)) {
                                    IRLine line = new IRLine(curLine.getOpcode() == IRLine.OPCODE.ADD ?
                                            IRLine.OPCODE.ADDI : IRLine.OPCODE.ANDI);
                                    line.addReg(reg0_c);
                                    line.addReg(reg2_c);
                                    line.addReg(reg1_p);
                                    newLineList.set(newLineList.size() - 1, line);
                                    updated = true;
                                } else if (isSame(reg0_p, reg2_c)) {
                                    IRLine line = new IRLine(curLine.getOpcode() == IRLine.OPCODE.ADD ?
                                            IRLine.OPCODE.ADDI : IRLine.OPCODE.ANDI);
                                    line.addReg(reg0_c);
                                    line.addReg(reg1_c);
                                    line.addReg(reg1_p);
                                    newLineList.set(newLineList.size() - 1, line);
                                    updated = true;
                                } else newLineList.add(curLine);
                            } else newLineList.add(curLine);
                        } else newLineList.add(curLine);
                    }
                    case LE -> {
                        if (prvLine.getOpcode() == IRLine.OPCODE.LOAD
                                && prvLine.getRegList().get(0).getType() == 5
                                && prvLine.getRegList().get(1).getType() == 8) {
                            IRReg reg0_p = prvLine.getRegList().get(0);
                            IRReg reg1_p = prvLine.getRegList().get(1);
                            IRReg reg0_c = curLine.getRegList().get(0);
                            IRReg reg1_c = curLine.getRegList().get(1);
                            IRReg reg2_c = curLine.getRegList().get(2);
                            if (validImm(reg1_p) && isSame(reg0_p, reg2_c)) {
                                IRLine line = new IRLine(IRLine.OPCODE.SLTI);
                                line.addReg(reg0_c);
                                line.addReg(reg1_c);
                                line.addReg(reg1_p);
                                newLineList.set(newLineList.size() - 1, line);
                                updated = true;
                            } else newLineList.add(curLine);
                        } else newLineList.add(curLine);
                    }
                    /*case MOVE -> {
                        if (prvLine.getOpcode() == IRLine.OPCODE.LOAD
                                && prvLine.getRegList().get(0).getType() == 5
                                && prvLine.getRegList().get(1).getType() == 8) {
                            IRReg reg0_p = prvLine.getRegList().get(0);
                            IRReg reg1_p = prvLine.getRegList().get(1);
                            IRReg reg0_c = curLine.getRegList().get(0);
                            IRReg reg1_c = curLine.getRegList().get(1);
                            if (isSame(reg0_p, reg1_c)) {
                                IRLine line = new IRLine(IRLine.OPCODE.LOAD);
                                line.addReg(reg0_c);
                                line.addReg(reg1_p);
                                newLineList.set(newLineList.size() - 1, line);
                                updated = true;
                            } else newLineList.add(curLine);
                        } else newLineList.add(curLine);
                    }*/
                    default -> newLineList.add(curLine);
                }
            }
        }
        block.setLineList(newLineList);
    }

    @Override
    public void opt() {
        updated = false;
        for (IRBlock block : curBlockList.getBlockList()) {
            convert(block);
        }
    }
}
