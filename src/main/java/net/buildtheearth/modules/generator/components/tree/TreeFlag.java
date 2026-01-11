package net.buildtheearth.modules.generator.components.tree;

import lombok.Getter;
import net.buildtheearth.modules.generator.model.Flag;
import net.buildtheearth.modules.generator.model.FlagType;
import org.jspecify.annotations.Nullable;

public enum TreeFlag implements Flag {

    TYPE("t", FlagType.TREE_TYPE),
    WIDTH("w", FlagType.TREE_WIDTH),
    HEIGHT("h", FlagType.STRING);

    @Getter
    private final String flag;

    @Getter
    private final FlagType flagType;

    TreeFlag(String flag, FlagType flagType){
        this.flag = flag;
        this.flagType = flagType;
    }

    public static @Nullable TreeFlag byString(String flag) {
        for(TreeFlag treeFlag : TreeFlag.values())
            if(treeFlag.getFlag().equalsIgnoreCase(flag))
                return treeFlag;

        return null;
    }
}