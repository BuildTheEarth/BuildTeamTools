package net.buildtheearth.buildteam.components.generator.tree;

import net.buildtheearth.buildteam.components.generator.Flag;
import net.buildtheearth.buildteam.components.generator.FlagType;

public enum TreeFlag implements Flag {

    TYPE("t", FlagType.ENUM),
    WIDTH("w", FlagType.INTEGER),
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
