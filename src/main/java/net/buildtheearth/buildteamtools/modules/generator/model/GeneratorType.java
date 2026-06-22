package net.buildtheearth.buildteamtools.modules.generator.model;

import lombok.Getter;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.utils.WikiLinks;

import java.util.Arrays;

public enum GeneratorType {

    HOUSE("House", WikiLinks.Gen.HOUSE),
    ROAD("Road", WikiLinks.Gen.ROAD),
    RAIL("Rail", WikiLinks.Gen.RAIL),
    TREE("Tree", WikiLinks.Gen.TREE),
    FIELD("Field", WikiLinks.Gen.FIELD);

    @Getter
    private final String name;

    @Getter
    private final String wikiPage;

    GeneratorType(String name, String wikiPage) {
        this.name = name;
        this.wikiPage = wikiPage;
    }

    public String getCommandName() {
        return name().toLowerCase();
    }

    public GeneratorComponent getComponent(GeneratorModule generatorModule) {
        return switch (this) {
            case HOUSE -> generatorModule.getHouse();
            case ROAD -> generatorModule.getRoad();
            case RAIL -> generatorModule.getRail();
            case TREE -> generatorModule.getTree();
            case FIELD -> generatorModule.getField();
        };
    }

    public static GeneratorType fromCommandName(String commandName) {
        return Arrays.stream(values())
                .filter(type -> type.getCommandName().equalsIgnoreCase(commandName))
                .findFirst()
                .orElse(null);
    }

}
