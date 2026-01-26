package net.buildtheearth.buildteamtools.modules.generator.components.tree;

import lombok.Getter;
import org.jspecify.annotations.Nullable;

public enum TreeWidth {

    ANY("any"),
    THIN("thin"),
    NORMAL("normal"),
    WIDE("wide"),
    XXL("xxl"),
    DEAD("dead");

    @Getter
    private final String name;

    TreeWidth(String name) {
        this.name = name;
    }

    public static @Nullable TreeWidth byString(String width) {
        for (TreeWidth treeWidth : TreeWidth.values())
            if (treeWidth.getName().equalsIgnoreCase(width))
                return treeWidth;

        return null;
    }
}
