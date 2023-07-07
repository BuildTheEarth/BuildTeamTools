package net.buildtheearth.buildteam.components.generator.tree;

import lombok.Getter;

public enum TreeWidth {

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
}
