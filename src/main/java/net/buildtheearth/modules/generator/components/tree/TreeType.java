package net.buildtheearth.modules.generator.components.tree;

import lombok.Getter;
import org.jspecify.annotations.Nullable;

public enum TreeType {

    ANY("any"),
    ACACIA("acacia"),
    AFRICA("africa"),
    ALEXANDER_PALM("alexanderpalm"),
    AMERICA("america"),
    ASIA("asia"),
    BIRCH("birch"),
    BUSH("bush"),
    CARIBBEAN_PALM("caribbeanpalm"),
    CEDAR("cedar"),
    COASTAL_FIR("coastalfir"),
    COCONUT_PALM("coconutpalm"),
    CYPRESS("cypress"),
    DEAD("dead"),
    EUROPE("europe"),
    FIR("fir"),
    GIANT_SEQUOIA("giantsequoia"),
    KING_PALM("kingpalm"),
    LARCH("larch"),
    LONGLEAF_PINE("longleafpine"),
    MOUNTAIN_ASH("mountainash"),
    OAK("oak"),
    OCEANIA("oceania"),
    OTHER("other"),
    PALM("palm"),
    PLANE("plane"),
    POPLAR("poplar"),
    RAFFIA_PALM("raffiapalm"),
    ROCK("rock"),
    SCOTTS_PINE("scottspine"),
    SNOWY_LARCH("snowylarch"),
    SNOWY_LONGLEAF("snowylongleaf"),
    SNOWY_SPRUCE("snowyspruce"),
    SPRUCE("spruce"),
    SWAMP("swamp"),
    SWAMP_ROOTS("swamproots"),
    TROPICAL("tropical"),
    WILLOW("willow"),
    YELLOW_MERANTI("yellowmeranti");

    @Getter
    private final String name;

    TreeType(String name) {
        this.name = name;
    }

    public static @Nullable TreeType byString(String check) {
        for (TreeType type : TreeType.values())
            if (type.getName().equalsIgnoreCase(check))
                return type;

        return null;
    }
}
