package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.configuration.RailType;

import java.util.List;

/**
 * Mutable state of a rail type that is being created in the editor menu.
 */
@Getter
@Setter
class RailTypeDraft {

    private String identifier;
    private String displayName;
    private XMaterial icon = XMaterial.RAIL;
    private List<XMaterial> railBlocks = List.of(XMaterial.ANVIL);
    private List<XMaterial> blocksBelow = List.of(XMaterial.GRAVEL);
    private List<XMaterial> sleeperBlocks = List.of(XMaterial.SPRUCE_PLANKS);
    private int sleeperSpacing = RailType.MIN_SLEEPER_SPACING;
    private int trackCount = RailType.DEFAULT_TRACK_COUNT;
    private int trackSpacing = RailType.DEFAULT_TRACK_SPACING;
    private List<Integer> trackSpacings = new java.util.ArrayList<>(List.of(RailType.DEFAULT_TRACK_SPACING));
    private boolean overheadPolesEnabled = true;
    private List<XMaterial> overheadPoleBlocks = List.of(XMaterial.LIGHT_GRAY_CONCRETE);
    private List<XMaterial> overheadSupportBlocks = List.of(XMaterial.LIGHT_GRAY_CONCRETE);
    private int overheadPoleSpacing = RailType.DEFAULT_OVERHEAD_POLE_SPACING;
    private int overheadPoleOffset = RailType.DEFAULT_OVERHEAD_POLE_OFFSET;
    private int overheadPoleHeight = RailType.DEFAULT_OVERHEAD_POLE_HEIGHT;
    private boolean overheadWiresEnabled = true;
    private List<XMaterial> overheadWireBlocks = List.of(XMaterial.IRON_BARS);
    private boolean trackSwitchesEnabled;

    static RailTypeDraft from(RailType railType, boolean keepIdentity) {
        RailTypeDraft draft = new RailTypeDraft();
        List<XMaterial> blocksBelow = railType.getBlocksBelow();

        if (keepIdentity) {
            draft.identifier = railType.getIdentifier();
            draft.displayName = railType.getDisplayName();
        }

        draft.icon = railType.getIcon();
        draft.railBlocks = railType.getRailBlocks().isEmpty() ? draft.railBlocks : List.copyOf(railType.getRailBlocks());
        draft.blocksBelow = blocksBelow.isEmpty() ? List.of(XMaterial.GRAVEL) : List.copyOf(blocksBelow);
        draft.sleeperBlocks = railType.getSleeperBlocks().isEmpty() ? draft.sleeperBlocks : List.copyOf(railType.getSleeperBlocks());
        draft.sleeperSpacing = railType.getSleeperSpacing();
        draft.trackCount = railType.getTrackCount();
        draft.trackSpacing = railType.getTrackSpacing();
        draft.trackSpacings = new java.util.ArrayList<>(railType.getTrackSpacings());
        draft.overheadPolesEnabled = railType.isOverheadPolesEnabled();
        draft.overheadPoleBlocks = railType.getOverheadPoleBlocks().isEmpty() ? draft.overheadPoleBlocks : List.copyOf(railType.getOverheadPoleBlocks());
        draft.overheadSupportBlocks = railType.getOverheadSupportBlocks().isEmpty() ? draft.overheadSupportBlocks : List.copyOf(railType.getOverheadSupportBlocks());
        draft.overheadPoleSpacing = railType.getOverheadPoleSpacing();
        draft.overheadPoleOffset = railType.getOverheadPoleOffset();
        draft.overheadPoleHeight = railType.getOverheadPoleHeight();
        draft.overheadWiresEnabled = railType.isOverheadWiresEnabled();
        draft.overheadWireBlocks = railType.getOverheadWireBlocks().isEmpty() ? draft.overheadWireBlocks : List.copyOf(railType.getOverheadWireBlocks());
        draft.trackSwitchesEnabled = railType.isTrackSwitchesEnabled();
        return draft;
    }

    XMaterial getBlockBelow() {
        return blocksBelow.getFirst();
    }

    // The picker replaces the mix only after the user explicitly chooses a block.
    void setBlockBelow(XMaterial blockBelow) {
        blocksBelow = List.of(blockBelow);
    }

    XMaterial getRailBlock() {
        return railBlocks.getFirst();
    }

    void setRailBlock(XMaterial material) {
        railBlocks = List.of(material);
    }

    XMaterial getSleeperBlock() {
        return sleeperBlocks.getFirst();
    }

    void setSleeperBlock(XMaterial material) {
        sleeperBlocks = List.of(material);
    }

    XMaterial getOverheadPoleBlock() {
        return overheadPoleBlocks.getFirst();
    }

    void setOverheadPoleBlock(XMaterial material) {
        overheadPoleBlocks = List.of(material);
    }

    XMaterial getOverheadSupportBlock() {
        return overheadSupportBlocks.getFirst();
    }

    void setOverheadSupportBlock(XMaterial material) {
        overheadSupportBlocks = List.of(material);
    }

    XMaterial getOverheadWireBlock() {
        return overheadWireBlocks.getFirst();
    }

    void setOverheadWireBlock(XMaterial material) {
        overheadWireBlocks = List.of(material);
    }

    void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
        resizeTrackSpacings();
    }

    void setTrackSpacing(int trackSpacing) {
        this.trackSpacing = trackSpacing;
        this.trackSpacings = new java.util.ArrayList<>(java.util.Collections.nCopies(
                Math.max(0, trackCount - 1),
                trackSpacing
        ));
    }

    void setTrackSpacing(int gapIndex, int spacing) {
        resizeTrackSpacings();
        trackSpacings.set(gapIndex, spacing);
        trackSpacing = trackSpacings.getFirst();
    }

    private void resizeTrackSpacings() {
        int requiredSize = Math.max(0, trackCount - 1);

        while (trackSpacings.size() < requiredSize)
            trackSpacings.add(trackSpacing);

        while (trackSpacings.size() > requiredSize)
            trackSpacings.removeLast();
    }
}
