package net.buildtheearth.modules.generator.components.tree;

import lombok.Getter;

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
}
