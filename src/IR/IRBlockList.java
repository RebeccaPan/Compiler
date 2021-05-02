package IR;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class IRBlockList {
    private ArrayList<Integer> globalList = new ArrayList<>();
    private ArrayList<IRBlock> blockList = new ArrayList<>();
    private ArrayList<String> stringList = new ArrayList<>();
    public boolean mainNeedRet = true;
    private Map<String, Integer> classVarNumMap = new LinkedHashMap<>();
    private int maxLabel = 0;

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

    public void jumpUpdate() { blockList.forEach(x -> x.jumpOpt(maxLabel)); }
    public void labelOpt() { blockList.forEach(x -> x.labelOpt(maxLabel)); }
    public void fulfill() { blockList.forEach(IRBlock::fulfill); }
//    public void allocate() { blockList.forEach(IRBlock::allocate); }
    public void graphColor() { blockList.forEach(x -> x.graphColor(maxLabel)); }
    public void fulfillLocal() { blockList.forEach(IRBlock::fulfillLocal); }
    public void allocateLocal() { blockList.forEach(IRBlock::allocateLocal); }
    public void trim() { blockList.forEach(IRBlock::trim); }
    public void calcRAM() { blockList.forEach(IRBlock::calcRAM); }

/*    public void initASM() {
        System.out.println("---1");
        print();
        blockList.forEach(IRBlock::fulfill);
        System.out.println("---2");
        print();
        blockList.forEach(IRBlock::allocate);
        System.out.println("---3");
        print();
        blockList.forEach(IRBlock::fulfillLocal);
        System.out.println("---4");
        print();
        blockList.forEach(IRBlock::allocateLocal);
        System.out.println("---5");
        print();
        blockList.forEach(IRBlock::trim);
        System.out.println("---6");
        print();
        blockList.forEach(IRBlock::calcRAM);
    }*/

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

    public ArrayList<Integer> getGlobalList() { return globalList; }
    public ArrayList<String> getStringList() { return stringList; }
    public ArrayList<IRBlock> getBlockList() { return blockList; }
    public void putClassVarNum(String ID, int num) { classVarNumMap.put(ID, num); }
    public Integer getClassVarNum(String ID) { return classVarNumMap.get(ID); }
    public int getMaxLabel() { return maxLabel; }
    public void setMaxLabel(int maxLabel) { this.maxLabel = maxLabel; }
}
