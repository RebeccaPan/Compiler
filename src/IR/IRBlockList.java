package IR;

import java.util.ArrayList;
import java.util.HashMap;

public class IRBlockList {
    private ArrayList<Integer> globalList = new ArrayList<>();
    private ArrayList<IRBlock> blockList = new ArrayList<>();
    private ArrayList<String> stringList = new ArrayList<>();
    private HashMap<String, Integer> classSizeMap = new HashMap<>();
    private boolean withRet;

    public IRBlockList() {
        // left empty
    }

    public void addBlock(IRBlock block) {
        blockList.add(block);
    }
    public int addStr(String str) {
        stringList.add(str);
        return stringList.size() - 1; // label: 0-base
    }
    public void addGlobal(Integer global) {
        globalList.add(global);
    }

    public void print() {
        for (int i = 0; i < stringList.size(); ++i) {
            System.out.println("STRING(" + i + ") " + stringList.get(i));
        }
        for (int i = 0; i < globalList.size(); ++i) {
            System.out.println("GLOBAL(" + i + ") " + globalList.get(i));
        }
        blockList.forEach(IRBlock::print);
    }

    public void initASM() {
        blockList.forEach(x -> x.fulfill());
        blockList.forEach(IRBlock::allocate);
        blockList.forEach(IRBlock::fulfillLocal);
        blockList.forEach(IRBlock::allocateLocal);
        blockList.forEach(IRBlock::trim);
        blockList.forEach(IRBlock::calcRAM);
//        System.out.println("----------------------");
//        System.out.println("After all these ordeal");
//        print();
//        System.out.println("----------------------");
    }

    public void printASM() {
        String str = "";
        if (!stringList.isEmpty() || !globalList.isEmpty()) {
            str = "\t.text\n";
            for (int i = 0; i < stringList.size(); ++i) {
                if (i == 0) str += "\t.section\t.rodata\n";
                str += "\t.align\t2\n"
                    +  ".LS" + i + ":\n"
                    +  "\t.string\t\"" + stringList.get(i) + "\"\n";
            }
            for (int i = 0; i < globalList.size(); ++i) {
                String globalStr = ".G" + i;
                str += "\t.globl\t.G" + i + "\n";
                if (i == 0) str += "\t.section\t.sbss,\"aw\",@nobits\n";
                str += "\t.align\t2\n"
                    +  "\t.type\t.G" + i + ", @object\n"
                    +  "\t.size\t.G" + i + ", 4\n"
                    +  ".G" + i + ":\n"
                    +  "\t.zero\t4\n";
            }
        }
        System.out.print(str);
        blockList.forEach(IRBlock::printASM);
    }

    public boolean isWithRet() { return withRet; }
}
