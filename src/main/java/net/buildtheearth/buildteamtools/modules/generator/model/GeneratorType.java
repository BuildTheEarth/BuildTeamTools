package net.buildtheearth.buildteamtools.modules.generator.model;

import lombok.Getter;
import net.buildtheearth.buildteamtools.utils.WikiLinks;

public enum GeneratorType {

    HOUSE("House", WikiLinks.Gen.HOUSE),
    ROAD("Road", WikiLinks.Gen.ROAD),
    RAILWAY("Railway", WikiLinks.Gen.RAIL),
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

}
