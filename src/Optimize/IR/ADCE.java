package Optimize.IR;

import IR.IRBlockList;
import IR.*;
import Optimize.Opt;

import java.util.*;

// Advanced Dead Code Elimination
// - Get useful operands (from basic to all)
// - Check side effects of functions
// - Get useful instructions (from basic to all)
// - Delete other instructions
public class ADCE extends Opt {
    private boolean[] isActive;
    private int sizeT = 0;
    private Map<Integer, Set<IRReg>> dependencies = new HashMap<>();
    private Queue<Integer> queue = new LinkedList<>();
    private boolean[] visited;

    public ADCE(IRBlockList _curBlockList) { super(_curBlockList); }

    private void setActive(IRReg reg) { isActive[reg.getID()] = true; }
    private boolean primitivelyNeeded(IRReg reg) {
        return reg.getType() == 0 || reg.getType() == 3
            || reg.getType() == 2 || reg.getType() == 7;
    }

    private void collect(IRLine line) {
        IRLine.OPCODE op = line.getOpcode();
        if (op == IRLine.OPCODE.SW || op == IRLine.OPCODE.BEQ || op == IRLine.OPCODE.BNEQ) {
            for (IRReg reg : line.getRegList())
                if (reg.getType() == 5) setActive(reg);
        } else if (line.isDefLine()) {
            IRReg reg0 = line.getRegList().get(0);
            if (reg0.getType() == 5) {
                for (int i = 1; i < line.getRegList().size(); ++i)
                    if (line.getRegList().get(i).getType() != 8) dependencies.get(reg0.getID()).add(line.getRegList().get(i));
            } else if (primitivelyNeeded(reg0)) {
                isActive[sizeT] = true;
                queue.offer(sizeT);
                for (int i = 1; i < line.getRegList().size(); ++i) {
                    if (line.getRegList().get(i).getType() != 8) dependencies.get(sizeT).add(line.getRegList().get(i));
                }
            }
        }
        for (IRReg reg : line.getRegList()) {
            if (reg.getType() == 5)
                if (isActive[reg.getID()])
                    queue.offer(reg.getID());
        }
    }

    private void expand() { // BFS
        while (!queue.isEmpty()) {
            int cur = queue.poll();
            if (visited[cur]) continue;
            visited[cur] = true;
            Set<IRReg> regs = dependencies.get(cur);
            for (IRReg reg : regs) {
                if (reg.getType() == 5) {
                    if (isActive[reg.getID()]) continue;
                    setActive(reg);
                    queue.offer(reg.getID());
                } else if (primitivelyNeeded(reg)) {
                    if (isActive[sizeT]) continue;
                    isActive[sizeT] = true;
                    queue.offer(sizeT);
                }
            }
        }
    }

    private void delete(IRBlock block) {
        ArrayList<IRLine> newLineList = new ArrayList<>();
        for (IRLine line : block.getLineList()) {
            if (!line.isDefLine()) { newLineList.add(line); continue; }
            IRReg reg0 = line.getRegList().get(0);
            if (reg0.getType() != 5 || isActive[reg0.getID()])
                newLineList.add(line);
        }
        block.setLineList(newLineList);
    }

    @Override
    public void opt() {
        updated = false;
        // init
        for (IRBlock block : curBlockList.getBlockList())
            for (IRLine line : block.getLineList())
                for (IRReg reg : line.getRegList())
                    if (reg.getType() == 5) sizeT++;
        isActive = new boolean[sizeT + 1];
        visited = new boolean[sizeT + 1];
        for (int i = 0; i <= sizeT; ++i) {
            isActive[i] = false; visited[i] = false;
            dependencies.put(i, new LinkedHashSet<>());
        }

        queue.clear();
        // collect
        for (IRBlock block : curBlockList.getBlockList()) {
            for (IRLine line : block.getLineList()) {
                collect(line);
            }
        }
        // expand
        expand();
        // delete
        for (IRBlock block : curBlockList.getBlockList()) {
            delete(block);
        }
    }
}