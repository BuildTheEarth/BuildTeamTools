package net.buildtheearth.buildteamtools.modules.generator.components.rail.configuration;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RailPaletteStorageTest {
    @Test
    void saveValidationReceivesAllPalettesWithoutCollapsingToTheirFirstMaterial() {
        List<XMaterial> mix = List.of(XMaterial.STONE, XMaterial.COBBLESTONE);
        RailType original = RailType.createCustom(new RailType.Configuration()
                .railBlocks(mix).sleeperBlocks(mix).overheadPoleBlocks(mix)
                .overheadSupportBlocks(mix).overheadWireBlocks(mix));
        RailType.Configuration saved = RailTypeManager.toConfiguration(original);
        assertEquals(mix, saved.railBlocks());
        assertEquals(mix, saved.sleeperBlocks());
        assertEquals(mix, saved.overheadPoleBlocks());
        assertEquals(mix, saved.overheadSupportBlocks());
        assertEquals(mix, saved.overheadWireBlocks());
    }

    @Test
    void acceptsLegacyScalarAndPreservesPaletteOrderAndWeights() {
        assertEquals(List.of(XMaterial.ANVIL), RailTypeManager.parsePalette("ANVIL"));
        assertEquals(List.of(XMaterial.ANVIL, XMaterial.CHIPPED_ANVIL, XMaterial.ANVIL),
                RailTypeManager.parsePalette(List.of("ANVIL", "CHIPPED_ANVIL", "ANVIL")));
        assertEquals(List.of(), RailTypeManager.parsePalette(null));
        assertNull(RailTypeManager.parsePalette(List.of("NOT_A_BLOCK")).getFirst());
        assertNull(RailTypeManager.parsePalette(List.of(123)).getFirst());
    }

    @Test
    void migrationConvertsEveryScalarAndPreservesExistingMixes() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        List<String> paths = List.of("rail-block", "sleeper-block", "overhead.poles.block",
                "overhead.support.block", "overhead.wires.block");
        for (String path : paths)
            config.set("rail-types.example." + path, "STONE");
        config.set("rail-types.example.blocks-below", List.of("GRAVEL", "STONE", "GRAVEL"));
        config.set("rail-types.mixed.rail-block", List.of("ANVIL", "CHIPPED_ANVIL"));

        RailTypeManager.migrateVersionFourToFive(config);
        RailTypeManager.migrateVersionFourToFive(config);
        YamlConfiguration reloaded = new YamlConfiguration();
        reloaded.loadFromString(config.saveToString());
        for (String path : paths)
            assertEquals(List.of(XMaterial.STONE), RailTypeManager.parsePalette(reloaded.get("rail-types.example." + path)));
        assertEquals(List.of("GRAVEL", "STONE", "GRAVEL"), reloaded.getStringList("rail-types.example.blocks-below"));
        assertEquals(List.of("ANVIL", "CHIPPED_ANVIL"), reloaded.getStringList("rail-types.mixed.rail-block"));
    }
}
