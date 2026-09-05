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
    private XMaterial railBlock = XMaterial.ANVIL;
    private XMaterial blockBelow = XMaterial.GRAVEL;
    private XMaterial sleeperBlock = XMaterial.SPRUCE_PLANKS;
    private int sleeperSpacing = RailType.MIN_SLEEPER_SPACING;
    private int trackCount = RailType.DEFAULT_TRACK_COUNT;
    private int trackSpacing = RailType.DEFAULT_TRACK_SPACING;
    private List<Integer> trackSpacings = new java.util.ArrayList<>(List.of(RailType.DEFAULT_TRACK_SPACING));
    private boolean overheadPolesEnabled = true;
    private XMaterial overheadPoleBlock = XMaterial.LIGHT_GRAY_CONCRETE;
    private XMaterial overheadSupportBlock = XMaterial.LIGHT_GRAY_CONCRETE;
    private int overheadPoleSpacing = RailType.DEFAULT_OVERHEAD_POLE_SPACING;
    private int overheadPoleOffset = RailType.DEFAULT_OVERHEAD_POLE_OFFSET;
    private int overheadPoleHeight = RailType.DEFAULT_OVERHEAD_POLE_HEIGHT;
    private boolean overheadWiresEnabled = true;
    private XMaterial overheadWireBlock = XMaterial.IRON_BARS;
    private boolean trackSwitchesEnabled;

    static RailTypeDraft from(RailType railType, boolean keepIdentity) {
        RailTypeDraft draft = new RailTypeDraft();
        List<XMaterial> blocksBelow = railType.getBlocksBelow();

        if (keepIdentity) {
            draft.identifier = railType.getIdentifier();
            draft.displayName = railType.getDisplayName();
        }

        draft.icon = railType.getIcon();
        draft.railBlock = railType.getRailBlock();
        draft.blockBelow = blocksBelow.isEmpty() ? XMaterial.GRAVEL : blocksBelow.getFirst();
        draft.sleeperBlock = railType.getSleeperBlock() == null ? XMaterial.SPRUCE_PLANKS : railType.getSleeperBlock();
        draft.sleeperSpacing = railType.getSleeperSpacing();
        draft.trackCount = railType.getTrackCount();
        draft.trackSpacing = railType.getTrackSpacing();
        draft.trackSpacings = new java.util.ArrayList<>(railType.getTrackSpacings());
        draft.overheadPolesEnabled = railType.isOverheadPolesEnabled();
        draft.overheadPoleBlock = railType.getOverheadPoleBlock() == null
                ? XMaterial.LIGHT_GRAY_CONCRETE
                : railType.getOverheadPoleBlock();
        draft.overheadSupportBlock = railType.getOverheadSupportBlock() == null
                ? XMaterial.LIGHT_GRAY_CONCRETE
                : railType.getOverheadSupportBlock();
        draft.overheadPoleSpacing = railType.getOverheadPoleSpacing();
        draft.overheadPoleOffset = railType.getOverheadPoleOffset();
        draft.overheadPoleHeight = railType.getOverheadPoleHeight();
        draft.overheadWiresEnabled = railType.isOverheadWiresEnabled();
        draft.overheadWireBlock = railType.getOverheadWireBlock() == null
                ? XMaterial.IRON_BARS
                : railType.getOverheadWireBlock();
        draft.trackSwitchesEnabled = railType.isTrackSwitchesEnabled();
        return draft;
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
