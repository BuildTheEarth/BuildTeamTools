package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.kyori.adventure.text.Component;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.configuration.RailType;
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

final class RailOverheadWireMenu extends AbstractMenu {
    private static final String FILLED_MASK_ROW = "111111111";

    private static final int POLES_TOGGLE_SLOT = 10;
    private static final int POLE_BLOCK_SLOT = 16;
    private static final int SUPPORT_BLOCK_SLOT = 22;
    private static final int WIRES_TOGGLE_SLOT = 19;
    private static final int WIRE_BLOCK_SLOT = 25;
    private static final int POLE_SPACING_SLOT = 28;
    private static final int POLE_OFFSET_SLOT = 31;
    private static final int POLE_HEIGHT_SLOT = 34;
    private static final int BACK_ITEM_SLOT = 36;
    private static final int DONE_ITEM_SLOT = 44;

    private final RailTypeDraft draft;

    RailOverheadWireMenu(Player player, RailTypeDraft draft, boolean autoLoad) {
        super(5, Messages.component(MessageKeys.Generator.Rail.Menu.OVERHEAD_TITLE), player, false);
        this.draft = draft;

        if (autoLoad)
            reloadMenuAsync();
    }

    @Override
    protected void setPreviewItems() {
        if (getMask() != null)
            getMask().apply(getMenu());

        Player player = getMenuPlayer();
        getMenu().getSlot(POLES_TOGGLE_SLOT).setItem(createToggleItem(
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.OVERHEAD_POLES),
                draft.isOverheadPolesEnabled(),
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.OVERHEAD_POLES_DESC)
        ));
        getMenu().getSlot(WIRES_TOGGLE_SLOT).setItem(createToggleItem(
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.OVERHEAD_WIRES),
                draft.isOverheadWiresEnabled(),
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.OVERHEAD_WIRES_DESC)
        ));
        getMenu().getSlot(POLE_BLOCK_SLOT).setItem(createBlockItem(
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.OVERHEAD_POLE_BLOCK),
                draft.getOverheadPoleBlocks()
        ));
        getMenu().getSlot(SUPPORT_BLOCK_SLOT).setItem(createBlockItem(
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.OVERHEAD_SUPPORT_BLOCK),
                draft.getOverheadSupportBlocks()
        ));
        getMenu().getSlot(WIRE_BLOCK_SLOT).setItem(createBlockItem(
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.OVERHEAD_WIRE_BLOCK),
                draft.getOverheadWireBlocks()
        ));

        getMenu().getSlot(POLE_SPACING_SLOT).setItem(createSpacingItem());
        String blocksUnit = Messages.plain(player, MessageKeys.Generator.Rail.Menu.EDITOR_UNIT_BLOCKS);
        createCounter(
                HeadColor.LIGHT_GRAY,
                POLE_OFFSET_SLOT,
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.OVERHEAD_POLE_OFFSET),
                draft.getOverheadPoleOffset(),
                RailType.MIN_OVERHEAD_POLE_OFFSET,
                RailType.MAX_OVERHEAD_POLE_OFFSET,
                blocksUnit
        );
        createCounter(
                HeadColor.WHITE,
                POLE_HEIGHT_SLOT,
                Messages.plain(player, MessageKeys.Generator.Rail.Menu.OVERHEAD_POLE_HEIGHT),
                draft.getOverheadPoleHeight(),
                RailType.MIN_OVERHEAD_POLE_HEIGHT,
                RailType.MAX_OVERHEAD_POLE_HEIGHT,
                blocksUnit
        );

        setBackItem(BACK_ITEM_SLOT, new RailTypeEditorMenu(getMenuPlayer(), draft, false));
        getMenu().getSlot(DONE_ITEM_SLOT).setItem(HeadFactory.head(
                HeadTexture.CHECKMARK,
                Messages.render(player, MessageKeys.Generator.Rail.Menu.TRACK_SPACING_DONE)
        ));
        getMenu().open(getMenuPlayer());
    }

    @Override
    protected void setMenuItemsAsync() {
        // All items use local draft state.
    }

    @Override
    protected void setItemClickEventsAsync() {
        getMenu().getSlot(POLES_TOGGLE_SLOT).setClickHandler((player, click) -> {
            draft.setOverheadPolesEnabled(!draft.isOverheadPolesEnabled());
            playSound(player, Sound.UI_BUTTON_CLICK);
            reloadMenuAsync();
        });
        getMenu().getSlot(WIRES_TOGGLE_SLOT).setClickHandler((player, click) -> {
            draft.setOverheadWiresEnabled(!draft.isOverheadWiresEnabled());
            playSound(player, Sound.UI_BUTTON_CLICK);
            reloadMenuAsync();
        });

        setBlockPickerClickEvent(POLE_BLOCK_SLOT, RailBlockRole.OVERHEAD_POLE_BLOCK);
        setBlockPickerClickEvent(SUPPORT_BLOCK_SLOT, RailBlockRole.OVERHEAD_SUPPORT_BLOCK);
        setBlockPickerClickEvent(WIRE_BLOCK_SLOT, RailBlockRole.OVERHEAD_WIRE_BLOCK);
        getMenu().getSlot(POLE_SPACING_SLOT).setClickHandler((player, click) -> openSpacingEditor(player));
        setCounterClickEvents(
                POLE_OFFSET_SLOT,
                RailType.MIN_OVERHEAD_POLE_OFFSET,
                RailType.MAX_OVERHEAD_POLE_OFFSET,
                draft::getOverheadPoleOffset,
                draft::setOverheadPoleOffset
        );
        setCounterClickEvents(
                POLE_HEIGHT_SLOT,
                RailType.MIN_OVERHEAD_POLE_HEIGHT,
                RailType.MAX_OVERHEAD_POLE_HEIGHT,
                draft::getOverheadPoleHeight,
                draft::setOverheadPoleHeight
        );

        getMenu().getSlot(DONE_ITEM_SLOT).setClickHandler((player, click) -> {
            player.closeInventory();
            playSound(player, Sound.UI_BUTTON_CLICK);
            new RailTypeEditorMenu(player, draft, true);
        });
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

    private void setBlockPickerClickEvent(int slot, RailBlockRole role) {
        getMenu().getSlot(slot).setClickHandler((player, click) -> {
            player.closeInventory();
            playSound(player, Sound.UI_BUTTON_CLICK);
            new RailBlockPickerMenu(player, draft, role, true);
        });
    }

    private void setCounterClickEvents(
            int slot,
            int minimum,
            int maximum,
            IntSupplier getter,
            IntConsumer setter
    ) {
        getMenu().getSlot(slot - 1).setClickHandler((player, click) ->
                changeCounter(player, minimum, getter.getAsInt() - 1, getter, setter));
        getMenu().getSlot(slot + 1).setClickHandler((player, click) ->
                changeCounter(player, maximum, getter.getAsInt() + 1, getter, setter));
    }

    private void changeCounter(
            Player player,
            int boundary,
            int newValue,
            IntSupplier getter,
            IntConsumer setter
    ) {
        if (getter.getAsInt() == boundary) {
            playSound(player, Sound.ENTITY_ITEM_BREAK);
            return;
        }

        setter.accept(newValue);
        playSound(player, Sound.UI_BUTTON_CLICK);
        reloadMenuAsync();
    }

    private ItemStack createToggleItem(String name, boolean enabled, String description) {
        Player player = getMenuPlayer();
        Component state = enabled
                ? Component.text(RailMenuText.enabled(player), NamedTextColor.GREEN)
                : Component.text(RailMenuText.disabled(player), NamedTextColor.RED);
        Component displayName = yellow(name + ": ").append(state);
        return ItemComponents.of(
                Objects.requireNonNull(XMaterial.LEVER.get()),
                displayName,
                Messages.loreComponents(
                        player,
                        MessageKeys.Generator.Rail.Menu.OVERHEAD_TOGGLE_LORE,
                        Component.text(description))
        );
    }

    private ItemStack createBlockItem(String name, List<XMaterial> materials) {
        Player player = getMenuPlayer();
        Material bukkitMaterial = materials.getFirst().get();

        if (bukkitMaterial == null)
            bukkitMaterial = Objects.requireNonNull(XMaterial.BARRIER.get());

        Component displayName = yellow(name + ": ").append(RailTypeMenuItems.formatMaterialsComponent(materials));
        return ItemComponents.of(
                bukkitMaterial,
                displayName,
                Messages.loreComponents(player, MessageKeys.Generator.Rail.Menu.OVERHEAD_MATERIALS_LORE)
        );
    }

    private ItemStack createSpacingItem() {
        Player player = getMenuPlayer();
        String range = RailType.MIN_OVERHEAD_POLE_SPACING + "-" + RailType.MAX_OVERHEAD_POLE_SPACING;
        return ItemComponents.of(
                Objects.requireNonNull(XMaterial.NAME_TAG.get()),
                Messages.render(
                        player,
                        MessageKeys.Generator.Rail.Menu.OVERHEAD_POLE_SPACING,
                        Component.text(String.valueOf(draft.getOverheadPoleSpacing()))),
                Messages.loreComponents(
                        player,
                        MessageKeys.Generator.Rail.Menu.OVERHEAD_POLE_SPACING_LORE,
                        Component.text(range))
        );
    }

    private void openSpacingEditor(Player clickPlayer) {
        playSound(clickPlayer, Sound.UI_BUTTON_CLICK);

        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT)
                        return List.of();

                    String input = stateSnapshot.getText().trim();
                    Integer spacing = parseSpacing(input);

                    if (spacing == null) {
                        stateSnapshot.getPlayer().sendMessage(Messages.component(MessageKeys.Generator.Rail.Menu.OVERHEAD_SPACING_INVALID, Component.text(String.valueOf(RailType.MIN_OVERHEAD_POLE_SPACING)), Component.text(String.valueOf(RailType.MAX_OVERHEAD_POLE_SPACING))));
                        return List.of(AnvilGUI.ResponseAction.replaceInputText(String.valueOf(draft.getOverheadPoleSpacing())));
                    }

                    draft.setOverheadPoleSpacing(spacing);
                    playSound(stateSnapshot.getPlayer(), Sound.UI_BUTTON_CLICK);
                    stateSnapshot.getPlayer().sendMessage(Messages.component(MessageKeys.Generator.Rail.Menu.OVERHEAD_SPACING_SET, Component.text(String.valueOf(spacing))));
                    return List.of(
                            AnvilGUI.ResponseAction.close(),
                            AnvilGUI.ResponseAction.run(() -> new RailOverheadWireMenu(clickPlayer, draft, true))
                    );
                })
                .text(String.valueOf(draft.getOverheadPoleSpacing()))
                .itemLeft(ItemComponents.of(Objects.requireNonNull(XMaterial.NAME_TAG.get()),
                        Messages.render(clickPlayer, MessageKeys.Generator.Rail.Menu.OVERHEAD_ANVIL_ITEM)))
                .title(Messages.legacy(clickPlayer, MessageKeys.Generator.Rail.Menu.OVERHEAD_ANVIL_TITLE))
                .plugin(BuildTeamTools.getInstance())
                .open(clickPlayer);
    }

    private Integer parseSpacing(String input) {
        try {
            int spacing = Integer.parseInt(input);

            if (spacing < RailType.MIN_OVERHEAD_POLE_SPACING || spacing > RailType.MAX_OVERHEAD_POLE_SPACING)
                return null;

            return spacing;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private void playSound(Player player, Sound sound) {
        player.playSound(player, sound, 1.0F, 1.0F);
    }

    private static Component yellow(String text) {
        return RailMenuText.colored(NamedTextColor.YELLOW, text);
    }
}
