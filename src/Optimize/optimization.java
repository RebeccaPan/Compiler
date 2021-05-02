package Optimize;

import IR.*;
import Optimize.ASM.CFGSimplification;
import Optimize.ASM.ImmInstructionMerge;
import Optimize.ASM.RedundantInstRemove;
import Optimize.Opt;

public class optimization extends Opt {
    private void OptAsm() {
        updated = true;
        while (updated) {
            updated = false;
            RedundantInstRemove opt1 = new RedundantInstRemove(curBlockList);
            opt1.opt(); updated |= opt1.updated;
            CFGSimplification opt2 = new CFGSimplification(curBlockList);
            opt2.opt(); updated |= opt2.updated;
            ImmInstructionMerge opt3 = new ImmInstructionMerge(curBlockList);
            opt3.opt(); updated |= opt3.updated;
        }
    }

    public optimization(IRBlockList _curBlockList) { super(_curBlockList); }

    @Override
    public void opt() {
        OptAsm();
    }
}
