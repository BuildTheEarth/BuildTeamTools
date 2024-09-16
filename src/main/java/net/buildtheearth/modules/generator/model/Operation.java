package net.buildtheearth.modules.generator.model;

import com.sk89q.worldedit.world.block.BlockState;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Operation {

    public enum OperationType {

        COMMAND(String.class),
        BREAKPOINT,
        PASTE_SCHEMATIC(String.class, Location.class, Double.class),
        CUBOID_SELECTION(Vector.class, Vector.class),
        POLYGONAL_SELECTION(Vector[].class),
        REPLACE_BLOCKSTATES(BlockState[].class, BlockState[].class),
        REPLACE_BLOCKSTATES_WITH_MASKS(String[].class, BlockState.class, BlockState[].class, Integer.class),
        DRAW_CURVE_WITH_MASKS(String[].class, Vector[].class, BlockState[].class, Boolean.class),
        DRAW_POLY_LINE_WITH_MASKS(String[].class, Vector[].class, BlockState[].class, Boolean.class, Boolean.class),
        DRAW_LINE_WITH_MASKS(String[].class, Vector.class, Vector.class, BlockState[].class, Boolean.class),
        CLEAR_HISTORY,
        SET_GMASK(String.class),
        EXPAND_SELECTION(Vector.class);

        @Getter
        private final Object[] valueTypes;

        OperationType(Object... valueTypes){
            this.valueTypes = valueTypes;
        }
    }



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

        if(operationType.getValueTypes()[0] != null && !operationType.getValueTypes()[0].equals(String.class))
            throw new IllegalArgumentException("OperationType " + operationType + " must have a value type of String");

        this.values = Collections.singletonList(value);
        this.operationType = operationType;
    }

    protected Operation(OperationType operationType, Object... values){
        if(values.length != operationType.getValueTypes().length)
            throw new IllegalArgumentException("OperationType " + operationType + " must have " + operationType.getValueTypes().length + " values");

        for(int i = 0; i < values.length; i++)
            if(values[i] != null && !values[i].getClass().equals(operationType.getValueTypes()[i]))
                throw new IllegalArgumentException("OperationType " + operationType + " must have a value type of " + operationType.getValueTypes()[i] + " at index " + i + ". Provided: " + values[i].getClass());

        this.values = Arrays.asList(values);
        this.operationType = operationType;
    }

    protected Operation(OperationType operationType){
        if(operationType.getValueTypes().length != 0)
            throw new IllegalArgumentException("OperationType " + operationType + " must have no value types");

        this.operationType = operationType;
    }

    public Object get(int i){
        return values.get(i);
    }

    public String getValuesAsString(){
        StringBuilder builder = new StringBuilder();
        for(Object value : values) {
            if(value == null){
                builder.append("null, ");
                continue;
            }

            String valueString = value.toString();

            if(value instanceof BlockState)
                valueString = ((BlockState) value).getBlockType().getNamespace();
            else if(value instanceof Vector[])
                valueString = Arrays.toString((Vector[]) value);
            else if(value instanceof BlockState[])
                valueString = Arrays.toString((BlockState[]) value);
            else if(value instanceof String[])
                valueString = Arrays.toString((String[]) value);
            else if(value instanceof Boolean)
                valueString = ((Boolean) value) ? "true" : "false";
            else if(value instanceof Double)
                valueString = String.valueOf(value);
            else if(value instanceof Integer)
                valueString = String.valueOf(value);

            builder.append(valueString).append(", ");
        }
        return builder.toString();
    }
}
