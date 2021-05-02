package Optimize.ASM;

import IR.*;
import Optimize.Opt;

import java.util.ArrayList;

public class RedundantInstRemove extends Opt {
    public RedundantInstRemove(IRBlockList _curBlockList) { super(_curBlockList); }

    private void trimRedundant_single(IRBlock curBlock) {
        boolean localUpdated = false;
        ArrayList<IRLine> newLineList = new ArrayList<>();
        for (IRLine line : curBlock.getLineList()) {
            IRLine.OPCODE opcode = line.getOpcode();
            ArrayList<IRReg> regList = line.getRegList();
            IRReg reg0 = regList.size() > 0 ? regList.get(0) : null;
            IRReg reg1 = regList.size() > 1 ? regList.get(1) : null;
            IRReg reg2 = regList.size() > 2 ? regList.get(2) : null;

            switch (opcode) {
                case FUNC, LABEL -> { }
                case MOVE  -> {
                    assert reg0 != null && reg1 != null && reg2 == null;
                    if (isSame(reg0, reg1)) { localUpdated = true; line.print(); continue; }
                }
                case JUMP, CALL  -> { }
                case BNEQ, BEQ, NEG, NOT, LOGICNOT -> { }
                case EQ, NEQ, GE, LE, GEQ, LEQ -> { }
//                case ADD -> { }
//                case SUB -> { }
//                case MUL -> { }
                case MUL, DIV -> {
//                    if (isSame(reg0, reg1) && isOne(reg2)) { localUpdated = true; continue; } // TODO: is correct?
//                    if (isSame(reg0, reg2) && isOne(reg1)) { localUpdated = true; continue; }
                }
                case MOD -> { }
//                case OR  -> { }
                case AND -> { }
//                case XOR -> { }
//                case SHL -> { }
//                case SHR -> { }
                case INDEX -> { }
                case LOAD, LOADSTRING -> { }
                case RETURN -> { }
                case ADD, SUB, SHL, SHR, OR, XOR, ADDI -> {
                    assert reg0 != null && reg1 != null && reg2 != null;
                    if (isSame(reg0, reg1) && isZero(reg2)) { localUpdated = true; line.print(); continue; }
                    if (isSame(reg0, reg2) && isZero(reg1)) { localUpdated = true; line.print(); continue; }
                }
                case LW, SW -> { }
            }
            newLineList.add(line);
        }
        curBlock.setLineList(newLineList);
        updated |= localUpdated;
    }

    void trimRedundant_multi(IRBlock block) {
        boolean localUpdate = false;
        for (int i = 0; i < block.getLineList().size(); ++i) {
            IRLine line = block.getLineList().get(i);
            IRLine.OPCODE opcode = line.getOpcode();
            ArrayList<IRReg> regList = line.getRegList();
            IRReg reg0 = regList.size() > 0 ? regList.get(0) : null;
            IRReg reg1 = regList.size() > 1 ? regList.get(1) : null;
            IRReg reg2 = regList.size() > 2 ? regList.get(2) : null;
            switch (opcode) {
                // TODO
                case FUNC, LABEL -> { }
                case MOVE  -> { }
                case JUMP, CALL  -> { }
                case BNEQ, BEQ, NEG, NOT, LOGICNOT -> { }
                case EQ, NEQ, GE, LE, GEQ, LEQ -> { }
//                case ADD -> { }
//                case SUB -> { }
//                case MUL -> { }
                case MUL, DIV, MOD -> { }
//                case OR  -> { }
                case AND -> { }
//                case XOR -> { }
//                case SHL -> { }
//                case SHR -> { }
                case INDEX -> { }
                case LOAD, LOADSTRING -> { }
                case RETURN -> { }
                case ADD, SUB, SHL, SHR, OR, XOR, ADDI -> { }
                case LW, SW -> { }
            }
        }
    }

    @Override
    public void opt() {
        updated = false;
        for (IRBlock block : curBlockList.getBlockList()) {
            trimRedundant_single(block); // `addi x 0` and so on
//            trimRedundant_multi(block);
        }
    }
}
