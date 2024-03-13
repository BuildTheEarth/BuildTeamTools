package net.buildtheearth.modules.generator.model;

import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.world.block.BlockState;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class Operation {

    public enum OperationType {

        COMMAND(String.class),

        BREAKPOINT,

        PASTE_SCHEMATIC(String.class, Location.class, Double.class),

        CUBOID_SELECTION(Vector.class, Vector.class),

        POLYGONAL_SELECTION(Vector[].class),

        CONVEX_SELECTION(Vector[].class),

        REPLACE_XMATERIALS(XMaterial.class, XMaterial.class),

        REPLACE_BLOCKSTATES(BlockState.class, BlockState.class),

        REPLACE_XMATERIALS_WITH_MASKS(String[].class, XMaterial.class, XMaterial.class, Integer.class),

        REPLACE_BLOCKSTATES_WITH_MASKS(String[].class, BlockState.class, BlockState.class, Integer.class),

        CLEAR_HISTORY,

        DISABLE_GMASK,

        EXPAND_SELECTION(Vector.class);

        @Getter
        private final Object[] valueTypes;

        OperationType(Object... valueTypes){
            this.valueTypes = valueTypes;
        }
    }

    @Getter
    private String value = null;

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

        this.value = value;
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
}
