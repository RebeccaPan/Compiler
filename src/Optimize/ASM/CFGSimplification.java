package Optimize.ASM;

import IR.*;
import Optimize.Opt;
import Util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CFGSimplification extends Opt {
    private Map<String, IRBlock> blockNameMap;
    private IRBlock getBlock(String str) { return blockNameMap.get(str); }

    public CFGSimplification(IRBlockList _curBlockList) { super(_curBlockList); }

    private void merge(String strA, int index, String strB) { // merge blockB to `CALL blockB` in blockA
        IRBlock blockA = getBlock(strA);
        IRBlock blockB = getBlock(strB);
        assert blockA.getLineList().get(index).getOpcode() == IRLine.OPCODE.CALL;
        ArrayList<IRLine> newLineList = blockA.getLineList();
        for (int i = 0; i < index; ++i)
            newLineList.add(blockA.getLineList().get(i));
        newLineList.addAll(blockB.getLineList());
        for (int i = index + 1; i < blockA.getLineList().size(); ++i)
            newLineList.add(blockA.getLineList().get(i));
        blockA.setLineList(newLineList);
        curBlockList.getBlockList().remove(blockB);
    }

    @Override
    public void opt() {
        updated = false;
        blockNameMap = new LinkedHashMap<>();
        for (IRBlock block : curBlockList.getBlockList()) {
            blockNameMap.put(block.getID(), block);
        }
        for (IRBlock block : curBlockList.getBlockList()) {
            if (!block.containsCall()) continue;
            for (int i = 0; i < block.getLineList().size(); ++i) {
                IRLine line = block.getLineList().get(i);
                if (line.getOpcode() == IRLine.OPCODE.JUMP) {
                    IRBlock curBlock = getBlock(line.getFuncStr());
                    if (curBlock == null) continue;
                    curBlock.jumpedBy.add(new Pair<>(block.getID(), i));
                }
            }
        }
        for (IRBlock block : curBlockList.getBlockList()) {
            if (block.jumpedBy.size() == 1) {
                merge(block.jumpedBy.get(0).getK(), block.jumpedBy.get(0).getV(), block.getID());
                updated = true;
            }
        }
    }
}
