package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.Rail;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.RailFlag;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.RailSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.configuration.RailType;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.configuration.RailTypeManager;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.heads.HeadColor;
import net.buildtheearth.buildteamtools.utils.heads.HeadFactory;
import net.buildtheearth.buildteamtools.utils.heads.HeadTexture;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.wesjd.anvilgui.AnvilGUI;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/**
 * Editor for creating a custom rail type in-game. The configured draft is
 * validated and saved persistently through the {@link RailTypeManager}.
 */
public class RailTypeEditorMenu extends AbstractMenu {

    public static final String EDITOR_INV_NAME = "Create a Rail Type";
    private static final String FILLED_MASK_ROW = "111111111";

    private static final int NAME_SLOT = 14;

    private static final int TRACK_COUNT_SLOT = 2;
    private static final int TRACK_SPACING_SLOT = 11;
    private static final int SLEEPER_SPACING_SLOT = 20;

    private static final int RAIL_BLOCK_SLOT = 7;
    private static final int BLOCK_BELOW_SLOT = 16;
    private static final int SLEEPER_BLOCK_SLOT = 25;
    private static final int ICON_SLOT = 23;
    private static final int OVERHEAD_SETTINGS_SLOT = 32;
    private static final int TRACK_SWITCHES_SLOT = 30;

    private static final int BACK_ITEM_SLOT = 36;
    private static final int SAVE_ITEM_SLOT = 44;

    private final RailTypeDraft draft;

    RailTypeEditorMenu(Player player, RailTypeDraft draft, boolean autoLoad) {
        // The parent constructor renders immediately, so load manually after the draft is set.
        super(5, EDITOR_INV_NAME, player, false);

        this.draft = draft;

        if (autoLoad)
            reloadMenuAsync();
    }

    @Override
    protected void setPreviewItems() {
        if (getMask() != null)
            getMask().apply(getMenu());

        getMenu().getSlot(NAME_SLOT).setItem(Item.create(
                Objects.requireNonNull(XMaterial.NAME_TAG.get()),
                yellow("Rail Type Name: ") + white(getDisplayName()),
                List.of(gray("Click to change the display name."))
        ));

        createCounter(HeadColor.WHITE, TRACK_COUNT_SLOT, "Track Count", draft.getTrackCount(),
                RailType.MIN_TRACK_COUNT, RailType.MAX_TRACK_COUNT, "Tracks");
        getMenu().getSlot(TRACK_SPACING_SLOT - 1).setItem(HeadFactory.getCounterMinusItem(
                HeadColor.LIGHT_GRAY, "Track Spacing", draft.getTrackSpacing(), RailType.MIN_TRACK_SPACING));
        getMenu().getSlot(TRACK_SPACING_SLOT + 1).setItem(HeadFactory.getCounterPlusItem(
                HeadColor.LIGHT_GRAY, "Track Spacing", draft.getTrackSpacing(), RailType.MAX_TRACK_SPACING));
        getMenu().getSlot(TRACK_SPACING_SLOT).setItem(Item.create(
                Objects.requireNonNull(XMaterial.NAME_TAG.get()),
                yellow("Track Spacings"),
                List.of(
                        gray("Between tracks: ") + white(draft.getTrackSpacings().toString()),
                        gray("Use +/- to set all spacings."),
                        gray("Click to configure each gap separately.")
                )
        ));
        createCounter(HeadColor.WHITE, SLEEPER_SPACING_SLOT, "Sleeper Spacing", draft.getSleeperSpacing(),
                RailType.MIN_SLEEPER_SPACING, RailType.MAX_SLEEPER_SPACING, "Blocks");

        getMenu().getSlot(RAIL_BLOCK_SLOT).setItem(createBlockItem(
                "Rail Block",
                draft.getRailBlocks(),
                "The block the rails are made of."
        ));
        getMenu().getSlot(BLOCK_BELOW_SLOT).setItem(createBlockItem(
                "Blocks Below the Rails",
                draft.getBlocksBelow(),
                "The material mix placed between and below the rails."
        ));
        getMenu().getSlot(SLEEPER_BLOCK_SLOT).setItem(createBlockItem(
                "Sleeper Block",
                draft.getSleeperBlocks(),
                "Sleeper Spacing 0 disables sleepers."
        ));
        getMenu().getSlot(ICON_SLOT).setItem(createBlockItem(
                "Menu Icon",
                draft.getIcon(),
                "The icon shown in the rail type menu."
        ));
        getMenu().getSlot(OVERHEAD_SETTINGS_SLOT).setItem(Item.create(
                Objects.requireNonNull(XMaterial.IRON_BARS.get()),
                yellow("Overhead Settings"),
                List.of(
                        gray("Poles: ") + white(draft.isOverheadPolesEnabled()
                                ? RailMenuText.ENABLED
                                : RailMenuText.DISABLED),
                        gray("Wires: ") + white(draft.isOverheadWiresEnabled()
                                ? RailMenuText.ENABLED
                                : RailMenuText.DISABLED),
                        gray("Click to configure overhead wires.")
                )
        ));
        getMenu().getSlot(TRACK_SWITCHES_SLOT).setItem(Item.create(
                Objects.requireNonNull(XMaterial.LEVER.get()),
                yellow("Track Switches: ")
                        + (draft.isTrackSwitchesEnabled()
                        ? green(RailMenuText.ENABLED)
                        : red(RailMenuText.DISABLED)),
                List.of(
                        gray("Prepares this type for future switch generation."),
                        gray("Click to toggle.")
                )
        ));

        setBackItem(BACK_ITEM_SLOT, new RailTypeMenu(getMenuPlayer(), false));
        getMenu().getSlot(SAVE_ITEM_SLOT).setItem(HeadFactory.head(
                HeadTexture.CHECKMARK,
                green(draft.getIdentifier() == null ? "Save Rail Type" : "Update Rail Type")
        ));

        getMenu().open(getMenuPlayer());
    }

    @Override
    protected void setMenuItemsAsync() {
        // All editor items are rendered synchronously because they depend only on the local draft state.
    }

    @Override
    protected void setItemClickEventsAsync() {
        getMenu().getSlot(NAME_SLOT).setClickHandler((clickPlayer, clickInformation) -> openNameEditor(clickPlayer));

        setCounterClickEvents(TRACK_COUNT_SLOT, RailType.MIN_TRACK_COUNT, RailType.MAX_TRACK_COUNT,
                draft::getTrackCount, draft::setTrackCount);
        setCounterClickEvents(TRACK_SPACING_SLOT, RailType.MIN_TRACK_SPACING, RailType.MAX_TRACK_SPACING,
                draft::getTrackSpacing, draft::setTrackSpacing);
        getMenu().getSlot(TRACK_SPACING_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
            new RailTrackSpacingMenu(clickPlayer, draft, true);
        });
        setCounterClickEvents(SLEEPER_SPACING_SLOT, RailType.MIN_SLEEPER_SPACING, RailType.MAX_SLEEPER_SPACING,
                draft::getSleeperSpacing, draft::setSleeperSpacing);

        setBlockPickerClickEvents(RAIL_BLOCK_SLOT, RailBlockRole.RAIL_BLOCK);
        setBlockPickerClickEvents(BLOCK_BELOW_SLOT, RailBlockRole.BLOCK_BELOW);
        setBlockPickerClickEvents(SLEEPER_BLOCK_SLOT, RailBlockRole.SLEEPER_BLOCK);
        setBlockPickerClickEvents(ICON_SLOT, RailBlockRole.ICON);
        getMenu().getSlot(TRACK_SWITCHES_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            draft.setTrackSwitchesEnabled(!draft.isTrackSwitchesEnabled());
            playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
            reloadMenuAsync();
        });
        getMenu().getSlot(OVERHEAD_SETTINGS_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
            new RailOverheadWireMenu(clickPlayer, draft, true);
        });

        getMenu().getSlot(SAVE_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> saveRailType(clickPlayer));
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

    private void openNameEditor(Player clickPlayer) {
        playSound(clickPlayer, Sound.UI_BUTTON_CLICK);

        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT)
                        return List.of();

                    String displayName = stateSnapshot.getText().trim();

                    if (displayName.isEmpty()) {
                        stateSnapshot.getPlayer().sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                                "Rail type name cannot be empty."
                        )));
                        return List.of(AnvilGUI.ResponseAction.replaceInputText(getDisplayName()));
                    }

                    draft.setDisplayName(displayName);
                    playSound(stateSnapshot.getPlayer(), Sound.UI_BUTTON_CLICK);
                    stateSnapshot.getPlayer().sendMessage(ChatHelper.getStandardComponent(
                            true,
                            "Rail type name set to '%s'.",
                            displayName
                    ));
                    return List.of(
                            AnvilGUI.ResponseAction.close(),
                            AnvilGUI.ResponseAction.run(() -> new RailTypeEditorMenu(clickPlayer, draft, true))
                    );
                })
                .text(getDisplayName())
                .itemLeft(Item.create(Objects.requireNonNull(XMaterial.NAME_TAG.get()), yellow("Change Rail Type Name")))
                .title(darkGray("Change rail type name"))
                .plugin(BuildTeamTools.getInstance())
                .open(clickPlayer);
    }

    private void saveRailType(Player clickPlayer) {
        String permission = draft.getIdentifier() == null ? Permissions.RAIL_TYPE_CREATE : Permissions.RAIL_TYPE_EDIT;

        if (!Permissions.checkPermission(clickPlayer, permission))
            return;

        Rail rail = GeneratorModule.getInstance().getRail();

        if (rail == null)
            return;

        RailTypeManager railTypeManager = rail.getRailTypeManager();
        String identifier = draft.getIdentifier() == null ? railTypeManager.getNextCustomIdentifier() : draft.getIdentifier();
        String displayName = draft.getDisplayName() == null
                ? railTypeManager.getNextCustomDisplayName()
                : draft.getDisplayName();

        RailType railType = RailType.createCustom(new RailType.Configuration()
                .identifier(identifier)
                .displayName(displayName)
                .icon(draft.getIcon())
                .railBlocks(draft.getRailBlocks())
                .blocksBelow(draft.getBlocksBelow())
                .sleeperBlocks(draft.getSleeperBlocks())
                .sleeperSpacing(draft.getSleeperSpacing())
                .trackCount(draft.getTrackCount())
                .trackSpacing(draft.getTrackSpacing())
                .trackSpacings(draft.getTrackSpacings())
                .overheadPolesEnabled(draft.isOverheadPolesEnabled())
                .overheadPoleBlocks(draft.getOverheadPoleBlocks())
                .overheadSupportBlocks(draft.getOverheadSupportBlocks())
                .overheadPoleSpacing(draft.getOverheadPoleSpacing())
                .overheadPoleOffset(draft.getOverheadPoleOffset())
                .overheadPoleHeight(draft.getOverheadPoleHeight())
                .overheadWiresEnabled(draft.isOverheadWiresEnabled())
                .overheadWireBlocks(draft.getOverheadWireBlocks())
                .trackSwitchesEnabled(draft.isTrackSwitchesEnabled()));

        String error = railTypeManager.saveRailType(railType);

        if (error != null) {
            clickPlayer.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(error)));
            playSound(clickPlayer, Sound.ENTITY_ITEM_BREAK);
            return;
        }

        Settings settings = rail.getPlayerSettings().get(clickPlayer.getUniqueId());

        if (settings instanceof RailSettings railSettings)
            railSettings.setValue(RailFlag.RAIL_TYPE, railType);

        clickPlayer.sendMessage(ChatHelper.getStandardComponent(
                true,
                "Saved rail type %s as '%s'.",
                displayName,
                identifier
        ));
        playSound(clickPlayer, Sound.UI_BUTTON_CLICK);

        new RailTypeMenu(clickPlayer, true);
    }

    private void setBlockPickerClickEvents(int slot, RailBlockRole role) {
        getMenu().getSlot(slot).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            playSound(clickPlayer, Sound.UI_BUTTON_CLICK);

            new RailBlockPickerMenu(clickPlayer, draft, role, true);
        });
    }

    private void setCounterClickEvents(int slot, int minValue, int maxValue, IntSupplier getter, IntConsumer setter) {
        getMenu().getSlot(slot - 1).setClickHandler((clickPlayer, clickInformation) -> {
            if (getter.getAsInt() <= minValue) {
                playSound(clickPlayer, Sound.ENTITY_ITEM_BREAK);
                return;
            }

            setter.accept(getter.getAsInt() - 1);
            playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
            reloadMenuAsync();
        });

        getMenu().getSlot(slot + 1).setClickHandler((clickPlayer, clickInformation) -> {
            if (getter.getAsInt() >= maxValue) {
                playSound(clickPlayer, Sound.ENTITY_ITEM_BREAK);
                return;
            }

            setter.accept(getter.getAsInt() + 1);
            playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
            reloadMenuAsync();
        });
    }

    private ItemStack createBlockItem(String name, List<XMaterial> materials, String description) {
        List<String> lore = new java.util.ArrayList<>();
        lore.add(gray(description));
        for (XMaterial material : materials)
            lore.add(white(RailTypeMenu.formatMaterial(material)));
        lore.add(gray("Click to choose one or more materials."));
        Material icon = materials.getFirst().get();
        return Item.create(icon == null ? Material.BARRIER : icon, yellow(name), lore);
    }
    private ItemStack createBlockItem(String name, XMaterial material, String description) {
        Material bukkitMaterial = material.get();

        if (bukkitMaterial == null)
            bukkitMaterial = Objects.requireNonNull(XMaterial.BARRIER.get());

        return Item.create(
                bukkitMaterial,
                yellow(name + ": ") + white(RailTypeMenu.formatMaterial(material)),
                List.of(gray(description), gray("Click to choose a different block."))
        );
    }

    private String getDisplayName() {
        if (draft.getDisplayName() != null)
            return draft.getDisplayName();

        return draft.getIdentifier() == null ? "Custom Rail" : draft.getIdentifier();
    }

    private void playSound(Player player, Sound sound) {
        player.playSound(player, sound, 1.0F, 1.0F);
    }

    private static String darkGray(String text) {
        return RailMenuText.color(NamedTextColor.DARK_GRAY, text);
    }

    private static String gray(String text) {
        return RailMenuText.color(NamedTextColor.GRAY, text);
    }

    private static String white(String text) {
        return RailMenuText.color(NamedTextColor.WHITE, text);
    }

    private static String yellow(String text) {
        return RailMenuText.color(NamedTextColor.YELLOW, text);
    }

    private static String green(String text) {
        return RailMenuText.color(NamedTextColor.GREEN, text);
    }

    private static String red(String text) {
        return RailMenuText.color(NamedTextColor.RED, text);
    }
}
