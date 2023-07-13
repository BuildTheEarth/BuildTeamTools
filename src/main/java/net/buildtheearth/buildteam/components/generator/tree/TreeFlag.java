package net.buildtheearth.buildteam.components.generator.tree;

import net.buildtheearth.buildteam.components.generator.Flag;

public enum TreeFlag implements Flag {

    TYPE("t"),
    WIDTH("w"),
    HEIGHT("h");

    private String flag;

    TreeFlag(String flag){
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }

    public static TreeFlag byString(String flag){
        for(TreeFlag treeFlag : TreeFlag.values())
            if(treeFlag.getFlag().equalsIgnoreCase(flag))
                return treeFlag;

        return null;
    }
}
