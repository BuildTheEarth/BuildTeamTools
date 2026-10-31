package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.buildtheearth.buildteamtools.utils.menus.BlockListMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Selects independent material palettes for every building role in a draft.
 * Cursor items are sampled without moving or consuming them.
 */
public class RailBlockPickerMenu extends BlockListMenu {

    private static final int CUSTOM_BLOCK_SLOT = 28;
    private static final int SELECTION_SLOT = 29;

    private final RailTypeDraft draft;
    private final RailBlockRole role;
    private final List<ItemStack> choices;

    RailBlockPickerMenu(Player player, RailTypeDraft draft, RailBlockRole role, boolean autoLoad) {
        this(player, draft, role, autoLoad, createChoices(player, draft, role));
    }

    private RailBlockPickerMenu(Player player, RailTypeDraft draft, RailBlockRole role,
                               boolean autoLoad, List<ItemStack> choices) {
        super(player, Messages.plain(player, role.getMenuTitleKey()), choices, createParentMenu(player, draft, role), false);

        this.draft = draft;
        this.role = role;
        this.choices = choices;
        selectedMaterials = new ArrayList<>(role.getSelected(draft).stream().map(XMaterial::name).toList());
        if (autoLoad)
            reloadMenuAsync();
    }

    private static List<ItemStack> createChoices(Player player, RailTypeDraft draft, RailBlockRole role) {
        Map<Material, ItemStack> choices = new LinkedHashMap<>();
        if (role == RailBlockRole.RAIL_BLOCK)
            for (Material material : List.of(Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL))
                choices.put(material, new ItemStack(material));
        for (XMaterial material : role.getSelected(draft)) {
            if (material.get() != null)
                choices.put(material.get(), new ItemStack(material.get()));
        }
        for (ItemStack item : player.getInventory().getContents()) {
            if (isValidItem(item, role))
                choices.putIfAbsent(item.getType(), new ItemStack(item.getType()));
        }
        for (ItemStack item : role.createChoices())
            choices.putIfAbsent(item.getType(), item);
        return new ArrayList<>(choices.values());
    }

    private static boolean isValidItem(ItemStack item, RailBlockRole role) {
        return item != null && !item.getType().isAir()
                && (role == RailBlockRole.ICON || item.getType().isBlock());
    }

    @Override
    protected void setPreviewItems() {
        super.setPreviewItems();
        Player player = getMenuPlayer();
        getMenu().getSlot(CUSTOM_BLOCK_SLOT).setItem(ItemComponents.of(
                Material.HOPPER,
                Messages.render(player, MessageKeys.Generator.Rail.Menu.PICKER_USE_OWN),
                Messages.loreComponents(player, MessageKeys.Generator.Rail.Menu.PICKER_USE_OWN_LORE)));
        getMenu().getSlot(SELECTION_SLOT).setItem(ItemComponents.of(
                Material.PAPER,
                Messages.render(player, MessageKeys.Generator.Rail.Menu.PICKER_SELECTED),
                Messages.loreComponents(
                        player,
                        MessageKeys.Generator.Rail.Menu.PICKER_SELECTED_LORE,
                        net.kyori.adventure.text.Component.text(String.join(", ", selectedMaterials)),
                        Messages.component(role != RailBlockRole.ICON
                                ? MessageKeys.Generator.Rail.Menu.PICKER_SELECTED_HINT_MULTI
                                : MessageKeys.Generator.Rail.Menu.PICKER_SELECTED_HINT_SINGLE))));
        getMenu().update(getMenuPlayer());
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(List<?> source) {
        List<ItemStack> itemStacks = source.stream().map(l -> (ItemStack) l).toList();
        int slot = 0;

        for (ItemStack ignored : itemStacks) {
            final int _slot = slot;
            getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
                String type = Item.getUppercaseMaterialString(getMenu().getSlot(_slot).getItem(getMenuPlayer()));

                if (selectedMaterials.contains(type)) {
                    selectedMaterials.removeIf(type::equals);
                } else {
                    if (role == RailBlockRole.ICON)
                        selectedMaterials.clear();
                    selectedMaterials.add(type);
                }

                reloadMenuAsync();
            });
            slot++;
        }
    }

    @Override
    protected void setItemClickEventsAsync() {
        super.setItemClickEventsAsync();

        getMenu().getSlot(CUSTOM_BLOCK_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickInformation.setResult(Event.Result.DENY);
            ItemStack item = clickPlayer.getItemOnCursor();
            if (!isValidItem(item, role)) {
                clickPlayer.sendMessage(Messages.component(role == RailBlockRole.ICON
                        ? MessageKeys.Generator.Rail.Menu.PICKER_NEED_ITEM
                        : MessageKeys.Generator.Rail.Menu.PICKER_NEED_BLOCK));
                return;
            }
            String material = XMaterial.matchXMaterial(item.getType()).name();
            if (role == RailBlockRole.ICON)
                selectedMaterials.clear();
            if (!selectedMaterials.contains(material))
                selectedMaterials.add(material);
            if (choices.stream().noneMatch(choice -> choice.getType() == item.getType()))
                choices.add(new ItemStack(item.getType()));
            // Finish the cancelled cursor transaction before redrawing the inventory.
            Bukkit.getScheduler().runTask(BuildTeamTools.getInstance(), () -> {
                if (getMenu().isOpen(clickPlayer))
                    reloadMenuAsync();
            });
        });

        getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            if (!canProceed())
                return;
            List<XMaterial> materials = selectedMaterials.stream().map(Item::convertStringToXMaterial).toList();

            if (materials.stream().anyMatch(Objects::isNull))
                return;

            role.apply(draft, materials);

            clickPlayer.closeInventory();
            playSound(clickPlayer, Sound.UI_BUTTON_CLICK);

            if (role.isOverheadSetting())
                new RailOverheadWireMenu(clickPlayer, draft, true);
            else
                new RailTypeEditorMenu(clickPlayer, draft, true);
        });
    }

    private static AbstractMenu createParentMenu(Player player, RailTypeDraft draft, RailBlockRole role) {
        if (role.isOverheadSetting())
            return new RailOverheadWireMenu(player, draft, false);

        return new RailTypeEditorMenu(player, draft, false);
    }

    private void playSound(Player player, Sound sound) {
        player.playSound(player, sound, 1.0F, 1.0F);
    }
}
