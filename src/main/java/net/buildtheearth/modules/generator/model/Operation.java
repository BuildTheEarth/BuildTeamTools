package net.buildtheearth.modules.generator.model;

import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.function.pattern.Pattern;
import lombok.Getter;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class Operation {

    public enum OperationType {

        COMMAND(String.class),

        BREAKPOINT,

        PASTE_SCHEMATIC(String.class, Location.class, Double.class),

        CUBOID_SELECTION(Vector.class, Vector.class),

        POLYGONAL_SELECTION(List.class),

        CONVEX_SELECTION(List.class),

        SET_BLOCKS_WITH_EXPRESSION_MASK(String.class, XMaterial.class),

        CLEAR_HISTORY;

        @Getter
        private final Object[] valueTypes;

        OperationType(Object... valueTypes){
            this.valueTypes = valueTypes;
        }
    }

    @Getter
    private String value = null;

    @Getter
    private List<Object> values = null;

    @Getter
    private final OperationType operationType;

    protected Operation(String command){
        this(OperationType.COMMAND, command);
    }

    protected Operation(OperationType operationType, String value){
        if(operationType.getValueTypes().length != 1)
            throw new IllegalArgumentException("OperationType " + operationType + " must have exactly one value type");

        if(operationType.getValueTypes()[0] != String.class)
            throw new IllegalArgumentException("OperationType " + operationType + " must have a value type of String");

        this.value = value;
        this.operationType = operationType;
    }

    protected Operation(OperationType operationType, Object... values){
        if(values.length != operationType.getValueTypes().length)
            throw new IllegalArgumentException("OperationType " + operationType + " must have " + operationType.getValueTypes().length + " values");

        for(int i = 0; i < values.length; i++)
            if(values[i].getClass() != operationType.getValueTypes()[i])
                throw new IllegalArgumentException("OperationType " + operationType + " must have a value type of " + operationType.getValueTypes()[i]);

        this.values = Arrays.asList(values);
        this.operationType = operationType;
    }

    protected Operation(OperationType operationType){
        if(operationType.getValueTypes().length != 0)
            throw new IllegalArgumentException("OperationType " + operationType + " must have no value types");

        this.operationType = operationType;
    }
}
