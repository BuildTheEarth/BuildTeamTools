package net.buildtheearth.modules.generator.components.tree;

import net.buildtheearth.modules.generator.model.FlagType;
import net.buildtheearth.modules.generator.model.Flag;

public enum TreeFlag implements Flag {

    TYPE("t", FlagType.TREE_TYPE),
    WIDTH("w", FlagType.TREE_WIDTH),
    HEIGHT("h", FlagType.INTEGER);

    private final String flag;
    private final FlagType flagType;

    TreeFlag(String flag, FlagType flagType){
        this.flag = flag;
        this.flagType = flagType;
    }

    public String getFlag() {
        return flag;
    }

    @Override
    public FlagType getFlagType() {
        return null;
    }

    public static TreeFlag byString(String flag){
        for(TreeFlag treeFlag : TreeFlag.values())
            if(treeFlag.getFlag().equalsIgnoreCase(flag))
                return treeFlag;

        return null;
    }
}