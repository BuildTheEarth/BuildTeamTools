package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
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
import net.kyori.adventure.text.Component;
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
        super(5, Messages.component(MessageKeys.Generator.Rail.Menu.EDITOR_TITLE), player, false);

        this.draft = draft;

        if (autoLoad)
            reloadMenuAsync();
    }

    @Override
    protected void setPreviewItems() {
        if (getMask() != null)
            getMask().apply(getMenu());

        Player player = getMenuPlayer();
        String enabled = Messages.plain(player, MessageKeys.Generator.Rail.Menu.ENABLED);
        String disabled = Messages.plain(player, MessageKeys.Generator.Rail.Menu.DISABLED);

        getMenu().getSlot(NAME_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.NAME_TAG.get()),
                Messages.render(player, MessageKeys.Generator.Rail.Menu.EDITOR_NAME_LABEL, Component.text(getDisplayName())),
                Messages.loreComponents(player, MessageKeys.Generator.Rail.Menu.EDITOR_NAME_LORE)
        ));

        createCounter(HeadColor.WHITE, TRACK_COUNT_SLOT,
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.EDITOR_TRACK_COUNT),
                draft.getTrackCount(),
                RailType.MIN_TRACK_COUNT, RailType.MAX_TRACK_COUNT,
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.EDITOR_UNIT_TRACKS));
        Component trackSpacingName = Component.text(
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.EDITOR_TRACK_SPACING));
        getMenu().getSlot(TRACK_SPACING_SLOT - 1).setItem(HeadFactory.getCounterMinusItem(
                HeadColor.LIGHT_GRAY,
                Messages.render(player, Messages.component(MessageKeys.Ui.COUNTER_MINUS, trackSpacingName)),
                draft.getTrackSpacing(),
                RailType.MIN_TRACK_SPACING));
        getMenu().getSlot(TRACK_SPACING_SLOT + 1).setItem(HeadFactory.getCounterPlusItem(
                HeadColor.LIGHT_GRAY,
                Messages.render(player, Messages.component(MessageKeys.Ui.COUNTER_PLUS, trackSpacingName)),
                draft.getTrackSpacing(),
                RailType.MAX_TRACK_SPACING));
        getMenu().getSlot(TRACK_SPACING_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.NAME_TAG.get()),
                Messages.render(player, MessageKeys.Generator.Rail.Menu.EDITOR_TRACK_SPACINGS),
                Messages.loreComponents(
                        player,
                        MessageKeys.Generator.Rail.Menu.EDITOR_TRACK_SPACINGS_LORE,
                        Component.text(draft.getTrackSpacings().toString()))
        ));
        createCounter(HeadColor.WHITE, SLEEPER_SPACING_SLOT,
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.EDITOR_SLEEPER_SPACING),
                draft.getSleeperSpacing(),
                RailType.MIN_SLEEPER_SPACING, RailType.MAX_SLEEPER_SPACING,
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.EDITOR_UNIT_BLOCKS));

        getMenu().getSlot(RAIL_BLOCK_SLOT).setItem(createBlockItem(
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.EDITOR_RAIL_BLOCK),
                draft.getRailBlocks(),
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.EDITOR_RAIL_BLOCK_DESC)
        ));
        getMenu().getSlot(BLOCK_BELOW_SLOT).setItem(createBlockItem(
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.EDITOR_BLOCKS_BELOW),
                draft.getBlocksBelow(),
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.EDITOR_BLOCKS_BELOW_DESC)
        ));
        getMenu().getSlot(SLEEPER_BLOCK_SLOT).setItem(createBlockItem(
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.EDITOR_SLEEPER_BLOCK),
                draft.getSleeperBlocks(),
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.EDITOR_SLEEPER_BLOCK_DESC)
        ));
        getMenu().getSlot(ICON_SLOT).setItem(createBlockItem(
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.EDITOR_MENU_ICON),
                draft.getIcon(),
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.EDITOR_MENU_ICON_DESC)
        ));

        java.util.List<Component> overheadLore = new java.util.ArrayList<>();
        overheadLore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.EDITOR_OVERHEAD_POLES_LINE,
                Component.text(draft.isOverheadPolesEnabled() ? enabled : disabled)));
        overheadLore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.EDITOR_OVERHEAD_WIRES_LINE,
                Component.text(draft.isOverheadWiresEnabled() ? enabled : disabled)));
        overheadLore.addAll(Messages.loreComponents(player, MessageKeys.Generator.Rail.Menu.EDITOR_OVERHEAD_LORE));
        getMenu().getSlot(OVERHEAD_SETTINGS_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.IRON_BARS.get()),
                Messages.render(player, MessageKeys.Generator.Rail.Menu.EDITOR_OVERHEAD),
                overheadLore
        ));

        Component switchesState = draft.isTrackSwitchesEnabled()
                ? Component.text(enabled, NamedTextColor.GREEN)
                : Component.text(disabled, NamedTextColor.RED);
        getMenu().getSlot(TRACK_SWITCHES_SLOT).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.LEVER.get()),
                Messages.render(player, MessageKeys.Generator.Rail.Menu.EDITOR_TRACK_SWITCHES, switchesState),
                Messages.loreComponents(player, MessageKeys.Generator.Rail.Menu.EDITOR_TRACK_SWITCHES_LORE)
        ));

        setBackItem(BACK_ITEM_SLOT, new RailTypeMenu(getMenuPlayer(), false));
        getMenu().getSlot(SAVE_ITEM_SLOT).setItem(HeadFactory.head(
                HeadTexture.CHECKMARK,
                Messages.render(player, draft.getIdentifier() == null
                        ? MessageKeys.Generator.Rail.Menu.EDITOR_SAVE
                        : MessageKeys.Generator.Rail.Menu.EDITOR_UPDATE)
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
                        stateSnapshot.getPlayer().sendMessage(Messages.component(MessageKeys.Generator.Rail.Menu.EDITOR_NAME_EMPTY));
                        return List.of(AnvilGUI.ResponseAction.replaceInputText(getDisplayName()));
                    }

                    draft.setDisplayName(displayName);
                    playSound(stateSnapshot.getPlayer(), Sound.UI_BUTTON_CLICK);
                    stateSnapshot.getPlayer().sendMessage(Messages.component(MessageKeys.Generator.Rail.Menu.EDITOR_NAME_SET, Component.text(displayName)));
                    return List.of(
                            AnvilGUI.ResponseAction.close(),
                            AnvilGUI.ResponseAction.run(() -> new RailTypeEditorMenu(clickPlayer, draft, true))
                    );
                })
                .text(getDisplayName())
                .itemLeft(ItemComponents.of(
                        Objects.requireNonNull(XMaterial.NAME_TAG.get()),
                        Messages.render(clickPlayer, MessageKeys.Generator.Rail.Menu.EDITOR_ANVIL_ITEM)))
                .title(Messages.legacy(clickPlayer, MessageKeys.Generator.Rail.Menu.EDITOR_ANVIL_TITLE))
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
            clickPlayer.sendMessage(Messages.component(MessageKeys.Generator.Rail.Menu.VALIDATION_ERROR, Component.text(error)));
            playSound(clickPlayer, Sound.ENTITY_ITEM_BREAK);
            return;
        }

        Settings settings = rail.getPlayerSettings().get(clickPlayer.getUniqueId());

        if (settings instanceof RailSettings railSettings)
            railSettings.setValue(RailFlag.RAIL_TYPE, railType);

        clickPlayer.sendMessage(Messages.component(
                MessageKeys.Generator.Rail.Menu.EDITOR_SAVED,
                Component.text(railType.getDisplayName()),
                Component.text(railType.getIdentifier())));
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
        Player player = getMenuPlayer();
        List<Component> lore = new java.util.ArrayList<>();
        lore.add(gray(description));
        for (XMaterial material : materials)
            lore.add(white(RailTypeMenu.formatMaterial(material)));
        lore.add(Messages.render(player, MessageKeys.Generator.Rail.Menu.EDITOR_CLICK_MATERIALS));
        Material icon = materials.getFirst().get();
        return ItemComponents.of(icon == null ? Material.BARRIER : icon, yellow(name), lore);
    }

    private ItemStack createBlockItem(String name, XMaterial material, String description) {
        Player player = getMenuPlayer();
        Material bukkitMaterial = material.get();

        if (bukkitMaterial == null)
            bukkitMaterial = Objects.requireNonNull(XMaterial.BARRIER.get());

        Component displayName = yellow(name + ": ").append(white(RailTypeMenu.formatMaterial(material)));
        return ItemComponents.of(
                bukkitMaterial,
                displayName,
                List.of(
                        gray(description),
                        Messages.render(player, MessageKeys.Generator.Rail.Menu.EDITOR_CLICK_BLOCK))
        );
    }

    private String getDisplayName() {
        if (draft.getDisplayName() != null)
            return draft.getDisplayName();

        return draft.getIdentifier() == null
                ? Messages.plain(getMenuPlayer(), MessageKeys.Generator.Rail.Menu.EDITOR_DEFAULT_NAME)
                : draft.getIdentifier();
    }

    private void playSound(Player player, Sound sound) {
        player.playSound(player, sound, 1.0F, 1.0F);
    }

    private static Component gray(String text) {
        return RailMenuText.colored(NamedTextColor.GRAY, text);
    }

    private static Component white(String text) {
        return RailMenuText.colored(NamedTextColor.WHITE, text);
    }

    private static Component yellow(String text) {
        return RailMenuText.colored(NamedTextColor.YELLOW, text);
    }
}
