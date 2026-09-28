package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import net.buildtheearth.buildteamtools.modules.generator.components.rail.configuration.RailType;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.heads.HeadColor;
import net.buildtheearth.buildteamtools.utils.heads.HeadFactory;
import net.buildtheearth.buildteamtools.utils.heads.HeadTexture;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

/** Configures the distance for each gap between up to eight parallel tracks. */
final class RailTrackSpacingMenu extends AbstractMenu {

    private static final int[] GAP_SLOTS = {2, 6, 11, 15, 20, 24, 29};
    private static final String FILLED_MASK_ROW = "111111111";
    private static final int BACK_ITEM_SLOT = 36;
    private static final int DONE_ITEM_SLOT = 44;

    private final RailTypeDraft draft;

    RailTrackSpacingMenu(Player player, RailTypeDraft draft, boolean autoLoad) {
        super(5, "Track Spacing Settings", player, false);
        this.draft = draft;

        if (autoLoad)
            reloadMenuAsync();
    }

    @Override
    protected void setPreviewItems() {
        if (getMask() != null)
            getMask().apply(getMenu());

        for (int gapIndex = 0; gapIndex < draft.getTrackSpacings().size(); gapIndex++) {
            createCounter(
                    gapIndex % 2 == 0 ? HeadColor.WHITE : HeadColor.LIGHT_GRAY,
                    GAP_SLOTS[gapIndex],
                    "Tracks " + (gapIndex + 1) + " & " + (gapIndex + 2),
                    draft.getTrackSpacings().get(gapIndex),
                    RailType.MIN_TRACK_SPACING,
                    RailType.MAX_TRACK_SPACING,
                    "Blocks"
            );
        }

        setBackItem(BACK_ITEM_SLOT, new RailTypeEditorMenu(getMenuPlayer(), draft, false));
        getMenu().getSlot(DONE_ITEM_SLOT).setItem(HeadFactory.head(HeadTexture.CHECKMARK, "§aDone"));
        getMenu().open(getMenuPlayer());
    }

    @Override
    protected void setMenuItemsAsync() {
        // All values live in the local draft.
    }

    @Override
    protected void setItemClickEventsAsync() {
        for (int gapIndex = 0; gapIndex < draft.getTrackSpacings().size(); gapIndex++) {
            int selectedGap = gapIndex;
            int slot = GAP_SLOTS[gapIndex];

            getMenu().getSlot(slot - 1).setClickHandler((player, click) -> changeSpacing(player, selectedGap, -1));
            getMenu().getSlot(slot + 1).setClickHandler((player, click) -> changeSpacing(player, selectedGap, 1));
        }

        getMenu().getSlot(DONE_ITEM_SLOT).setClickHandler((player, click) -> {
            player.closeInventory();
            playSound(player, Sound.UI_BUTTON_CLICK);
            new RailTypeEditorMenu(player, draft, true);
        });
    }

    private void changeSpacing(Player player, int gapIndex, int change) {
        int current = draft.getTrackSpacings().get(gapIndex);
        int updated = current + change;

        if (updated < RailType.MIN_TRACK_SPACING || updated > RailType.MAX_TRACK_SPACING) {
            playSound(player, Sound.ENTITY_ITEM_BREAK);
            return;
        }

        draft.setTrackSpacing(gapIndex, updated);
        playSound(player, Sound.UI_BUTTON_CLICK);
        reloadMenuAsync();
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern(FILLED_MASK_ROW)
                .pattern(FILLED_MASK_ROW)
                .pattern(FILLED_MASK_ROW)
                .pattern(FILLED_MASK_ROW)
                .pattern(FILLED_MASK_ROW)
                .build();
    }

    private void playSound(Player player, Sound sound) {
        player.playSound(player, sound, 1.0F, 1.0F);
    }
}
