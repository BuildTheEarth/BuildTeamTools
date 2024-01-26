package net.buildtheearth.modules.warp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.modules.warp.model.Warp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpGroup {

    @Getter
    private UUID id = UUID.randomUUID();

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String description;

    @Getter
    private final List<Warp> warps;


    public WarpGroup(String name, String description) {
        this.name = name;
        this.description = description;
        this.warps = new ArrayList<>();
    }

    public WarpGroup(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.warps = new ArrayList<>();
    }
}
