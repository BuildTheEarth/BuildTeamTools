package net.buildtheearth.modules.generator.model;

import lombok.Getter;

public class Operation {

    public enum OperationType {
        COMMAND,

        BREAKPOINT,

        PASTE_SCHEMATIC,

        CUBOID_SELECTION,

        POLYGONAL_SELECTION,

        CONVEX_SELECTION;
    }

    @Getter
    private String value = null;

    @Getter
    private final OperationType operationType;

    public Operation(String command){
        this(OperationType.COMMAND, command);
    }

    public Operation(OperationType operationType, String value){
        this.value = value;
        this.operationType = operationType;
    }

    public Operation(OperationType operationType){
        if(operationType == OperationType.COMMAND)
            throw new IllegalArgumentException("OperationType COMMAND must have a value");

        this.operationType = operationType;
    }
}
