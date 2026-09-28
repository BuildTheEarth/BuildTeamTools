package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
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

    private static final String MENU_TITLE = "Overhead Wire Settings";
    private static final String FILLED_MASK_ROW = "111111111";
    private static final String BLOCKS_UNIT = "Blocks";

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
        super(5, MENU_TITLE, player, false);
        this.draft = draft;

        if (autoLoad)
            reloadMenuAsync();
    }

    @Override
    protected void setPreviewItems() {
        if (getMask() != null)
            getMask().apply(getMenu());

        getMenu().getSlot(POLES_TOGGLE_SLOT).setItem(createToggleItem(
                "Overhead Poles",
                draft.isOverheadPolesEnabled(),
                "Generate poles alongside the tracks."
        ));
        getMenu().getSlot(WIRES_TOGGLE_SLOT).setItem(createToggleItem(
                "Overhead Wires",
                draft.isOverheadWiresEnabled(),
                "Generate wires above the tracks."
        ));
        getMenu().getSlot(POLE_BLOCK_SLOT).setItem(createBlockItem(
                "Pole Block",
                draft.getOverheadPoleBlocks()
        ));
        getMenu().getSlot(SUPPORT_BLOCK_SLOT).setItem(createBlockItem(
                "Top Support Block",
                draft.getOverheadSupportBlocks()
        ));
        getMenu().getSlot(WIRE_BLOCK_SLOT).setItem(createBlockItem(
                "Wire Block",
                draft.getOverheadWireBlocks()
        ));

        getMenu().getSlot(POLE_SPACING_SLOT).setItem(createSpacingItem());
        createCounter(
                HeadColor.LIGHT_GRAY,
                POLE_OFFSET_SLOT,
                "Pole Offset",
                draft.getOverheadPoleOffset(),
                RailType.MIN_OVERHEAD_POLE_OFFSET,
                RailType.MAX_OVERHEAD_POLE_OFFSET,
                BLOCKS_UNIT
        );
        createCounter(
                HeadColor.WHITE,
                POLE_HEIGHT_SLOT,
                "Pole Height",
                draft.getOverheadPoleHeight(),
                RailType.MIN_OVERHEAD_POLE_HEIGHT,
                RailType.MAX_OVERHEAD_POLE_HEIGHT,
                BLOCKS_UNIT
        );

        setBackItem(BACK_ITEM_SLOT, new RailTypeEditorMenu(getMenuPlayer(), draft, false));
        getMenu().getSlot(DONE_ITEM_SLOT).setItem(HeadFactory.head(
                HeadTexture.CHECKMARK,
                green("Done")
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
        return Item.create(
                Objects.requireNonNull(XMaterial.LEVER.get()),
                yellow(name + ": ") + (enabled
                        ? green(RailMenuText.ENABLED)
                        : red(RailMenuText.DISABLED)),
                List.of(gray(description), gray("Click to toggle."))
        );
    }

    private ItemStack createBlockItem(String name, List<XMaterial> materials) {
        Material bukkitMaterial = materials.getFirst().get();

        if (bukkitMaterial == null)
            bukkitMaterial = Objects.requireNonNull(XMaterial.BARRIER.get());

        return Item.create(
                bukkitMaterial,
                yellow(name + ": ") + RailTypeMenu.formatMaterials(materials),
                List.of(gray("Click to choose one or more materials."))
        );
    }

    private ItemStack createSpacingItem() {
        return Item.create(
                Objects.requireNonNull(XMaterial.NAME_TAG.get()),
                yellow("Pole Spacing: ") + white(draft.getOverheadPoleSpacing() + " " + BLOCKS_UNIT),
                List.of(
                        gray("Click to enter a custom spacing."),
                        gray("Valid range: ") + white(RailType.MIN_OVERHEAD_POLE_SPACING
                                + "-" + RailType.MAX_OVERHEAD_POLE_SPACING + " " + BLOCKS_UNIT)
                )
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
                        stateSnapshot.getPlayer().sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                                "Pole spacing must be a number between %s and %s.",
                                RailType.MIN_OVERHEAD_POLE_SPACING,
                                RailType.MAX_OVERHEAD_POLE_SPACING
                        )));
                        return List.of(AnvilGUI.ResponseAction.replaceInputText(String.valueOf(draft.getOverheadPoleSpacing())));
                    }

                    draft.setOverheadPoleSpacing(spacing);
                    playSound(stateSnapshot.getPlayer(), Sound.UI_BUTTON_CLICK);
                    stateSnapshot.getPlayer().sendMessage(ChatHelper.getStandardComponent(
                            true,
                            "Pole spacing set to %s blocks.",
                            spacing
                    ));
                    return List.of(
                            AnvilGUI.ResponseAction.close(),
                            AnvilGUI.ResponseAction.run(() -> new RailOverheadWireMenu(clickPlayer, draft, true))
                    );
                })
                .text(String.valueOf(draft.getOverheadPoleSpacing()))
                .itemLeft(Item.create(Objects.requireNonNull(XMaterial.NAME_TAG.get()), yellow("Change Pole Spacing")))
                .title(darkGray("Change pole spacing"))
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

    private static String gray(String text) {
        return RailMenuText.color(NamedTextColor.GRAY, text);
    }

    private static String darkGray(String text) {
        return RailMenuText.color(NamedTextColor.DARK_GRAY, text);
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
