package net.buildtheearth.modules.warp.model;

import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.modules.utils.ListUtil;
import net.buildtheearth.modules.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class WarpGroup {

    @Getter
    private UUID id = UUID.randomUUID();

    @Getter
    private BuildTeam buildTeam;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String description;

    @Getter
    private final List<Warp> warps;


    public WarpGroup(BuildTeam buildTeam, String name, String description) {
        this.buildTeam = buildTeam;
        this.name = name;
        this.description = description;
        this.warps = new ArrayList<>();
    }

    public WarpGroup(UUID id, BuildTeam buildTeam, String name, String description) {
        this.id = id;
        this.buildTeam = buildTeam;
        this.name = name;
        this.description = description;
        this.warps = new ArrayList<>();
    }

    public ArrayList<String> getDescriptionLore() {
        return new ArrayList<String>() {{
            add("");
            add("§eDescription:");
            addAll(ListUtil.createList(Utils.splitStringByLineLength(description, 30, ", ")));
        }};
    }
}
