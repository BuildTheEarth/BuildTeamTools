package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.configuration.RailType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RailTypeDraftTest {
    @Test
    void everyBuildingRoleSupportsAnIndependentPaletteWhileIconStaysSingle() {
        List<XMaterial> palette = List.of(XMaterial.STONE, XMaterial.COBBLESTONE);
        RailType source = RailType.createCustom(new RailType.Configuration()
                .railBlocks(palette).sleeperBlocks(palette).overheadPoleBlocks(palette)
                .overheadSupportBlocks(palette).overheadWireBlocks(palette).blocksBelow(palette)
                .icon(XMaterial.RAIL).trackCount(1).trackSpacing(5));
        RailTypeDraft draft = RailTypeDraft.from(source, false);
        for (RailBlockRole role : RailBlockRole.values()) {
            if (role == RailBlockRole.ICON)
                continue;
            assertEquals(palette, role.getSelected(draft));
            List<XMaterial> selection = new ArrayList<>(List.of(XMaterial.ANVIL, XMaterial.CHIPPED_ANVIL));
            role.apply(draft, selection);
            selection.clear();
            assertEquals(List.of(XMaterial.ANVIL, XMaterial.CHIPPED_ANVIL), role.getSelected(draft));
        }
        assertEquals(List.of(XMaterial.RAIL), RailBlockRole.ICON.getSelected(draft));
        assertEquals(palette, source.getRailBlocks());
        assertEquals(palette, source.getSleeperBlocks());
        assertEquals(palette, source.getOverheadPoleBlocks());
        assertEquals(palette, source.getOverheadSupportBlocks());
        assertEquals(palette, source.getOverheadWireBlocks());
    }

    private RailType source() {
        return RailType.createCustom(new RailType.Configuration()
                .identifier("custom-1").displayName("Regional Railway")
                .icon(XMaterial.RAIL).railBlock(XMaterial.ANVIL)
                .blocksBelow(List.of(XMaterial.DEAD_FIRE_CORAL_BLOCK, XMaterial.STONE, XMaterial.COBBLESTONE))
                .sleeperBlock(XMaterial.SPRUCE_PLANKS).sleeperSpacing(4)
                .trackCount(3).trackSpacing(5).trackSpacings(List.of(5, 8))
                .overheadPolesEnabled(true).overheadPoleBlock(XMaterial.LIGHT_GRAY_CONCRETE)
                .overheadSupportBlock(XMaterial.STONE).overheadPoleSpacing(20)
                .overheadPoleOffset(4).overheadPoleHeight(7)
                .overheadWiresEnabled(true).overheadWireBlock(XMaterial.IRON_BARS)
                .trackSwitchesEnabled(true));
    }

    @Test
    void copyingCustomTypePreservesConfigurationWithoutReusingIdentity() {
        RailType source = source();
        RailTypeDraft copy = RailTypeDraft.from(source, false);
        assertNull(copy.getIdentifier());
        assertNull(copy.getDisplayName());
        assertEquals(source.getBlocksBelow(), copy.getBlocksBelow());
        assertEquals(source.getRailBlock(), copy.getRailBlock());
        assertEquals(source.getTrackSpacings(), copy.getTrackSpacings());
        assertEquals(source.getSleeperSpacing(), copy.getSleeperSpacing());
        assertEquals(source.getOverheadPoleSpacing(), copy.getOverheadPoleSpacing());
        assertEquals(source.getOverheadSupportBlock(), copy.getOverheadSupportBlock());
        assertEquals(source.isTrackSwitchesEnabled(), copy.isTrackSwitchesEnabled());

        copy.setTrackSpacing(0, 9);
        assertEquals(List.of(5, 8), source.getTrackSpacings());
    }

    @Test
    void editingPreservesIdentityAndEntirePalette() {
        RailType source = source();
        RailTypeDraft edit = RailTypeDraft.from(source, true);
        assertEquals(source.getIdentifier(), edit.getIdentifier());
        assertEquals(source.getDisplayName(), edit.getDisplayName());
        assertEquals(source.getBlocksBelow(), RailBlockRole.BLOCK_BELOW.getSelected(edit));
        RailBlockRole.RAIL_BLOCK.apply(edit, List.of(XMaterial.CHIPPED_ANVIL));
        assertEquals(XMaterial.CHIPPED_ANVIL, edit.getRailBlock());
        assertEquals(source.getBlocksBelow(), edit.getBlocksBelow());
    }

    @Test
    void applyingPaletteKeepsAllMaterialsAndIsolatesSelectionChanges() {
        RailTypeDraft draft = RailTypeDraft.from(source(), true);
        List<XMaterial> selection = new ArrayList<>(List.of(XMaterial.GRAVEL, XMaterial.STONE));
        RailBlockRole.BLOCK_BELOW.apply(draft, selection);
        selection.clear();
        assertEquals(List.of(XMaterial.GRAVEL, XMaterial.STONE), draft.getBlocksBelow());
    }

    private RailType mixedType() {
        return RailType.createCustom(new RailType.Configuration()
                .identifier("mixed").displayName("Mixed ballast")
                .blocksBelow(List.of(XMaterial.GRAVEL, XMaterial.STONE, XMaterial.GRAVEL))
                .trackCount(2).trackSpacing(5));
    }

    @Test
    void unrelatedEditsPreserveBallastOrderAndWeights() {
        RailType original = mixedType();
        RailTypeDraft draft = RailTypeDraft.from(original, true);
        draft.setDisplayName("Renamed railway");
        draft.setTrackCount(3);
        draft.setOverheadWiresEnabled(true);

        assertEquals(original.getBlocksBelow(), draft.getBlocksBelow());
        assertEquals("mixed", draft.getIdentifier());
    }

    @Test
    void copyingATypePreservesTheEntireMix() {
        RailType original = mixedType();
        RailTypeDraft draft = RailTypeDraft.from(original, false);

        assertNull(draft.getIdentifier());
        assertEquals(original.getBlocksBelow(), draft.getBlocksBelow());
    }

    @Test
    void choosingBallastExplicitlyReplacesTheMix() {
        RailType original = mixedType();
        RailTypeDraft draft = RailTypeDraft.from(original, true);
        RailBlockRole.BLOCK_BELOW.apply(draft, XMaterial.STONE);

        assertEquals(List.of(XMaterial.STONE), draft.getBlocksBelow());
        assertEquals(3, original.getBlocksBelow().size());
    }
}
